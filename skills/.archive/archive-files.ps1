# Skills 文档归档脚本
# 执行日期: 2026-04-07

$ErrorActionPreference = "Stop"

# 定义归档根目录
$archiveRoot = "e:\github\ooder-skills\skills\.archive"

# 定义需要归档的文件列表（版本 < 3.0.1）
$filesToArchive = @(
    # 0.1.0 版本
    @{
        Source = "e:\github\ooder-skills\skills\_system\skill-management\src\main\resources\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-management\src\main\resources\skill.yaml"
        Version = "0.1.0"
    },
    
    # 0.7.3 版本
    @{
        Source = "e:\github\ooder-skills\skills\_drivers\payment\skill-payment-wechat\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-payment-wechat\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\_drivers\payment\skill-payment-unionpay\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-payment-unionpay\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\_drivers\payment\skill-payment-alipay\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-payment-alipay\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\_drivers\org\skill-org-ldap\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-org-ldap\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\_drivers\media\skill-media-zhihu\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-media-zhihu\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\_drivers\media\skill-media-xiaohongshu\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-media-xiaohongshu\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\_drivers\media\skill-media-weibo\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-media-weibo\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\_drivers\media\skill-media-wechat\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-media-wechat\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\_drivers\media\skill-media-toutiao\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-media-toutiao\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\capabilities\search\skill-search\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-search\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\capabilities\monitor\skill-remote-terminal\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-remote-terminal\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\capabilities\scheduler\skill-task\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-task\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\tools\skill-report\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-report\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\tools\skill-share\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-share\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\capabilities\monitor\skill-cmd-service\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-cmd-service\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\capabilities\monitor\skill-res-service\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-res-service\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\capabilities\communication\skill-notify\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-notify\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\capabilities\communication\skill-msg\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-msg\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\capabilities\communication\skill-group\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-group\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\capabilities\communication\skill-im\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-im\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\capabilities\scheduler\skill-scheduler-quartz\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-scheduler-quartz\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\capabilities\communication\skill-email\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-email\skill.yaml"
        Version = "0.7.3"
    },
    @{
        Source = "e:\github\ooder-skills\skills\tools\skill-market\skill.yaml"
        Target = "$archiveRoot\v0.x\skill-market\skill.yaml"
        Version = "0.7.3"
    },
    
    # 1.0.0 版本
    @{
        Source = "e:\github\ooder-skills\skills\_drivers\bpm\bpmserver\skill.yaml"
        Target = "$archiveRoot\v1.x\bpmserver\skill.yaml"
        Version = "1.0.0"
    },
    @{
        Source = "e:\github\ooder-skills\skills\_system\skill-agent\skill.yaml"
        Target = "$archiveRoot\v1.x\skill-agent\skill.yaml"
        Version = "1.0.0"
    },
    @{
        Source = "e:\github\ooder-skills\skills\_system\skill-dict\skill.yaml"
        Target = "$archiveRoot\v1.x\skill-dict\skill.yaml"
        Version = "1.0.0"
    },
    @{
        Source = "e:\github\ooder-skills\skills\_system\skill-audit\skill.yaml"
        Target = "$archiveRoot\v1.x\skill-audit\skill.yaml"
        Version = "1.0.0"
    },
    @{
        Source = "e:\github\ooder-skills\skills\scenes\skill-real-estate-form\skill.yaml"
        Target = "$archiveRoot\v1.x\skill-real-estate-form\skill.yaml"
        Version = "1.0.0"
    },
    @{
        Source = "e:\github\ooder-skills\skills\scenes\skill-onboarding-assistant\skill.yaml"
        Target = "$archiveRoot\v1.x\skill-onboarding-assistant\skill.yaml"
        Version = "1.0.0"
    },
    @{
        Source = "e:\github\ooder-skills\skills\scenes\skill-document-assistant\skill.yaml"
        Target = "$archiveRoot\v1.x\skill-document-assistant\skill.yaml"
        Version = "1.0.0"
    },
    @{
        Source = "e:\github\ooder-skills\skills\scenes\skill-knowledge-share\skill.yaml"
        Target = "$archiveRoot\v1.x\skill-knowledge-share\skill.yaml"
        Version = "1.0.0"
    },
    @{
        Source = "e:\github\ooder-skills\skills\capabilities\communication\skill-notification\skill.yaml"
        Target = "$archiveRoot\v1.x\skill-notification\skill.yaml"
        Version = "1.0.0"
    },
    @{
        Source = "e:\github\ooder-skills\skills\_system\skill-knowledge\skill.yaml"
        Target = "$archiveRoot\v1.x\skill-knowledge\skill.yaml"
        Version = "1.0.0"
    },
    @{
        Source = "e:\github\ooder-skills\skills\capabilities\scenes\skill-scenes\skill.yaml"
        Target = "$archiveRoot\v1.x\skill-scenes\skill.yaml"
        Version = "1.0.0"
    },
    @{
        Source = "e:\github\ooder-skills\skills\_system\skill-menu\skill.yaml"
        Target = "$archiveRoot\v1.x\skill-menu\skill.yaml"
        Version = "1.0.0"
    },
    @{
        Source = "e:\github\ooder-skills\skills\_system\skill-capability\skill.yaml"
        Target = "$archiveRoot\v1.x\skill-capability\skill.yaml"
        Version = "1.0.0"
    },
    @{
        Source = "e:\github\ooder-skills\skills\_system\skill-discovery\skill.yaml"
        Target = "$archiveRoot\v1.x\skill-discovery\skill.yaml"
        Version = "1.0.0"
    },
    @{
        Source = "e:\github\ooder-skills\skills\_system\skill-org\skill.yaml"
        Target = "$archiveRoot\v1.x\skill-org\skill.yaml"
        Version = "1.0.0"
    },
    @{
        Source = "e:\github\ooder-skills\skills\_system\skill-role\skill.yaml"
        Target = "$archiveRoot\v1.x\skill-role\skill.yaml"
        Version = "1.0.0"
    },
    @{
        Source = "e:\github\ooder-skills\skills\_system\skill-scene\skill.yaml"
        Target = "$archiveRoot\v1.x\skill-scene\skill.yaml"
        Version = "1.0.0"
    }
)

