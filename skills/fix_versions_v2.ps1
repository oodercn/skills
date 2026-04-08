# Ooder Skills 版本号批量修复脚本 V2

$skillsPath = "E:\github\ooder-skills\skills"
$targetVersion = "3.0.1"

# 需要修复的 skills 列表
$skillsToFix = @(
    "_business\skill-driver-config",
    "_business\skill-install-scene",
    "_business\skill-installer",
    "_business\skill-keys",
    "_business\skill-procedure",
    "_business\skill-security",
    "_business\skill-todo",
    "_system\skill-audit",
    "_system\skill-auth",
    "_system\skill-capability",
    "_system\skill-common",
    "_system\skill-config",
    "_system\skill-dashboard",
    "_system\skill-dict",
    "_system\skill-discovery",
    "_system\skill-history",
    "_system\skill-im-gateway",
    "_system\skill-install",
    "_system\skill-key",
    "_system\skill-knowledge",
    "_system\skill-knowledge-platform",
    "_system\skill-llm-chat",
    "_system\skill-management",
    "_system\skill-menu",
    "_system\skill-messaging",
    "_system\skill-notification",
    "_system\skill-org",
    "_system\skill-protocol",
    "_system\skill-rag",
    "_system\skill-role",
    "_system\skill-scene",
    "_system\skill-setup",
    "_system\skill-support",
    "_system\skill-template",
    "_system\skill-tenant",
    "_system\skill-workflow",
    "_system\skills-bpm-demo",
    "_drivers\bpm\bpmserver",
    "_drivers\bpm\bpm-designer",
    "_drivers\im\skill-im-weixin",
    "_drivers\org\skill-org-web",
    "_drivers\spi\skill-spi",
    "capabilities\infrastructure\skill-k8s",
    "capabilities\infrastructure\skill-hosting",
    "capabilities\infrastructure\skill-failover-manager",
    "capabilities\llm\skill-llm-config-manager",
    "capabilities\monitor\skill-health",
    "capabilities\monitor\skill-monitor",
    "capabilities\infrastructure\skill-openwrt",
    "capabilities\llm\skill-llm-config",
    "capabilities\auth\skill-user-auth",
    "capabilities\communication\skill-group",
    "capabilities\communication\skill-mqtt",
    "capabilities\communication\skill-email",
    "capabilities\communication\skill-im",
    "capabilities\communication\skill-notification",
    "capabilities\communication\skill-msg",
    "capabilities\communication\skill-notify",
    "capabilities\monitor\skill-network",
    "capabilities\scheduler\skill-scheduler-quartz",
    "capabilities\monitor\skill-res-service",
    "capabilities\monitor\skill-cmd-service",
    "capabilities\scenes\skill-scenes",
    "scenes\daily-report",
    "scenes\skill-agent-recommendation",
    "scenes\skill-approval-form",
    "scenes\skill-business",
    "scenes\skill-collaboration",
    "scenes\skill-document-assistant",
    "scenes\skill-knowledge-management",
    "scenes\skill-knowledge-qa",
    "scenes\skill-knowledge-share",
    "scenes\skill-meeting-minutes",
    "scenes\skill-onboarding-assistant",
    "scenes\skill-platform-bind",
    "scenes\skill-project-knowledge",
    "scenes\skill-real-estate-form",
    "scenes\skill-recording-qa",
    "scenes\skill-recruitment-management",
    "tools\skill-agent-cli",
    "tools\skill-calendar",
    "tools\skill-command-shortcut",
    "tools\skill-doc-collab",
    "tools\skill-document-processor",
    "tools\skill-market",
    "tools\skill-msg-push",
    "tools\skill-todo-sync"
)

$fixedCount = 0
$errorCount = 0

foreach ($skillPath in $skillsToFix) {
    $yamlPath = "$skillsPath\$skillPath\skill.yaml"
    
    if (Test-Path $yamlPath) {
        try {
            $content = Get-Content $yamlPath -Raw
            $originalContent = $content
            
            # 修复版本号
            $content = $content -replace 'version:\s*1\.0\.0', "version: ""$targetVersion"""
            $content = $content -replace 'version:\s*3\.0\.2', "version: ""$targetVersion"""
            $content = $content -replace 'version:\s*2\.3\.0', "version: ""$targetVersion"""
            $content = $content -replace 'version:\s*2\.3\b', "version: ""$targetVersion"""
            $content = $content -replace 'version:\s*0\.7\.3', "version: ""$targetVersion"""
            $content = $content -replace 'version:\s*"1\.0\.0"', "version: ""$targetVersion"""
            $content = $content -replace 'version:\s*"3\.0\.2"', "version: ""$targetVersion"""
            
            if ($originalContent -ne $content) {
                Set-Content $yamlPath $content -NoNewline -Encoding UTF8
                Write-Host "Fixed: $skillPath" -ForegroundColor Green
                $fixedCount++
            } else {
                Write-Host "Skip: $skillPath" -ForegroundColor Yellow
            }
        }
        catch {
            Write-Host "Error: $skillPath - $_" -ForegroundColor Red
            $errorCount++
        }
    }
    else {
        Write-Host "Not found: $yamlPath" -ForegroundColor Red
        $errorCount++
    }
}

Write-Host "`nDone! Fixed: $fixedCount, Errors: $errorCount" -ForegroundColor Cyan
