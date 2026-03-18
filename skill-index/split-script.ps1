# skill-index-split.ps1
# 自动拆分 skill-index.yaml 到多个文件

$sourceFile = "e:\github\ooder-skills\skill-index.yaml"
$targetDir = "e:\github\ooder-skills\skill-index"

# 读取源文件
$content = Get-Content $sourceFile -Raw

# 使用 YamlDotNet 解析 YAML
Add-Type -Path "e:\github\ooder-skills\mvp\target\dependency\YamlDotNet.dll" -ErrorAction SilentlyContinue

# 手动解析 - 提取 skills 部分
$lines = Get-Content $sourceFile
$inSkills = $false
$inScenes = $false
$skillsByCategory = @{}
$scenesByCategory = @{}
$currentSkill = @()
$currentScene = @()
$currentCategory = ""

foreach ($line in $lines) {
    if ($line -match "^  skills:") {
        $inSkills = $true
        $inScenes = $false
        continue
    }
    if ($line -match "^  scenes:") {
        $inSkills = $false
        $inScenes = $true
        continue
    }
    
    if ($inSkills) {
        if ($line -match "^    - id: ") {
            if ($currentSkill.Count -gt 0 -and $currentCategory) {
                if (-not $skillsByCategory.ContainsKey($currentCategory)) {
                    $skillsByCategory[$currentCategory] = @()
                }
                $skillsByCategory[$currentCategory] += ,@($currentSkill)
            }
            $currentSkill = @($line)
        } elseif ($line -match "^      capabilityCategory: (.+)") {
            $currentCategory = $Matches[1].Trim()
            $currentSkill += $line
        } else {
            if ($currentSkill.Count -gt 0) {
                $currentSkill += $line
            }
        }
    }
    
    if ($inScenes) {
        if ($line -match "^    - sceneId: ") {
            if ($currentScene.Count -gt 0 -and $currentCategory) {
                if (-not $scenesByCategory.ContainsKey($currentCategory)) {
                    $scenesByCategory[$currentCategory] = @()
                }
                $scenesByCategory[$currentCategory] += ,@($currentScene)
            }
            $currentScene = @($line)
        } elseif ($line -match "^      capabilityCategory: (.+)") {
            $currentCategory = $Matches[1].Trim()
            $currentScene += $line
        } else {
            if ($currentScene.Count -gt 0) {
                $currentScene += $line
            }
        }
    }
}

# 保存最后一个
if ($currentSkill.Count -gt 0 -and $currentCategory) {
    if (-not $skillsByCategory.ContainsKey($currentCategory)) {
        $skillsByCategory[$currentCategory] = @()
    }
    $skillsByCategory[$currentCategory] += ,@($currentSkill)
}
if ($currentScene.Count -gt 0 -and $currentCategory) {
    if (-not $scenesByCategory.ContainsKey($currentCategory)) {
        $scenesByCategory[$currentCategory] = @()
    }
    $scenesByCategory[$currentCategory] += ,@($currentScene)
}

# 输出统计
Write-Host "Skills by category:"
foreach ($cat in $skillsByCategory.Keys) {
    Write-Host "  $cat : $($skillsByCategory[$cat].Count) skills"
}

Write-Host "`nScenes by category:"
foreach ($cat in $scenesByCategory.Keys) {
    Write-Host "  $cat : $($scenesByCategory[$cat].Count) scenes"
}