# 执行归档
$successCount = 0
$failCount = 0
$logFile = "$archiveRoot\归档执行日志.txt"

# 初始化日志文件
"Skills 文档归档执行日志" | Out-File -FilePath $logFile -Encoding UTF8
"执行时间: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" | Out-File -FilePath $logFile -Encoding UTF8 -Append
"=" * 80 | Out-File -FilePath $logFile -Encoding UTF8 -Append
"" | Out-File -FilePath $logFile -Encoding UTF8 -Append

foreach ($file in $filesToArchive) {
    $source = $file.Source
    $target = $file.Target
    $version = $file.Version
    
    try {
        # 检查源文件是否存在
        if (Test-Path $source) {
            # 创建目标目录
            $targetDir = Split-Path $target -Parent
            if (-not (Test-Path $targetDir)) {
                New-Item -ItemType Directory -Path $targetDir -Force | Out-Null
            }
            
            # 移动文件
            Move-Item -Path $source -Destination $target -Force
            
            # 记录日志
            $logMessage = "[成功] 版本: $version | 源: $source | 目标: $target"
            $logMessage | Out-File -FilePath $logFile -Encoding UTF8 -Append
            Write-Host $logMessage -ForegroundColor Green
            
            $successCount++
        } else {
            $logMessage = "[跳过] 版本: $version | 源文件不存在: $source"
            $logMessage | Out-File -FilePath $logFile -Encoding UTF8 -Append
            Write-Host $logMessage -ForegroundColor Yellow
        }
    } catch {
        $logMessage = "[失败] 版本: $version | 源: $source | 错误: $($_.Exception.Message)"
        $logMessage | Out-File -FilePath $logFile -Encoding UTF8 -Append
        Write-Host $logMessage -ForegroundColor Red
        
        $failCount++
    }
}

# 输出统计信息
"" | Out-File -FilePath $logFile -Encoding UTF8 -Append
"=" * 80 | Out-File -FilePath $logFile -Encoding UTF8 -Append
"归档完成统计:" | Out-File -FilePath $logFile -Encoding UTF8 -Append
"成功: $successCount 个文件" | Out-File -FilePath $logFile -Encoding UTF8 -Append
"失败: $failCount 个文件" | Out-File -FilePath $logFile -Encoding UTF8 -Append
"总计: $($filesToArchive.Count) 个文件" | Out-File -FilePath $logFile -Encoding UTF8 -Append
"完成时间: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" | Out-File -FilePath $logFile -Encoding UTF8 -Append

Write-Host ""
Write-Host "归档完成!" -ForegroundColor Cyan
Write-Host "成功: $successCount 个文件" -ForegroundColor Green
Write-Host "失败: $failCount 个文件" -ForegroundColor Red
Write-Host "日志文件: $logFile" -ForegroundColor Yellow
