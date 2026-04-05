<#
.SYNOPSIS
    Encoding validation script - Prevents corrupted files from being committed
.DESCRIPTION
    Checks for:
    1. BOM (Byte Order Mark) characters in UTF-8 files
    2. Corrupted Chinese characters (replacement character U+FFFD)
    3. Invalid UTF-8 sequences
.EXAMPLE
    .\check-encoding.ps1 -Path "src/main/java"
    .\check-encoding.ps1 -Path "." -Extensions @("*.java", "*.xml", "*.yaml")
#>

param(
    [Parameter(Mandatory=$false)]
    [string]$Path = ".",
    
    [Parameter(Mandatory=$false)]
    [string[]]$Extensions = @("*.java", "*.xml", "*.yaml", "*.yml", "*.properties", "*.json"),
    
    [Parameter(Mandatory=$false)]
    [switch]$FailOnError = $true
)

$ErrorActionPreference = "Continue"
$exitCode = 0
$issues = @()

Write-Host "=== Encoding Validation Check ===" -ForegroundColor Cyan
Write-Host "Scanning path: $Path"
Write-Host "Extensions: $($Extensions -join ', ')"
Write-Host ""

foreach ($ext in $Extensions) {
    $files = Get-ChildItem -Path $Path -Filter $ext -Recurse -File -ErrorAction SilentlyContinue
    
    foreach ($file in $files) {
        $relativePath = $file.FullName.Replace((Get-Location).Path, "").TrimStart('\', '/')
        $hasIssue = $false
        
        $bytes = [System.IO.File]::ReadAllBytes($file.FullName)
        
        if ($bytes.Length -ge 3) {
            if ($bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
                $issues += "[BOM] $relativePath - Contains UTF-8 BOM (should be UTF-8 without BOM)"
                $hasIssue = $true
            }
        }
        
        try {
            $content = [System.IO.File]::ReadAllText($file.FullName, [System.Text.Encoding]::UTF8)
            
            if ($content -match '\ufffd') {
                $issues += "[CORRUPT] $relativePath - Contains corrupted characters (U+FFFD replacement character)"
                $hasIssue = $true
            }
            
            if ($content -match '\ufeff') {
                $issues += "[BOM-CHAR] $relativePath - Contains BOM character in text content"
                $hasIssue = $true
            }
        }
        catch {
            $issues += "[ENCODING] $relativePath - Failed to read as UTF-8: $($_.Exception.Message)"
            $hasIssue = $true
        }
        
        if (-not $hasIssue) {
            Write-Host "  [OK] $relativePath" -ForegroundColor Green
        }
    }
}

Write-Host ""
if ($issues.Count -gt 0) {
    Write-Host "=== Encoding Issues Found ===" -ForegroundColor Red
    foreach ($issue in $issues) {
        Write-Host "  $issue" -ForegroundColor Red
    }
    Write-Host ""
    Write-Host "Total issues: $($issues.Count)" -ForegroundColor Red
    Write-Host ""
    Write-Host "REMEDIATION:" -ForegroundColor Yellow
    Write-Host "  1. Remove BOM: Open file in VS Code, click encoding in status bar, select 'Save with Encoding' -> 'UTF-8'"
    Write-Host "  2. Fix corrupted text: Re-enter the Chinese characters manually"
    Write-Host "  3. Ensure .editorconfig is present and IDE respects it"
    
    if ($FailOnError) {
        $exitCode = 1
    }
}
else {
    Write-Host "=== All files passed encoding check ===" -ForegroundColor Green
}

exit $exitCode
