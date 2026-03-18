# split-scenes.ps1
$sourceFile = "e:\github\ooder-skills\skill-index.yaml"
$targetDir = "e:\github\ooder-skills\skill-index\scenes"

$content = Get-Content $sourceFile -Raw
$scenesMatch = [regex]::Match($content, "(?s)(?<=  scenes:\r?\n)(.+)$")

if ($scenesMatch.Success) {
    $scenesSection = $scenesMatch.Groups[1].Value
    $lines = $scenesSection -split "`n"
    
    $scenesByCategory = @{}
    $currentScene = @()
    $currentCategory = ""
    $sceneCount = 0
    
    foreach ($line in $lines) {
        if ($line -match "^    - sceneId: ") {
            if ($currentScene.Count -gt 0 -and $currentCategory -ne "") {
                if (-not $scenesByCategory.ContainsKey($currentCategory)) {
                    $scenesByCategory[$currentCategory] = @()
                }
                $scenesByCategory[$currentCategory] += ,@($currentScene)
                $sceneCount++
            }
            $currentScene = @($line)
        }
        elseif ($line -match "^      capabilityCategory: (.+)") {
            $currentCategory = $Matches[1].Trim()
            $currentScene += $line
        }
        else {
            if ($currentScene.Count -gt 0) {
                $currentScene += $line
            }
        }
    }
    
    if ($currentScene.Count -gt 0 -and $currentCategory -ne "") {
        if (-not $scenesByCategory.ContainsKey($currentCategory)) {
            $scenesByCategory[$currentCategory] = @()
        }
        $scenesByCategory[$currentCategory] += ,@($currentScene)
        $sceneCount++
    }
    
    Write-Host "Total scenes: $sceneCount"
    Write-Host "Categories: $($scenesByCategory.Keys.Count)"
    
    foreach ($cat in $scenesByCategory.Keys) {
        $scenes = $scenesByCategory[$cat]
        $fileName = "$targetDir\$cat-scenes.yaml"
        $header = "apiVersion: ooder.io/v1`nkind: Scenes`nmetadata:`n  name: $cat-scenes`n  category: $cat`n  count: $($scenes.Count)`n`nscenes:"
        $header | Out-File $fileName -Encoding UTF8
        foreach ($scene in $scenes) {
            "" | Out-File $fileName -Encoding UTF8 -Append
            $scene | Out-File $fileName -Encoding UTF8 -Append
        }
        Write-Host "Created: $cat-scenes.yaml ($($scenes.Count) scenes)"
    }
    
    Write-Host "Done!"
}
else {
    Write-Host "Scenes section not found"
}
