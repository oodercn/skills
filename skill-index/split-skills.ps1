# split-skills-by-category.ps1
$sourceFile = "e:\github\ooder-skills\skill-index.yaml"
$targetDir = "e:\github\ooder-skills\skill-index\skills"

$lines = Get-Content $sourceFile
$inSkills = $false
$skillsByCategory = @{}
$currentSkill = @()
$currentCategory = ""
$skillCount = 0

foreach ($line in $lines) {
    if ($line -match "^  skills:") {
        $inSkills = $true
        continue
    }
    if ($line -match "^  scenes:") {
        $inSkills = $false
        if ($currentSkill.Count -gt 0 -and $currentCategory -ne "") {
            if (-not $skillsByCategory.ContainsKey($currentCategory)) {
                $skillsByCategory[$currentCategory] = @()
            }
            $skillsByCategory[$currentCategory] += ,@($currentSkill)
            $skillCount++
        }
        continue
    }
    
    if ($inSkills) {
        if ($line -match "^    - id: ") {
            if ($currentSkill.Count -gt 0 -and $currentCategory -ne "") {
                if (-not $skillsByCategory.ContainsKey($currentCategory)) {
                    $skillsByCategory[$currentCategory] = @()
                }
                $skillsByCategory[$currentCategory] += ,@($currentSkill)
                $skillCount++
            }
            $currentSkill = @($line)
        }
        elseif ($line -match "^      capabilityCategory: (.+)") {
            $currentCategory = $Matches[1].Trim()
            $currentSkill += $line
        }
        else {
            if ($currentSkill.Count -gt 0) {
                $currentSkill += $line
            }
        }
    }
}

Write-Host "Total skills: $skillCount"
Write-Host "Categories: $($skillsByCategory.Keys.Count)"

foreach ($cat in $skillsByCategory.Keys) {
    $skills = $skillsByCategory[$cat]
    $fileName = "$targetDir\$cat.yaml"
    $header = "apiVersion: ooder.io/v1`nkind: Skills`nmetadata:`n  name: $cat-skills`n  category: $cat`n  count: $($skills.Count)`n`nskills:"
    $header | Out-File $fileName -Encoding UTF8
    foreach ($skill in $skills) {
        "" | Out-File $fileName -Encoding UTF8 -Append
        $skill | Out-File $fileName -Encoding UTF8 -Append
    }
    Write-Host "Created: $cat.yaml ($($skills.Count) skills)"
}

Write-Host "Done!"
