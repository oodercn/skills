# BPM扩展属性API测试脚本 - 简化版
$BaseUrl = "http://localhost:8080"
$TestResults = @()

function Add-TestResult {
    param($TestName, $Passed, $Message)
    $script:TestResults += [PSCustomObject]@{
        TestName = $TestName
        Passed = $Passed
        Message = $Message
    }
    $status = if ($Passed) { "✓ PASS" } else { "✗ FAIL" }
    $color = if ($Passed) { "Green" } else { "Red" }
    Write-Host "$status - $TestName : $Message" -ForegroundColor $color
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "BPM扩展属性API测试" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# 测试1: API连接
Write-Host "`n=== 测试API连接 ===" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$BaseUrl/actuator/health" -Method GET -TimeoutSec 5
    Add-TestResult -TestName "API连接测试" -Passed $true -Message "服务运行正常"
} catch {
    Add-TestResult -TestName "API连接测试" -Passed $false -Message "无法连接到服务: $_"
    Write-Host "`n无法连接到服务，测试中止。" -ForegroundColor Red
    exit 1
}

# 测试2: 获取流程定义列表
Write-Host "`n=== 测试获取流程定义列表 ===" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$BaseUrl/api/process-definitions" -Method GET -TimeoutSec 10
    if ($response -and $response.Count -gt 0) {
        Add-TestResult -TestName "获取流程定义列表" -Passed $true -Message "找到 $($response.Count) 个流程定义"
        $processDefId = $response[0].processDefId
        $versionId = $response[0].activeVersion.processDefVersionId
    } else {
        Add-TestResult -TestName "获取流程定义列表" -Passed $false -Message "未找到流程定义"
        $processDefId = $null
    }
} catch {
    Add-TestResult -TestName "获取流程定义列表" -Passed $false -Message "请求失败: $_"
    $processDefId = $null
}

# 测试3: 获取流程定义详情
if ($processDefId) {
    Write-Host "`n=== 测试获取流程定义详情 ===" -ForegroundColor Cyan
    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl/api/process-definitions/$processDefId" -Method GET -TimeoutSec 10
        Add-TestResult -TestName "获取流程定义详情" -Passed $true -Message "流程名称: $($response.name)"
        Add-TestResult -TestName "属性检查: processDefId" -Passed ($response.processDefId -ne $null) -Message $response.processDefId
        Add-TestResult -TestName "属性检查: name" -Passed ($response.name -ne $null) -Message $response.name
    } catch {
        Add-TestResult -TestName "获取流程定义详情" -Passed $false -Message "请求失败: $_"
    }
}

# 测试4: 获取活动列表
if ($processDefId -and $versionId) {
    Write-Host "`n=== 测试获取活动列表 ===" -ForegroundColor Cyan
    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl/api/process-definitions/$processDefId/versions/$versionId/activities" -Method GET -TimeoutSec 10
        if ($response -and $response.Count -gt 0) {
            Add-TestResult -TestName "获取活动列表" -Passed $true -Message "找到 $($response.Count) 个活动"
            $firstActivity = $response[0]
            Add-TestResult -TestName "活动属性: activityDefId" -Passed ($firstActivity.activityDefId -ne $null) -Message $firstActivity.activityDefId
            Add-TestResult -TestName "活动属性: name" -Passed ($firstActivity.name -ne $null) -Message $firstActivity.name
            Add-TestResult -TestName "活动属性: position" -Passed ($firstActivity.position -ne $null) -Message $firstActivity.position
        } else {
            Add-TestResult -TestName "获取活动列表" -Passed $false -Message "未找到活动"
        }
    } catch {
        Add-TestResult -TestName "获取活动列表" -Passed $false -Message "请求失败: $_"
    }
}

