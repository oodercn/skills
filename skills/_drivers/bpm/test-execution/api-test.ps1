# BPM扩展属性API测试脚本
# 测试服务端API是否能正确处理和返回所有扩展属性

$BaseUrl = "http://localhost:8080"
$TestResults = @()

function Write-TestResult {
    param(
        [string]$TestName,
        [bool]$Passed,
        [string]$Message
    )
    $status = if ($Passed) { "✓ PASS" } else { "✗ FAIL" }
    Write-Host "$status - $TestName : $Message" -ForegroundColor $(if ($Passed) { "Green" } else { "Red" })
    $script:TestResults += [PSCustomObject]@{
        TestName = $TestName
        Passed = $Passed
        Message = $Message
    }
}

function Test-APIConnection {
    Write-Host "`n=== 测试API连接 ===" -ForegroundColor Cyan
    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl/actuator/health" -Method GET -TimeoutSec 5
        Write-TestResult -TestName "API连接测试" -Passed $true -Message "服务运行正常"
        return $true
    } catch {
        Write-TestResult -TestName "API连接测试" -Passed $false -Message "无法连接到服务: $_"
        return $false
    }
}

function Test-GetProcessDefs {
    Write-Host "`n=== 测试获取流程定义列表 ===" -ForegroundColor Cyan
    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl/api/process-definitions" -Method GET -TimeoutSec 10
        if ($response -and $response.Count -gt 0) {
            Write-TestResult -TestName "获取流程定义列表" -Passed $true -Message "找到 $($response.Count) 个流程定义"
            return $response
        } else {
            Write-TestResult -TestName "获取流程定义列表" -Passed $false -Message "未找到流程定义"
            return $null
        }
    } catch {
        Write-TestResult -TestName "获取流程定义列表" -Passed $false -Message "请求失败: $_"
        return $null
    }
}

function Test-GetProcessDefDetail {
    param([string]$ProcessDefId)
    Write-Host "`n=== 测试获取流程定义详情: $ProcessDefId ===" -ForegroundColor Cyan
    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl/api/process-definitions/$ProcessDefId" -Method GET -TimeoutSec 10
        if ($response) {
            Write-TestResult -TestName "获取流程定义详情" -Passed $true -Message "流程名称: $($response.name)"
            
            # 检查关键属性
            $checks = @(
                @{ Name = "processDefId"; Value = $response.processDefId }
                @{ Name = "name"; Value = $response.name }
                @{ Name = "version"; Value = $response.version }
            )
            
            foreach ($check in $checks) {
                if ($check.Value) {
                    Write-TestResult -TestName "属性检查: $($check.Name)" -Passed $true -Message $check.Value
                } else {
                    Write-TestResult -TestName "属性检查: $($check.Name)" -Passed $false -Message "属性缺失"
                }
            }
            
            return $response
        } else {
            Write-TestResult -TestName "获取流程定义详情" -Passed $false -Message "返回数据为空"
            return $null
        }
    } catch {
        Write-TestResult -TestName "获取流程定义详情" -Passed $false -Message "请求失败: $_"
        return $null
    }
}

function Test-GetActivities {
    param([string]$ProcessDefId, [string]$VersionId)
    Write-Host "`n=== 测试获取活动列表: $ProcessDefId ===" -ForegroundColor Cyan
    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl/api/process-definitions/$ProcessDefId/versions/$VersionId/activities" -Method GET -TimeoutSec 10
        if ($response -and $response.Count -gt 0) {
            Write-TestResult -TestName "获取活动列表" -Passed $true -Message "找到 $($response.Count) 个活动"
            
            # 检查第一个活动的属性
            $firstActivity = $response[0]
            $activityChecks = @(
                @{ Name = "activityDefId"; Value = $firstActivity.activityDefId }
                @{ Name = "name"; Value = $firstActivity.name }
                @{ Name = "position"; Value = $firstActivity.position }
            )
            
            foreach ($check in $activityChecks) {
                if ($check.Value) {
                    Write-TestResult -TestName "活动属性: $($check.Name)" -Passed $true -Message $check.Value
                } else {
                    Write-TestResult -TestName "活动属性: $($check.Name)" -Passed $false -Message "属性缺失"
                }
            }
            
            return $response
        } else {
            Write-TestResult -TestName "获取活动列表" -Passed $false -Message "未找到活动"
            return $null
        }
    } catch {
        Write-TestResult -TestName "获取活动列表" -Passed $false -Message "请求失败: $_"
        return $null
    }
}

function Test-GetRoutes {
    param([string]$ProcessDefId, [string]$VersionId)
    Write-Host "`n=== 测试获取路由列表: $ProcessDefId ===" -ForegroundColor Cyan
    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl/api/process-definitions/$ProcessDefId/versions/$VersionId/routes" -Method GET -TimeoutSec 10
        if ($response -and $response.Count -gt 0) {
            Write-TestResult -TestName "获取路由列表" -Passed $true -Message "找到 $($response.Count) 个路由"
            return $response
        } else {
            Write-TestResult -TestName "获取路由列表" -Passed $false -Message "未找到路由"
            return $null
        }
    } catch {
        Write-TestResult -TestName "获取路由列表" -Passed $false -Message "请求失败: $_"
        return $null
    }
}

