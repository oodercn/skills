# update-skills-config.ps1
# 统一版本为 2.3.1 并添加标准 llmConfig

$skillsDir = "e:\github\ooder-skills\skills"

# 标准的 llmConfig 模板
$standardLlmConfig = @'

  llmConfig:
    required: false
    defaultProvider: "deepseek"
    defaultModel: "deepseek-chat"
    capabilities:
      - chat
      - streaming
      - function-calling
    modelSelection:
      allowUserOverride: true
      availableProviders:
        - deepseek
        - openai
        - qianwen
        - volcengine
        - ollama
    functionCalling:
      enabled: true
      tools:
        - name: query_skill_capability
          description: "查询当前技能的能力和使用方法"
          parameters:
            type: object
            properties:
              capability:
                type: string
                description: "能力名称"
              detail:
                type: string
                enum: [brief, detailed, examples]
                default: "brief"
                description: "详情级别"
        - name: execute_mvel_action
          description: "通过MVEL表达式执行后台操作"
          parameters:
            type: object
            properties:
              expression:
                type: string
                description: "MVEL表达式"
              context:
                type: object
                description: "执行上下文"
        - name: generate_ui_form
          description: "生成UI表单供用户填写"
          parameters:
            type: object
            properties:
              formType:
                type: string
                description: "表单类型"
              fields:
                type: array
                items:
                  type: object
                description: "表单字段定义"
              defaults:
                type: object
                description: "默认值"
        - name: execute_batch_operation
          description: "执行批量操作"
          parameters:
            type: object
            properties:
              operation:
                type: string
                description: "操作类型"
              items:
                type: array
                items:
                  type: object
                description: "操作项列表"
        - name: convert_to_javascript
          description: "转换为JavaScript代码供用户使用"
          parameters:
            type: object
            properties:
              action:
                type: string
                description: "要执行的动作"
              parameters:
                type: object
                description: "动作参数"
      toolChoice: auto
    rateLimits:
      requestsPerMinute: 60
      tokensPerMinute: 100000
'@

# 查找所有 skill.yaml 文件
$skillFiles = Get-ChildItem -Path $skillsDir -Recurse -Filter "skill.yaml"
$skillIndexFiles = Get-ChildItem -Path $skillsDir -Recurse -Filter "skill-index-entry.yaml"

$allFiles = $skillFiles + $skillIndexFiles

$count = 0
foreach ($file in $allFiles) {
    $content = Get-Content $file.FullName -Raw
    $modified = $false
    
    # 1. 统一版本为 2.3.1
    if ($content -match 'version:\s*["\']?0\.7\.3["\']?') {
        $content = $content -replace 'version:\s*["\']?0\.7\.3["\']?', 'version: "2.3.1"'
        $modified = $true
    }
    
    # 2. 检查是否已有 llmConfig
    if ($content -notmatch 'llmConfig:') {
        # 在 runtime 后添加 llmConfig
        if ($content -match '  runtime:\s*\n\s*language:') {
            # 找到 runtime 块结束位置
            $content = $content -replace '(  runtime:\s*\n\s*language:.*?\n\s*javaVersion:.*?\n\s*framework:.*?\n)', "`$1$standardLlmConfig`n"
            $modified = $true
        }
    }
    
    if ($modified) {
        $content | Out-File $file.FullName -Encoding UTF8 -NoNewline
        $count++
        Write-Host "Updated: $($file.FullName)"
    }
}

Write-Host "`nTotal files updated: $count"
