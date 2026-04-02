#!/usr/bin/env pwsh
$skillsToCheck = @("skill-approval-form", "skill-real-estate-form", "skill-recruitment-management")
$giteeBase = "https://gitee.com/ooderCN/skills/raw/master"

Write-Host "Checking Gitee for duplicate skills..." -ForegroundColor Cyan

foreach ($skillId in $skillsToCheck) {
    Write-Host "========================================" -ForegroundColor Yellow
    Write-Host "Checking: $skillId" -ForegroundColor Yellow

    $possiblePaths = @(
        "skills/scenes/$skillId/skill.yaml",
        "skills/tools/$skillId/skill.yaml",
        "skills/biz/$skillId/skill.yaml"
    )

    foreach ($path in $possiblePaths) {
        $url = "$giteeBase/$path"
        try {
            $content = Invoke-WebRequest -Uri $url -TimeoutSec 10 -UseBasicParsing -ErrorAction SilentlyContinue
            if ($content) {
                Write-Host "  FOUND: $path" -ForegroundColor Green
                $skillForm = ""
                foreach ($line in ($content.Content -split "`n")) {
                    if ($line -match "skillForm:\s*(.+)") { $skillForm = $matches[1].Trim() }
                }
                Write-Host "    skillForm: $skillForm" -ForegroundColor $(if ($skillForm -eq "SCENE") { "Green" } else { "Red" })
            }
        }
        catch { }
    }
    Write-Host ""
}
