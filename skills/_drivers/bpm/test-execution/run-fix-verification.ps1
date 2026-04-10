# BPM JSON转换修复验证测试脚本
# 验证ProcessDefManagerService修复后的功能

$baseUrl = "http://localhost:8084/bpm"
$testResults = @()

function Write-TestResult {
    param($TestName, $Passed, $Details)
    $result = [PSCustomObject]@{
        TestName = $TestName
        Passed = $Passed
        Details = $Details
        Timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    }
    $script:testResults += $result
    
    if ($Passed) {
        Write-Host "✅ $TestName - 通过" -ForegroundColor Green
    } else {
        Write-Host "❌ $TestName - 失败" -ForegroundColor Red
        Write-Host "   $Details" -ForegroundColor Yellow
    }
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "BPM JSON转换修复验证测试" -ForegroundColor Cyan
Write-Host "服务地址: $baseUrl" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 测试1: 获取流程定义列表
Write-Host "测试1: 获取流程定义列表..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/processdef/list" -Method GET -TimeoutSec 30
    if ($response.code -eq 200 -and $response.data) {
        $processCount = $response.data.Count
        Write-TestResult -TestName "获取流程列表" -Passed $true -Details "共 $processCount 个流程定义"
        $existingProcessId = $response.data[0].processDefId
    } else {
        Write-TestResult -TestName "获取流程列表" -Passed $false -Details "返回数据异常"
    }
} catch {
    Write-TestResult -TestName "获取流程列表" -Passed $false -Details $_.Exception.Message
}

# 测试2: 获取完整流程定义（验证扩展属性读取）
if ($existingProcessId) {
    Write-Host "`n测试2: 获取完整流程定义 [$existingProcessId]..." -ForegroundColor Yellow
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/processdef/$existingProcessId" -Method GET -TimeoutSec 30
        if ($response.code -eq 200 -and $response.data) {
            $processData = $response.data
            Write-TestResult -TestName "读取流程定义" -Passed $true -Details "流程名称: $($processData.name)"
            
            # 检查扩展属性
            if ($processData.startNode) {
                Write-TestResult -TestName "StartOfWorkflow存在" -Passed $true -Details "startNode: $($processData.startNode | ConvertTo-Json -Compress)"
            } else {
                Write-TestResult -TestName "StartOfWorkflow存在" -Passed $false -Details "startNode为空"
            }
            
            if ($processData.endNodes -and $processData.endNodes.Count -gt 0) {
                Write-TestResult -TestName "EndOfWorkflow存在" -Passed $true -Details "共 $($processData.endNodes.Count) 个结束节点"
            } else {
                Write-TestResult -TestName "EndOfWorkflow存在" -Passed $false -Details "endNodes为空"
            }
            
            if ($processData.listeners -and $processData.listeners.Count -gt 0) {
                Write-TestResult -TestName "Listeners存在" -Passed $true -Details "共 $($processData.listeners.Count) 个监听器"
            } else {
                Write-TestResult -TestName "Listeners存在" -Passed $false -Details "listeners为空"
            }
            
            if ($processData.rightGroups -and $processData.rightGroups.Count -gt 0) {
                Write-TestResult -TestName "RightGroups存在" -Passed $true -Details "共 $($processData.rightGroups.Count) 个权限组"
            } else {
                Write-TestResult -TestName "RightGroups存在" -Passed $false -Details "rightGroups为空"
            }
            
            # 检查活动属性
            if ($processData.activities) {
                Write-TestResult -TestName "活动定义存在" -Passed $true -Details "共 $($processData.activities.Count) 个活动"
                
                # 检查第一个活动的扩展属性
                $firstActivity = $processData.activities[0]
                if ($firstActivity.positionCoord) {
                    Write-TestResult -TestName "活动坐标存在" -Passed $true -Details "X=$($firstActivity.positionCoord.x), Y=$($firstActivity.positionCoord.y)"
                } else {
                    Write-TestResult -TestName "活动坐标存在" -Passed $false -Details "坐标为空"
                }
                
                if ($firstActivity.participantId) {
                    Write-TestResult -TestName "活动ParticipantID存在" -Passed $true -Details "participantId: $($firstActivity.participantId)"
                } else {
                    Write-TestResult -TestName "活动ParticipantID存在" -Passed $false -Details "participantId为空"
                }
            }
        } else {
            Write-TestResult -TestName "读取流程定义" -Passed $false -Details "返回数据异常: $($response.message)"
        }
    } catch {
        Write-TestResult -TestName "读取流程定义" -Passed $false -Details $_.Exception.Message
    }
}

