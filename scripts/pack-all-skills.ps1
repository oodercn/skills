#!/usr/bin/env pwsh
<#
.SYNOPSIS
    批量打包所有技能为独立的.zip文件

.DESCRIPTION
    遍历所有技能目录，将每个技能打包成独立的.zip文件
    输出到 releases/v{version}/ 目录

.PARAMETER Version
    版本号，如 2.3.1

.PARAMETER OutputDir
    输出目录，默认为 releases/v{version}

.EXAMPLE
    .\pack-all-skills.ps1 -Version 2.3.1
#>

param(
    [Parameter(Mandatory=$true)]
    [string]$Version,
    
    [string]$OutputDir = "releases/v$Version"
)

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Ooder Skills - Batch Packer" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

New-Item -ItemType Directory -Force -Path $OutputDir | Out-Null

$skillYamlFiles = Get-ChildItem -Path "skills" -Recurse -Filter "skill.yaml"

$skills = @()
foreach ($file in $skillYamlFiles) {
    $content = Get-Content $file.FullName -Raw
    if ($content -match 'id:\s*[''"]?([a-zA-Z0-9-]+)') {
        $skillId = $Matches[1]
        $skills += @{
            Id = $skillId
            Path = $file.DirectoryName
            YamlFile = $file.FullName
        }
    }
}

Write-Host "Found $($skills.Count) skills to pack" -ForegroundColor Yellow
Write-Host ""

$success = 0
$failed = 0
$results = @()

foreach ($skill in $skills) {
    Write-Host "Packing: $($skill.Id)" -ForegroundColor Cyan
    
    try {
        $zipPath = & "$PSScriptRoot/pack-skill.ps1" -SkillId $skill.Id -Version $Version -OutputDir $OutputDir
        $success++
        $results += @{
            SkillId = $skill.Id
            Status = "Success"
            Path = $zipPath
        }
        Write-Host "  [OK] $zipPath" -ForegroundColor Green
    } catch {
        $failed++
        $results += @{
            SkillId = $skill.Id
            Status = "Failed"
            Error = $_.Exception.Message
        }
        Write-Host "  [FAILED] $($_.Exception.Message)" -ForegroundColor Red
    }
    Write-Host ""
}

$manifest = @{
    version = $Version
    createdAt = (Get-Date -Format "yyyy-MM-ddTHH:mm:ssZ")
    totalSkills = $skills.Count
    successCount = $success
    failedCount = $failed
    skills = $results
}

$manifestPath = Join-Path $OutputDir "manifest.json"
$manifest | ConvertTo-Json -Depth 10 | Out-File $manifestPath -Encoding UTF8

Write-Host "========================================" -ForegroundColor Green
Write-Host "  Packing Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Total: $($skills.Count)" -ForegroundColor White
Write-Host "  Success: $success" -ForegroundColor Green
Write-Host "  Failed: $failed" -ForegroundColor $(if ($failed -gt 0) { "Red" } else { "Green" })
Write-Host "  Output: $OutputDir" -ForegroundColor White
Write-Host "  Manifest: $manifestPath" -ForegroundColor White
Write-Host ""

return $results
