# MVP Core 启动验证脚本

param(
    [string]$Profile = "micro"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  MVP Core Startup Verification Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$ScriptPath = $PSScriptRoot
$MvpPath = Split-Path -Parent $ScriptPath
$RootPath = Split-Path -Parent $MvpPath

Write-Host "[1/5] Checking profile: $Profile" -ForegroundColor Yellow
$ProfilePath = Join-Path $MvpPath "profiles\$Profile.json"
if (Test-Path $ProfilePath) {
    Write-Host "  Profile file found: $ProfilePath" -ForegroundColor Green
} else {
    Write-Host "  Profile file not found, using default" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "[2/5] Checking core modules..." -ForegroundColor Yellow

$Modules = @(
    "skills\_system\skill-common",
    "skills\_system\skill-capability"
)

foreach ($Module in $Modules) {
    $ModulePath = Join-Path $RootPath $Module
    if (Test-Path $ModulePath) {
        Write-Host "  [OK] $Module" -ForegroundColor Green
    } else {
        Write-Host "  [MISSING] $Module" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "[3/5] Checking optional skills..." -ForegroundColor Yellow

$OptionalSkills = @(
    "skills\_drivers\llm\skill-llm-base",
    "skills\_drivers\llm\skill-llm-openai",
    "skills\capabilities\knowledge\skill-knowledge-base",
    "skills\capabilities\security\skill-audit"
)

foreach ($Skill in $OptionalSkills) {
    $SkillPath = Join-Path $RootPath $Skill
    if (Test-Path $SkillPath) {
        Write-Host "  [OK] $Skill" -ForegroundColor Green
    } else {
        Write-Host "  [SKIP] $Skill" -ForegroundColor Gray
    }
}

Write-Host ""
Write-Host "[4/5] Checking Java environment..." -ForegroundColor Yellow

try {
    $JavaVersion = java -version 2>&1 | Select-Object -First 1
    Write-Host "  Java: $JavaVersion" -ForegroundColor Green
} catch {
    Write-Host "  Java not found!" -ForegroundColor Red
}

Write-Host ""
Write-Host "[5/5] Checking Maven environment..." -ForegroundColor Yellow

try {
    $MavenVersion = mvn -version 2>&1 | Select-Object -First 1
    Write-Host "  Maven: $MavenVersion" -ForegroundColor Green
} catch {
    Write-Host "  Maven not found!" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Verification Complete!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Project Root: $RootPath" -ForegroundColor Gray
Write-Host "MVP Path: $MvpPath" -ForegroundColor Gray
Write-Host ""
Write-Host "To start MVP Core with profile '$Profile':" -ForegroundColor Yellow
Write-Host "  cd $MvpPath" -ForegroundColor White
Write-Host "  mvn spring-boot:run -Dspring-boot.run.profiles=$Profile" -ForegroundColor White
Write-Host ""
Write-Host "Or build and run:" -ForegroundColor Yellow
Write-Host "  mvn clean package -DskipTests" -ForegroundColor White
Write-Host "  java -jar target/mvp-core-2.3.jar --spring.profiles.active=$Profile" -ForegroundColor White
