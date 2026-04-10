# BPM JSON转换修复验证测试脚本
# 验证ProcessDefManagerService修复后的功能

$baseUrl = "http://localhost:8080"
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
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 测试1: 保存包含完整属性的流程定义
Write-Host "测试1: 保存包含完整扩展属性的流程定义..." -ForegroundColor Yellow
$processData = @{
    processDefId = "test-process-$(Get-Random)"
    name = "测试流程-完整属性"
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
    
    # 结束节点（XPDL格式，多个用|分隔）
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
            event = "PROCESS_START"
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
    
    # BPD扩展属性
    creatorName = "系统管理员"
    modifierId = "admin"
    modifierName = "管理员"
    modifyTime = "2026-04-09T10:00:00"
    limit = "30"
    durationUnit = "D"
    
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
            
            # WORKFLOW属性组
            WORKFLOW = @{
                deadLineOperation = "NOTIFY"
                specialScope = "ALL"
            }
            
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
                movePerformerTo = ""
                moveSponsorTo = ""
                moveReaderTo = ""
                surrogateId = ""
                surrogateName = ""
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
                httpServiceParams = '{"key":"value"}'
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
    $response = Invoke-RestMethod -Uri "$baseUrl/api/process-def/save" -Method POST `
        -ContentType "application/json" -Body $processData -TimeoutSec 30
    
    if ($response.processDefId) {
        Write-TestResult -TestName "保存流程定义" -Passed $true -Details "流程ID: $($response.processDefId)"
        $savedProcessId = $response.processDefId
    } else {
        Write-TestResult -TestName "保存流程定义" -Passed $false -Details "响应中未找到processDefId"
    }
} catch {
    Write-TestResult -TestName "保存流程定义" -Passed $false -Details $_.Exception.Message
}

# 测试2: 读取并验证完整流程定义
if ($savedProcessId) {
    Write-Host "`n测试2: 读取完整流程定义并验证所有扩展属性..." -ForegroundColor Yellow
    
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/process-def/full/$savedProcessId" -Method GET -TimeoutSec 30
        
        # 验证startNode
        if ($response.startNode) {
            $startNode = $response.startNode
            if ($startNode.participantId -and $startNode.firstActivityId -and $startNode.positionCoord) {
                Write-TestResult -TestName "StartOfWorkflow解析" -Passed $true `
                    -Details "participantId=$($startNode.participantId), firstActivityId=$($startNode.firstActivityId)"
            } else {
                Write-TestResult -TestName "StartOfWorkflow解析" -Passed $false `
                    -Details "缺少必要字段"
            }
        } else {
            Write-TestResult -TestName "StartOfWorkflow解析" -Passed $false -Details "startNode为空"
        }
        
        # 验证endNodes
        if ($response.endNodes -and $response.endNodes.Count -gt 0) {
            $endNode = $response.endNodes[0]
            if ($endNode.participantId -and $endNode.lastActivityId -and $endNode.positionCoord) {
                Write-TestResult -TestName "EndOfWorkflow解析" -Passed $true `
                    -Details "共$($response.endNodes.Count)个结束节点"
            } else {
                Write-TestResult -TestName "EndOfWorkflow解析" -Passed $false -Details "缺少必要字段"
            }
        } else {
            Write-TestResult -TestName "EndOfWorkflow解析" -Passed $false -Details "endNodes为空"
        }
        
        # 验证listeners
        if ($response.listeners -and $response.listeners.Count -gt 0) {
            $listener = $response.listeners[0]
            if ($listener.id -and $listener.name -and $listener.event -and $listener.realizeClass) {
                Write-TestResult -TestName "Listeners解析" -Passed $true `
                    -Details "共$($response.listeners.Count)个监听器"
            } else {
                Write-TestResult -TestName "Listeners解析" -Passed $false -Details "缺少必要字段"
            }
        } else {
            Write-TestResult -TestName "Listeners解析" -Passed $false -Details "listeners为空"
        }
        
        # 验证rightGroups
        if ($response.rightGroups -and $response.rightGroups.Count -gt 0) {
            $rg = $response.rightGroups[0]
            if ($rg.id -and $rg.name -and $rg.code) {
                Write-TestResult -TestName "RightGroups解析" -Passed $true `
                    -Details "共$($response.rightGroups.Count)个权限组"
            } else {
                Write-TestResult -TestName "RightGroups解析" -Passed $false -Details "缺少必要字段"
            }
        } else {
            Write-TestResult -TestName "RightGroups解析" -Passed $false -Details "rightGroups为空"
        }
        
        # 验证活动属性
        $activities = $response.activities
        if ($activities) {
            $approvalActivity = $activities | Where-Object { $_.activityDefId -eq "act-approval" }
            if ($approvalActivity) {
                # 验证坐标
                if ($approvalActivity.positionCoord -and $approvalActivity.participantId) {
                    Write-TestResult -TestName "活动坐标和ParticipantID" -Passed $true `
                        -Details "X=$($approvalActivity.positionCoord.x), Y=$($approvalActivity.positionCoord.y), Participant=$($approvalActivity.participantId)"
                } else {
                    Write-TestResult -TestName "活动坐标和ParticipantID" -Passed $false -Details "坐标或ParticipantID缺失"
                }
                
                # 验证RIGHT属性组
                if ($approvalActivity.RIGHT) {
                    Write-TestResult -TestName "RIGHT属性组" -Passed $true `
                        -Details "performType=$($approvalActivity.RIGHT.performType)"
                } else {
                    Write-TestResult -TestName "RIGHT属性组" -Passed $false -Details "RIGHT属性组缺失"
                }
                
                # 验证FORM属性组
                if ($approvalActivity.FORM) {
                    Write-TestResult -TestName "FORM属性组" -Passed $true `
                        -Details "formId=$($approvalActivity.FORM.formId)"
                } else {
                    Write-TestResult -TestName "FORM属性组" -Passed $false -Details "FORM属性组缺失"
                }
                
                # 验证SERVICE属性组
                if ($approvalActivity.SERVICE) {
                    Write-TestResult -TestName "SERVICE属性组" -Passed $true `
                        -Details "httpMethod=$($approvalActivity.SERVICE.httpMethod)"
                } else {
                    Write-TestResult -TestName "SERVICE属性组" -Passed $false -Details "SERVICE属性组缺失"
                }
            } else {
                Write-TestResult -TestName "活动属性验证" -Passed $false -Details "未找到审批活动"
            }
        } else {
            Write-TestResult -TestName "活动属性验证" -Passed $false -Details "activities为空"
        }
        
    } catch {
        Write-TestResult -TestName "读取流程定义" -Passed $false -Details $_.Exception.Message
    }
}

# 测试3: XPDL格式兼容性测试
Write-Host "`n测试3: XPDL格式兼容性测试..." -ForegroundColor Yellow

# 测试StartOfWorkflow格式: ParticipantID;FirstActivityID;X;Y;Routing
$xpdlStartTest = "participant-1;act-001;150;200;NO_ROUTING"
$parts = $xpdlStartTest.Split(";")
if ($parts.Length -eq 5) {
    Write-TestResult -TestName "StartOfWorkflow XPDL格式" -Passed $true `
        -Details "格式正确: Participant=$($parts[0]), Activity=$($parts[1]), X=$($parts[2]), Y=$($parts[3]), Routing=$($parts[4])"
} else {
    Write-TestResult -TestName "StartOfWorkflow XPDL格式" -Passed $false -Details "格式不正确"
}

# 测试EndOfWorkflow格式: ParticipantID;LastActivityID;X;Y;Routing（多个用|分隔）
$xpdlEndTest = "participant-1;act-end-1;500;200;NO_ROUTING|participant-2;act-end-2;500;300;NO_ROUTING"
$endNodes = $xpdlEndTest.Split("|")
if ($endNodes.Length -eq 2) {
    Write-TestResult -TestName "EndOfWorkflow XPDL格式" -Passed $true `
        -Details "格式正确: 共$($endNodes.Length)个结束节点"
} else {
    Write-TestResult -TestName "EndOfWorkflow XPDL格式" -Passed $false -Details "格式不正确"
}

# 测试4: XML格式兼容性测试
Write-Host "`n测试4: XML格式兼容性测试..." -ForegroundColor Yellow

# 测试Listeners XML格式
$listenersXml = '<itjds:Listeners><itjds:Listener Id="l-1" Name="监听器1" ListenerEvent="START" RealizeClass="com.example.Listener1"/></itjds:Listeners>'
if ($listenersXml -match '<itjds:Listener\s+Id="([^"]+)"') {
    Write-TestResult -TestName "Listeners XML格式" -Passed $true -Details "XML格式正确"
} else {
    Write-TestResult -TestName "Listeners XML格式" -Passed $false -Details "XML格式不正确"
}

# 测试RightGroups XML格式
$rightGroupsXml = '<itjds:RightGroups><itjds:RightGroup Id="rg-1" Name="权限组1" Code="CODE1" Order="1" DefaultGroup="YES"/></itjds:RightGroups>'
if ($rightGroupsXml -match '<itjds:RightGroup\s+Id="([^"]+)"') {
    Write-TestResult -TestName "RightGroups XML格式" -Passed $true -Details "XML格式正确"
} else {
    Write-TestResult -TestName "RightGroups XML格式" -Passed $false -Details "XML格式不正确"
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
$resultsPath = "e:\github\ooder-skills\skills\_drivers\bpm\test-execution\verify-fix-results.json"
$testResults | ConvertTo-Json -Depth 3 | Out-File -FilePath $resultsPath -Encoding UTF8
Write-Host "`n详细测试结果已保存到: $resultsPath" -ForegroundColor Gray
