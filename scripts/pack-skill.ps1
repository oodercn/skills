#!/usr/bin/env pwsh
<#
.SYNOPSIS
    打包单个技能为独立的.zip文件

.DESCRIPTION
    将指定技能目录打包成可分发的.zip文件，包含所有必要文件：
    - skill.yaml (必需)
    - target/*.jar (如果存在)
    - README.md (如果存在)
    - config/ (如果存在)

.PARAMETER SkillId
    技能ID，如 skill-network, skill-llm-chat

.PARAMETER Version
    版本号，如 2.3.1

.PARAMETER OutputDir
    输出目录，默认为 releases/v{version}

.EXAMPLE
    .\pack-skill.ps1 -SkillId skill-network -Version 2.3.1
    .\pack-skill.ps1 -SkillId skill-llm-chat -Version 2.3.1 -OutputDir .\releases\v2.3.1
#>

param(
    [Parameter(Mandatory=$true)]
    [string]$SkillId,
    
    [Parameter(Mandatory=$true)]
    [string]$Version,
    
    [string]$OutputDir = "releases/v$Version"
)

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Ooder Skills - Single Skill Packer" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$skillDirs = @(
    "skills/_system/$SkillId",
    "skills/_drivers/*/$SkillId",
    "skills/capabilities/*/$SkillId",
    "skills/scenes/$SkillId",
    "skills/tools/$SkillId"
)

$skillPath = $null
foreach ($dir in $skillDirs) {
    $matches = Get-Item $dir -ErrorAction SilentlyContinue
    if ($matches) {
        $skillPath = $matches[0].FullName
        break
    }
}

if (-not $skillPath) {
    Write-Error "Skill not found: $SkillId"
    Write-Host "Searched paths:" -ForegroundColor Yellow
    foreach ($dir in $skillDirs) {
        Write-Host "  - $dir" -ForegroundColor Gray
    }
    exit 1
}

Write-Host "Found skill at: $skillPath" -ForegroundColor Green

$skillYaml = Join-Path $skillPath "skill.yaml"
if (-not (Test-Path $skillYaml)) {
    Write-Error "skill.yaml not found in $skillPath"
    exit 1
}

$skillYamlContent = Get-Content $skillYaml -Raw
if ($skillYamlContent -match 'id:\s*[''"]?([a-zA-Z0-9-]+)') {
    $actualSkillId = $Matches[1]
    Write-Host "Skill ID: $actualSkillId" -ForegroundColor Gray
}

New-Item -ItemType Directory -Force -Path $OutputDir | Out-Null

$tempDir = Join-Path $env:TEMP "skill-pack-$SkillId-$Version"
if (Test-Path $tempDir) {
    Remove-Item -Recurse -Force $tempDir
}
New-Item -ItemType Directory -Force -Path $tempDir | Out-Null

Write-Host "Copying files..." -ForegroundColor Yellow

Copy-Item $skillYaml $tempDir

$jarFiles = Get-ChildItem -Path "$skillPath/target/*.jar" -ErrorAction SilentlyContinue
if ($jarFiles) {
    $jarDir = Join-Path $tempDir "lib"
    New-Item -ItemType Directory -Force -Path $jarDir | Out-Null
    foreach ($jar in $jarFiles) {
        Copy-Item $jar.FullName $jarDir
        Write-Host "  + lib/$($jar.Name)" -ForegroundColor Gray
    }
}

if (Test-Path "$skillPath/README.md") {
    Copy-Item "$skillPath/README.md" $tempDir
    Write-Host "  + README.md" -ForegroundColor Gray
}

if (Test-Path "$skillPath/config") {
    Copy-Item -Recurse "$skillPath/config" $tempDir
    Write-Host "  + config/" -ForegroundColor Gray
}

$zipName = "$SkillId-$Version.zip"
$zipPath = Join-Path $OutputDir $zipName

Write-Host ""
Write-Host "Creating package: $zipName" -ForegroundColor Yellow

Compress-Archive -Path "$tempDir/*" -DestinationPath $zipPath -Force

Remove-Item -Recurse -Force $tempDir

$fileSize = (Get-Item $zipPath).Length / 1KB
Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Package created successfully!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Path: $zipPath" -ForegroundColor White
Write-Host "  Size: $([math]::Round($fileSize, 2)) KB" -ForegroundColor White
Write-Host ""

return $zipPath