# 测试3: 保存包含完整扩展属性的流程定义
Write-Host "`n测试3: 保存包含完整扩展属性的流程定义..." -ForegroundColor Yellow

$testProcessId = "test-process-fix-$(Get-Random -Minimum 1000 -Maximum 9999)"
$processData = @{
    processDefId = $testProcessId
    name = "测试流程-修复验证"
    description = "验证修复后JSON转换功能"
    classification = "办公流程"
    systemCode = "bpm"
    accessLevel = "PUBLIC"
    
    # 开始节点（XPDL格式）
    startNode = @{
        participantId = "participant-start"
        firstActivityId = "act-start"
        positionCoord = @{ x = 100; y = 100 }
        routing = "NO_ROUTING"
    }
    
    # 结束节点（XPDL格式）
    endNodes = @(
        @{
            participantId = "participant-end-1"
            lastActivityId = "act-end-1"
            positionCoord = @{ x = 500; y = 100 }
            routing = "NO_ROUTING"
        }
    )
    
    # 监听器（XML格式）
    listeners = @(
        @{
            id = "listener-1"
            name = "流程启动监听器"
            'event' = "PROCESS_START"
            realizeClass = "com.example.ProcessStartListener"
        }
    )
    
    # 权限组（XML格式）
    rightGroups = @(
        @{
            id = "rg-1"
            name = "默认权限组"
            code = "DEFAULT"
            order = 1
            defaultGroup = $true
        }
    )
    
    # 活动定义
    activities = @(
        @{
            activityDefId = "act-start"
            name = "开始"
            description = "流程开始节点"
            position = "START"
            positionCoord = @{ x = 100; y = 100 }
            participantId = "participant-start"
        },
        @{
            activityDefId = "act-approval"
            name = "审批节点"
            description = "审批活动"
            position = "NORMAL"
            positionCoord = @{ x = 300; y = 100 }
            participantId = "participant-approval"
            limitTime = 24
            alertTime = 12
            durationUnit = "H"
            canRouteBack = "Y"
            routeBackMethod = "PREV"
            canSpecialSend = "N"
            join = "XOR"
            split = "XOR"
            
            # RIGHT属性组
            RIGHT = @{
                performType = "ANY"
                performSequence = "PARALLEL"
                specialSendScope = "NONE"
                canInsteadSign = "Y"
                canTakeBack = "Y"
                canReSend = "N"
                insteadSignSelected = "admin"
                performerSelectedId = "user1,user2"
                readerSelectedId = "user3"
            }
            
            # FORM属性组
            FORM = @{
                formId = "form-001"
                formName = "审批表单"
                formType = "INTERNAL"
                formUrl = "/forms/approval.html"
            }
            
            # SERVICE属性组
            SERVICE = @{
                httpMethod = "POST"
                httpUrl = "http://api.example.com/approval"
                httpRequestType = "JSON"
                httpResponseType = "JSON"
                serviceSelectedId = "service-001"
            }
        },
        @{
            activityDefId = "act-end-1"
            name = "结束"
            description = "流程结束节点"
            position = "END"
            positionCoord = @{ x = 500; y = 100 }
            participantId = "participant-end-1"
        }
    )
    
    # 路由定义
    routes = @(
        @{
            routeDefId = "route-1"
            name = "开始到审批"
            fromActivityDefId = "act-start"
            toActivityDefId = "act-approval"
            routeDirection = "FORWARD"
            routeCondition = ""
            routeConditionType = "OTHERWISE"
            routing = "NO_ROUTING"
        },
        @{
            routeDefId = "route-2"
            name = "审批到结束"
            fromActivityDefId = "act-approval"
            toActivityDefId = "act-end-1"
            routeDirection = "FORWARD"
            routeCondition = ""
            routeConditionType = "OTHERWISE"
            routing = "NO_ROUTING"
        }
    )
} | ConvertTo-Json -Depth 10

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/processdef/save" -Method POST `
        -ContentType "application/json" -Body $processData -TimeoutSec 30
    
    if ($response.code -eq 200 -and $response.data) {
        $savedData = $response.data
        Write-TestResult -TestName "保存流程定义" -Passed $true -Details "流程ID: $($savedData.processDefId)"
        
        # 验证保存的数据
        Write-Host "`n测试4: 验证保存的扩展属性..." -ForegroundColor Yellow
        
        if ($savedData.startNode -and $savedData.startNode.participantId) {
            Write-TestResult -TestName "StartOfWorkflow保存" -Passed $true `
                -Details "participantId=$($savedData.startNode.participantId), firstActivityId=$($savedData.startNode.firstActivityId)"
        } else {
            Write-TestResult -TestName "StartOfWorkflow保存" -Passed $false -Details "startNode为空或格式不正确"
        }
        
        if ($savedData.endNodes -and $savedData.endNodes.Count -gt 0) {
            Write-TestResult -TestName "EndOfWorkflow保存" -Passed $true `
                -Details "共$($savedData.endNodes.Count)个结束节点"
        } else {
            Write-TestResult -TestName "EndOfWorkflow保存" -Passed $false -Details "endNodes为空"
        }
        
        if ($savedData.listeners -and $savedData.listeners.Count -gt 0) {
            Write-TestResult -TestName "Listeners保存" -Passed $true `
                -Details "共$($savedData.listeners.Count)个监听器"
        } else {
            Write-TestResult -TestName "Listeners保存" -Passed $false -Details "listeners为空"
        }
        
        if ($savedData.rightGroups -and $savedData.rightGroups.Count -gt 0) {
            Write-TestResult -TestName "RightGroups保存" -Passed $true `
                -Details "共$($savedData.rightGroups.Count)个权限组"
        } else {
            Write-TestResult -TestName "RightGroups保存" -Passed $false -Details "rightGroups为空"
        }
        
        # 验证活动属性组
        $approvalActivity = $savedData.activities | Where-Object { $_.activityDefId -eq "act-approval" }
        if ($approvalActivity) {
            if ($approvalActivity.RIGHT) {
                Write-TestResult -TestName "RIGHT属性组保存" -Passed $true `
                    -Details "performType=$($approvalActivity.RIGHT.performType)"
            } else {
                Write-TestResult -TestName "RIGHT属性组保存" -Passed $false -Details "RIGHT属性组为空"
            }
            
            if ($approvalActivity.FORM) {
                Write-TestResult -TestName "FORM属性组保存" -Passed $true `
                    -Details "formId=$($approvalActivity.FORM.formId)"
            } else {
                Write-TestResult -TestName "FORM属性组保存" -Passed $false -Details "FORM属性组为空"
            }
            
            if ($approvalActivity.SERVICE) {
                Write-TestResult -TestName "SERVICE属性组保存" -Passed $true `
                    -Details "httpMethod=$($approvalActivity.SERVICE.httpMethod)"
            } else {
                Write-TestResult -TestName "SERVICE属性组保存" -Passed $false -Details "SERVICE属性组为空"
            }
        }
        
    } else {
        Write-TestResult -TestName "保存流程定义" -Passed $false -Details "返回异常: $($response.message)"
    }
} catch {
    Write-TestResult -TestName "保存流程定义" -Passed $false -Details $_.Exception.Message
}

# 输出测试摘要
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "测试摘要" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$passedCount = ($testResults | Where-Object { $_.Passed }).Count
$failedCount = ($testResults | Where-Object { -not $_.Passed }).Count
$totalCount = $testResults.Count

Write-Host "总测试数: $totalCount" -ForegroundColor White
Write-Host "通过: $passedCount" -ForegroundColor Green
Write-Host "失败: $failedCount" -ForegroundColor Red

if ($failedCount -eq 0) {
    Write-Host "`n✅ 所有测试通过！JSON转换修复成功。" -ForegroundColor Green
} else {
    Write-Host "`n⚠️ 部分测试失败，请检查修复实现。" -ForegroundColor Yellow
}

# 导出详细结果
$resultsPath = "e:\github\ooder-skills\skills\_drivers\bpm\test-execution\fix-verification-results.json"
$testResults | ConvertTo-Json -Depth 3 | Out-File -FilePath $resultsPath -Encoding UTF8
Write-Host "`n详细测试结果已保存到: $resultsPath" -ForegroundColor Gray
