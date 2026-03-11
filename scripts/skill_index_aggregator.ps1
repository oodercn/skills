# Skill Index 聚合工具 (PowerShell版本)

param(
    [string]$SkillsDir = "E:\github\ooder-skills\skills",
    [string]$OutputPath = "E:\github\ooder-skills\skills\skill-index-aggregated.yaml"
)

Write-Host "Starting Skill Index Aggregation..." -ForegroundColor Cyan

# 查找所有 skill-index-entry.yaml 文件
$entryFiles = Get-ChildItem -Path $SkillsDir -Recurse -Filter "skill-index-entry.yaml"
Write-Host "Found $($entryFiles.Count) skill entries" -ForegroundColor Green

# 统计变量
$skills = @()
$ids = @{}
$errors = @()

foreach ($entryFile in $entryFiles) {
    $content = Get-Content -Path $entryFile.FullName -Raw -Encoding UTF8
    
    # 简单解析 YAML (提取关键字段)
    $id = ""
    $name = ""
    $version = ""
    $skillForm = ""
    $visibility = ""
    $category = ""
    $capabilityCategory = ""
    
    # 提取 metadata 部分
    if ($content -match 'id:\s*(.+)') {
        $id = $Matches[1].Trim().Trim('"').Trim("'")
    }
    if ($content -match 'name:\s*(.+)') {
        $name = $Matches[1].Trim().Trim('"').Trim("'")
    }
    if ($content -match 'version:\s*(.+)') {
        $version = $Matches[1].Trim().Trim('"').Trim("'")
    }
    if ($content -match 'skillForm:\s*(.+)') {
        $skillForm = $Matches[1].Trim().Trim('"').Trim("'")
    }
    if ($content -match 'visibility:\s*(.+)') {
        $visibility = $Matches[1].Trim().Trim('"').Trim("'")
    }
    if ($content -match 'category:\s*(.+)') {
        $category = $Matches[1].Trim().Trim('"').Trim("'")
    }
    if ($content -match 'capabilityCategory:\s*(.+)') {
        $capabilityCategory = $Matches[1].Trim().Trim('"').Trim("'")
    }
    
    # 验证必需字段
    $missingFields = @()
    if (-not $id) { $missingFields += "id" }
    if (-not $name) { $missingFields += "name" }
    if (-not $version) { $missingFields += "version" }
    if (-not $skillForm) { $missingFields += "skillForm" }
    if (-not $visibility) { $missingFields += "visibility" }
    
    if ($missingFields.Count -gt 0) {
        $errors += "$($entryFile.FullName): Missing fields: $($missingFields -join ', ')"
        continue
    }
    
    # 检查重复 ID
    if ($ids.ContainsKey($id)) {
        $errors += "$($entryFile.FullName): Duplicate skill ID: $id"
        continue
    }
    
    $ids[$id] = $true
    
    # 添加到技能列表
    $skills += [PSCustomObject]@{
        id = $id
        name = $name
        version = $version
        skillForm = $skillForm
        visibility = $visibility
        category = $category
        capabilityCategory = $capabilityCategory
        path = $entryFile.DirectoryName
    }
}

# 显示错误
if ($errors.Count -gt 0) {
    Write-Host "`nValidation errors found:" -ForegroundColor Red
    foreach ($err in $errors) {
        Write-Host "  $err" -ForegroundColor Red
    }
    exit 1
}

# 按 ID 排序
$skills = $skills | Sort-Object -Property id

Write-Host "Validated $($skills.Count) unique skills" -ForegroundColor Green

# 生成 YAML 文件
$yamlLines = @()
$yamlLines += "apiVersion: skill.ooder.net/v1"
$yamlLines += "kind: SkillIndex"
$yamlLines += ""
$yamlLines += "metadata:"
$yamlLines += "  version: ""2.3.1"""
$yamlLines += "  generatedAt: ""$(Get-Date -Format 'yyyy-MM-ddTHH:mm:ss')"""
$yamlLines += "  totalSkills: $($skills.Count)"
$yamlLines += ""
$yamlLines += "spec:"
$yamlLines += "  skills:"

foreach ($skill in $skills) {
    $yamlLines += ""
    $yamlLines += "    - id: $($skill.id)"
    $yamlLines += "      name: $($skill.name)"
    $yamlLines += "      version: ""$($skill.version)"""
    $yamlLines += "      skillForm: $($skill.skillForm)"
    $yamlLines += "      visibility: $($skill.visibility)"
    $yamlLines += "      category: $($skill.category)"
    $yamlLines += "      capabilityCategory: $($skill.capabilityCategory)"
}

# 写入输出文件
$yamlLines -join "`n" | Out-File -FilePath $OutputPath -Encoding UTF8 -NoNewline
Write-Host "Generated skill-index.yaml at: $OutputPath" -ForegroundColor Green

# 统计信息
Write-Host "`nStatistics:" -ForegroundColor Cyan
Write-Host "  Total skills: $($skills.Count)"

# 按 skillForm 统计
$formCounts = $skills | Group-Object -Property skillForm
Write-Host "  By skillForm:"
foreach ($group in $formCounts) {
    Write-Host "    - $($group.Name): $($group.Count)"
}

# 按 category 统计
$catCounts = $skills | Group-Object -Property category
Write-Host "  By category:"
foreach ($group in $catCounts) {
    Write-Host "    - $($group.Name): $($group.Count)"
}

# 按 visibility 统计
$visCounts = $skills | Group-Object -Property visibility
Write-Host "  By visibility:"
foreach ($group in $visCounts) {
    Write-Host "    - $($group.Name): $($group.Count)"
}

Write-Host "`nAggregation completed successfully!" -ForegroundColor Green