function Test-SaveProcessDef {
    Write-Host "`n=== 测试保存流程定义 ===" -ForegroundColor Cyan
    
    # 读取测试数据
    $testDataPath = "..\test-data\simple-approval-process.json"
    if (-not (Test-Path $testDataPath)) {
        Write-TestResult -TestName "读取测试数据" -Passed $false -Message "测试数据文件不存在: $testDataPath"
        return $null
    }
    
    try {
        $processDef = Get-Content $testDataPath -Raw | ConvertFrom-Json
        Write-TestResult -TestName "读取测试数据" -Passed $true -Message "流程名称: $($processDef.processDef.name)"
        
        # 发送保存请求
        $body = $processDef | ConvertTo-Json -Depth 10
        $response = Invoke-RestMethod -Uri "$BaseUrl/api/process-definitions" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 30
        
        if ($response -and $response.success) {
            Write-TestResult -TestName "保存流程定义" -Passed $true -Message "流程ID: $($response.processDefId)"
            return $response
        } else {
            Write-TestResult -TestName "保存流程定义" -Passed $false -Message "保存失败: $($response.message)"
            return $null
        }
    } catch {
        Write-TestResult -TestName "保存流程定义" -Passed $false -Message "请求失败: $_"
        return $null
    }
}

function Test-ExtendedAttributes {
    param([string]$ProcessDefId, [string]$VersionId)
    Write-Host "`n=== 测试扩展属性 ===" -ForegroundColor Cyan
    
    try {
        # 获取流程版本详情
        $response = Invoke-RestMethod -Uri "$BaseUrl/api/process-definitions/$ProcessDefId/versions/$VersionId" -Method GET -TimeoutSec 10
        
        # 检查关键扩展属性
        $extendedAttrs = @(
            @{ Name = "startNode"; Path = "startNode" }
            @{ Name = "endNodes"; Path = "endNodes" }
            @{ Name = "listeners"; Path = "listeners" }
            @{ Name = "rightGroups"; Path = "rightGroups" }
        )
        
        foreach ($attr in $extendedAttrs) {
            $value = $response
            $pathParts = $attr.Path -split '\.'
            foreach ($part in $pathParts) {
                if ($value -and $value.PSObject.Properties[$part]) {
                    $value = $value.$part
                } else {
                    $value = $null
                    break
                }
            }
            
            if ($value) {
                Write-TestResult -TestName "扩展属性: $($attr.Name)" -Passed $true -Message "属性存在"
            } else {
                Write-TestResult -TestName "扩展属性: $($attr.Name)" -Passed $false -Message "属性缺失或为空"
            }
        }
        
        return $response
    } catch {
        Write-TestResult -TestName "扩展属性测试" -Passed $false -Message "请求失败: $_"
        return $null
    }
}

function Show-TestSummary {
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
}

# 主执行流程
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "BPM扩展属性API测试" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "测试时间: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor Gray
Write-Host "服务端地址: $BaseUrl" -ForegroundColor Gray

# 1. 测试连接
$connected = Test-APIConnection
if (-not $connected) {
    Write-Host "`n无法连接到服务，测试中止。" -ForegroundColor Red
    exit 1
}

# 2. 获取流程定义列表
$processDefs = Test-GetProcessDefs

# 3. 测试获取流程详情
if ($processDefs -and $processDefs.Count -gt 0) {
    $firstProcess = $processDefs[0]
    $processDetail = Test-GetProcessDefDetail -ProcessDefId $firstProcess.processDefId
    
    # 4. 测试获取活动列表
    $activities = Test-GetActivities -ProcessDefId $firstProcess.processDefId -VersionId $firstProcess.activeVersion.processDefVersionId
    
    # 5. 测试获取路由列表
    $routes = Test-GetRoutes -ProcessDefId $firstProcess.processDefId -VersionId $firstProcess.activeVersion.processDefVersionId
    
    # 6. 测试扩展属性
    $extendedAttrs = Test-ExtendedAttributes -ProcessDefId $firstProcess.processDefId -VersionId $firstProcess.activeVersion.processDefVersionId
}

# 7. 测试保存流程定义
$savedProcess = Test-SaveProcessDef

# 8. 显示测试总结
Show-TestSummary

# 保存测试结果
$TestResults | Export-Csv -Path "..\test-execution\test-results-$(Get-Date -Format 'yyyyMMdd-HHmmss').csv" -NoTypeInformation -Encoding UTF8
Write-Host "`n测试结果已保存到: test-results-$(Get-Date -Format 'yyyyMMdd-HHmmss').csv" -ForegroundColor Green
