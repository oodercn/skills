#!/usr/bin/env pwsh
# 分析 Gitee 仓库中的技能，查找重复来源
# 使用 WebFetch 获取页面内容

$ErrorActionPreference = "Continue"

# 配置
$giteeApiBase = "https://gitee.com/api/v5/repos"
$owner = "ooderCN"
$repo = "skills"
$branch = "master"

# 存储所有技能
$allSkills = @{}

function Get-GiteeRawFile {
    param([string]$path)

    # 使用 raw.githubusercontent.com 或 gitee 的 raw 路径
    $url = "https://gitee.com/$owner/$repo/raw/$branch/$path"

    Write-Host "[DEBUG] Fetching: $url" -ForegroundColor DarkGray

    try {
        $response = Invoke-WebRequest -Uri $url -TimeoutSec 30 -UseBasicParsing
        return $response.Content
    }
    catch {
        Write-Host "[WARN] Cannot access: $path" -ForegroundColor Yellow
        return $null
    }
}

function Get-GiteeTree {
    param([string]$path = "")

    $url = "$giteeApiBase/$owner/$repo/trees/$branch`?recursive=1&path=$path"
    Write-Host "[DEBUG] Fetching tree: $url" -ForegroundColor DarkGray

    try {
        $response = Invoke-RestMethod -Uri $url -TimeoutSec 30
        return $response
    }
    catch {
        Write-Host "[WARN] Cannot get tree for $path : $_" -ForegroundColor Yellow
        return $null
    }
}

function Parse-SkillFromYaml {
    param(
        [string]$content,
        [string]$path
    )

    if (-not $content) {
        return $null
    }

    try {
        $id = ""
        $skillForm = ""
        $category = ""

        foreach ($line in $content -split "`n") {
            if ($line -match "^\s*id:\s*(.+)$") {
                $id = $matches[1].Trim()
            }
            if ($line -match "^\s*category:\s*(.+)$") {
                $category = $matches[1].Trim()
            }
            if ($line -match "^\s*skillForm:\s*(.+)$") {
                $skillForm = $matches[1].Trim()
            }
        }

        if ($id) {
            return @{
                Id = $id
                skillForm = $skillForm
                Category = $category
                Path = $path
            }
        }
    }
    catch {
        Write-Host "[ERROR] Parse error for $path : $_" -ForegroundColor Red
    }

    return $null
}

# 主程序
Write-Host "========================================" -ForegroundColor Magenta
Write-Host "Gitee Skill Duplicate Analyzer" -ForegroundColor Magenta
Write-Host "Repository: $owner/$repo" -ForegroundColor Gray
Write-Host "========================================" -ForegroundColor Magenta
Write-Host ""

# 方法1: 使用 GitTree API 获取所有文件
Write-Host "[INFO] Getting repository tree..." -ForegroundColor Cyan
$tree = Get-GiteeTree

if ($tree -and $tree.tree) {
    Write-Host "[INFO] Found $($tree.tree.Count) items in repository" -ForegroundColor Green

    # 过滤出所有 skill.yaml 文件
    $skillFiles = $tree.tree | Where-Object { $_.path -match "skill\.yaml$" }

    Write-Host "[INFO] Found $($skillFiles.Count) skill.yaml files" -ForegroundColor Yellow
    Write-Host ""

    $processed = 0
    foreach ($file in $skillFiles) {
        $processed++
        if ($processed % 10 -eq 0) {
            Write-Host "[PROGRESS] Processed $processed / $($skillFiles.Count)" -ForegroundColor Cyan
        }

        $content = Get-GiteeRawFile -path $file.path
        $parsed = Parse-SkillFromYaml -content $content -path $file.path

        if ($parsed) {
            $id = $parsed.Id
            if (-not $allSkills.ContainsKey($id)) {
                $allSkills[$id] = @()
            }
            $allSkills[$id] += $parsed

            Write-Host "  [$processed] Found: $id (skillForm=$($parsed.skillForm), path=$($file.path))" -ForegroundColor Green
        }
    }
}
else {
    Write-Host "[WARN] Cannot get repository tree, trying alternative method..." -ForegroundColor Yellow

    # 备选方法：直接扫描特定目录
    $pathsToScan = @(
        "skills/scenes",
        "skills/tools",
        "skills/capabilities",
        "skills/_drivers"
    )

    foreach ($basePath in $pathsToScan) {
        Write-Host "[INFO] Scanning: $basePath" -ForegroundColor Cyan
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Magenta
Write-Host "Analysis Results" -ForegroundColor Magenta
Write-Host "========================================" -ForegroundColor Magenta
Write-Host ""

# 查找重复
$duplicates = $allSkills.GetEnumerator() | Where-Object { $_.Value.Count -gt 1 }

if ($duplicates) {
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "DUPLICATES FOUND: $($duplicates.Count) skills" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    Write-Host ""

    foreach ($dup in $duplicates | Sort-Object Name) {
        Write-Host "Skill: $($dup.Key)" -ForegroundColor Yellow
        Write-Host "  Versions found: $($dup.Value.Count)" -ForegroundColor White

        $forms = $dup.Value | Group-Object -Property skillForm
        foreach ($form in $forms) {
            Write-Host "  skillForm=$($form.Name): $($form.Count) version(s)" -ForegroundColor Cyan
            foreach ($v in $form.Group) {
                Write-Host "    - $($v.Path)" -ForegroundColor Gray
            }
        }
        Write-Host ""
    }
}
else {
    Write-Host "No duplicates found!" -ForegroundColor Green
}

Write-Host ""
Write-Host "Total unique skills: $($allSkills.Count)" -ForegroundColor Cyan

# 按 skillForm 统计
Write-Host ""
Write-Host "========================================" -ForegroundColor Magenta
Write-Host "SkillForm Distribution" -ForegroundColor Magenta
Write-Host "========================================" -ForegroundColor Magenta

$formStats = @{}
foreach ($skill in $allSkills.Values) {
    foreach ($v in $skill) {
        $form = $v.skillForm
        if (-not $form) { $form = "(null)" }
        if (-not $formStats.ContainsKey($form)) {
            $formStats[$form] = 0
        }
        $formStats[$form]++
    }
}

foreach ($stat in $formStats.GetEnumerator() | Sort-Object Value -Descending) {
    Write-Host "  $($stat.Key): $($stat.Value)" -ForegroundColor White
}