# 测试5: 获取路由列表
if ($processDefId -and $versionId) {
    Write-Host "`n=== 测试获取路由列表 ===" -ForegroundColor Cyan
    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl/api/process-definitions/$processDefId/versions/$versionId/routes" -Method GET -TimeoutSec 10
        if ($response -and $response.Count -gt 0) {
            Add-TestResult -TestName "获取路由列表" -Passed $true -Message "找到 $($response.Count) 个路由"
        } else {
            Add-TestResult -TestName "获取路由列表" -Passed $false -Message "未找到路由"
        }
    } catch {
        Add-TestResult -TestName "获取路由列表" -Passed $false -Message "请求失败: $_"
    }
}

# 测试6: 检查扩展属性
if ($processDefId -and $versionId) {
    Write-Host "`n=== 测试扩展属性 ===" -ForegroundColor Cyan
    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl/api/process-definitions/$processDefId/versions/$versionId" -Method GET -TimeoutSec 10
        
        # 检查关键扩展属性
        Add-TestResult -TestName "扩展属性: startNode" -Passed ($response.startNode -ne $null) -Message $(if ($response.startNode) { "存在" } else { "缺失" })
        Add-TestResult -TestName "扩展属性: endNodes" -Passed ($response.endNodes -ne $null) -Message $(if ($response.endNodes) { "存在" } else { "缺失" })
        Add-TestResult -TestName "扩展属性: listeners" -Passed ($response.listeners -ne $null) -Message $(if ($response.listeners) { "存在" } else { "缺失" })
        Add-TestResult -TestName "扩展属性: rightGroups" -Passed ($response.rightGroups -ne $null) -Message $(if ($response.rightGroups) { "存在" } else { "缺失" })
    } catch {
        Add-TestResult -TestName "扩展属性测试" -Passed $false -Message "请求失败: $_"
    }
}

# 测试7: 保存流程定义
Write-Host "`n=== 测试保存流程定义 ===" -ForegroundColor Cyan
$testDataPath = "..\test-data\simple-approval-process.json"
if (Test-Path $testDataPath) {
    try {
        $processDef = Get-Content $testDataPath -Raw | ConvertFrom-Json
        Add-TestResult -TestName "读取测试数据" -Passed $true -Message "流程名称: $($processDef.processDef.name)"
        
        $body = $processDef | ConvertTo-Json -Depth 10
        $response = Invoke-RestMethod -Uri "$BaseUrl/api/process-definitions" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 30
        
        if ($response -and $response.success) {
            Add-TestResult -TestName "保存流程定义" -Passed $true -Message "流程ID: $($response.processDefId)"
        } else {
            Add-TestResult -TestName "保存流程定义" -Passed $false -Message "保存失败: $($response.message)"
        }
    } catch {
        Add-TestResult -TestName "保存流程定义" -Passed $false -Message "请求失败: $_"
    }
} else {
    Add-TestResult -TestName "读取测试数据" -Passed $false -Message "测试数据文件不存在"
}

# 显示测试总结
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "测试总结" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$total = $TestResults.Count
$passed = ($TestResults | Where-Object { $_.Passed }).Count
$failed = $total - $passed

Write-Host "总测试数: $total" -ForegroundColor White
Write-Host "通过: $passed" -ForegroundColor Green
Write-Host "失败: $failed" -ForegroundColor Red
Write-Host "通过率: $([math]::Round($passed / $total * 100, 2))%" -ForegroundColor Yellow

if ($failed -gt 0) {
    Write-Host "`n失败的测试:" -ForegroundColor Red
    $TestResults | Where-Object { -not $_.Passed } | ForEach-Object {
        Write-Host "  - $($_.TestName): $($_.Message)" -ForegroundColor Red
    }
}

# 保存测试结果
$resultsPath = "..\test-execution\test-results-$(Get-Date -Format 'yyyyMMdd-HHmmss').csv"
$TestResults | Export-Csv -Path $resultsPath -NoTypeInformation -Encoding UTF8
Write-Host "`n测试结果已保存到: $resultsPath" -ForegroundColor Green
