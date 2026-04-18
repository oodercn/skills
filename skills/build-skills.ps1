$ErrorActionPreference = "Continue"
$OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Skills Build Script" -ForegroundColor Cyan
Write-Host "  Package all skills to plugins directory" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$MAVEN_REPO = "D:\maven\.m2\repository"
$PLUGINS_DIR = Join-Path $PSScriptRoot "..\..\plugins"
$DEV_DIR = $PSScriptRoot

Write-Host "Maven Repo: $MAVEN_REPO"
Write-Host "Plugins Dir: $PLUGINS_DIR"
Write-Host ""

if (-not (Test-Path $PLUGINS_DIR)) {
    New-Item -ItemType Directory -Path $PLUGINS_DIR -Force | Out-Null
}

function Build-Skill {
    param($skillPath)
    
    $pomFile = Join-Path $skillPath "pom.xml"
    if (-not (Test-Path $pomFile)) {
        return
    }
    
    $skillName = Split-Path $skillPath -Leaf
    Write-Host "  Building: $skillName" -ForegroundColor Yellow
    
    Push-Location $skillPath
    try {
        $env:MAVEN_OPTS = "--add-opens java.base/java.lang=ALL-UNNAMED"
        mvn clean package "-Dmaven.repo.local=$MAVEN_REPO" -DskipTests -q 2>$null
        
        $jarFiles = Get-ChildItem -Path "target" -Filter "*.jar" -ErrorAction SilentlyContinue
        foreach ($jar in $jarFiles) {
            Copy-Item $jar.FullName $PLUGINS_DIR -Force
            Write-Host "    - Copied: $($jar.Name)" -ForegroundColor Green
        }
    }
    catch {
        Write-Host "    - Failed: $_" -ForegroundColor Red
    }
    finally {
        Pop-Location
    }
}

Write-Host "[1/3] Building _system skills..." -ForegroundColor Cyan
$systemSkills = Get-ChildItem -Path (Join-Path $DEV_DIR "_system") -Directory -Filter "skill-*"
foreach ($skill in $systemSkills) {
    Build-Skill $skill.FullName
}

Write-Host ""
Write-Host "[2/3] Building _business skills..." -ForegroundColor Cyan
$businessSkills = Get-ChildItem -Path (Join-Path $DEV_DIR "_business") -Directory -Filter "skill-*"
foreach ($skill in $businessSkills) {
    Build-Skill $skill.FullName
}

Write-Host ""
Write-Host "[3/3] Building _drivers skills..." -ForegroundColor Cyan
$driverSkills = Get-ChildItem -Path (Join-Path $DEV_DIR "_drivers") -Directory -Recurse | Where-Object { $_.Name -like "skill-*" }
foreach ($skill in $driverSkills) {
    Build-Skill $skill.FullName
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Build Complete!" -ForegroundColor Green
Write-Host "  Plugins Dir: $PLUGINS_DIR" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$jarFiles = Get-ChildItem -Path $PLUGINS_DIR -Filter "*.jar"
Write-Host "Packaged JAR files:" -ForegroundColor Yellow
$jarFiles | ForEach-Object { Write-Host "  $($_.Name)" }

Write-Host ""
Write-Host "Total: $($jarFiles.Count) JAR files" -ForegroundColor Green
