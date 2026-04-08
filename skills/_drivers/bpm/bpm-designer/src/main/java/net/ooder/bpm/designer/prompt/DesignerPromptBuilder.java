package net.ooder.bpm.designer.prompt;

import net.ooder.bpm.designer.model.dto.DesignerContextDTO;
import net.ooder.bpm.designer.model.dto.ProcessDefDTO;
import net.ooder.bpm.designer.model.dto.ActivityDefDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DesignerPromptBuilder {

    public String buildSystemPrompt(DesignerContextDTO context) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append(buildRoleSection());
        prompt.append("\n");
        prompt.append(buildContextSection(context));
        prompt.append("\n");
        prompt.append(buildSchemaSection());
        prompt.append("\n");
        prompt.append(buildRulesSection());
        prompt.append("\n");
        prompt.append(buildExamplesSection());
        
        return prompt.toString();
    }

    private String buildRoleSection() {
        return """
            ## Role Definition
            
            你是一个专业的 BPM 流程设计助手，帮助用户通过自然语言描述创建和修改流程定义。
            你具备以下能力：
            
            1. **流程创建**：根据用户描述生成完整的流程定义
            2. **活动设计**：创建和配置各种类型的活动节点
            3. **路由配置**：设置活动之间的流转条件和路径
            4. **属性设置**：配置流程和活动的各种属性
            5. **智能建议**：根据上下文提供设计建议和最佳实践
            6. **错误修复**：检测并修复流程设计中的问题
            
            """;
    }

    private String buildContextSection(DesignerContextDTO context) {
        StringBuilder section = new StringBuilder();
        section.append("## Current Context\n\n");
        
        if (context == null) {
            section.append("No active context available.\n");
            return section.toString();
        }
        
        section.append("### User Information\n");
        section.append("- User: ").append(context.getUserName() != null ? context.getUserName() : "Anonymous").append("\n");
        section.append("- Role: ").append(context.getUserRole() != null ? context.getUserRole() : "Designer").append("\n");
        section.append("- Mode: ").append(context.getMode() != null ? context.getMode() : "CREATE").append("\n\n");
        
        if (context.getCurrentProcess() != null) {
            section.append(buildProcessContext(context.getCurrentProcess()));
        }
        
        if (context.getCurrentActivity() != null) {
            section.append(buildActivityContext(context.getCurrentActivity()));
        }
        
        if (context.getRagContext() != null && !context.getRagContext().isEmpty()) {
            section.append(buildRagContextSection(context));
        }
        
        return section.toString();
    }

    private String buildProcessContext(ProcessDefDTO process) {
        StringBuilder section = new StringBuilder();
        section.append("### Current Process\n");
        section.append("- ID: ").append(process.getProcessDefId()).append("\n");
        section.append("- Name: ").append(process.getName()).append("\n");
        section.append("- Description: ").append(process.getDescription() != null ? process.getDescription() : "N/A").append("\n");
        section.append("- Version: ").append(process.getVersion() != null ? process.getVersion() : 1).append("\n");
        section.append("- Status: ").append(process.getPublicationStatus() != null ? process.getPublicationStatus() : "DRAFT").append("\n");
        
        if (process.getActivities() != null && !process.getActivities().isEmpty()) {
            section.append("- Activities: ").append(process.getActivities().size()).append("\n");
            section.append("  - ").append(process.getActivities().stream()
                .map(ActivityDefDTO::getName)
                .collect(Collectors.joining(", "))).append("\n");
        }
        
        if (process.getRoutes() != null && !process.getRoutes().isEmpty()) {
            section.append("- Routes: ").append(process.getRoutes().size()).append("\n");
        }
        
        section.append("\n");
        return section.toString();
    }

    private String buildActivityContext(ActivityDefDTO activity) {
        StringBuilder section = new StringBuilder();
        section.append("### Current Activity\n");
        section.append("- ID: ").append(activity.getActivityDefId()).append("\n");
        section.append("- Name: ").append(activity.getName()).append("\n");
        section.append("- Type: ").append(activity.getActivityType()).append("\n");
        section.append("- Category: ").append(activity.getActivityCategory()).append("\n");
        section.append("- Implementation: ").append(activity.getImplementation()).append("\n");
        
        if (activity.getDescription() != null) {
            section.append("- Description: ").append(activity.getDescription()).append("\n");
        }
        
        section.append("\n");
        return section.toString();
    }

    private String buildRagContextSection(DesignerContextDTO context) {
        StringBuilder section = new StringBuilder();
        section.append("### Knowledge References\n");
        
        if (context.getKnowledgeReferences() != null && !context.getKnowledgeReferences().isEmpty()) {
            for (DesignerContextDTO.KnowledgeReference ref : context.getKnowledgeReferences()) {
                section.append("- [").append(ref.getTitle()).append("] ");
                section.append("(relevance: ").append(String.format("%.2f", ref.getRelevance())).append(")\n");
                if (ref.getSnippet() != null) {
                    section.append("  ").append(ref.getSnippet()).append("\n");
                }
            }
        }
        
        section.append("\n");
        return section.toString();
    }

    private String buildSchemaSection() {
        return """
            ## Available Schema Types
            
            ### Activity Types
            - TASK: 用户任务 - 需要人工处理的任务节点
            - SERVICE: 服务任务 - 自动执行的服务调用
            - SCRIPT: 脚本任务 - 执行脚本代码
            - START: 开始节点 - 流程起始点
            - END: 结束节点 - 流程结束点
            - XOR_GATEWAY: 排他网关 - 条件分支
            - AND_GATEWAY: 并行网关 - 并行分支
            - OR_GATEWAY: 包容网关 - 条件并行分支
            - SUBPROCESS: 子流程 - 嵌套流程
            - LLM_TASK: LLM任务 - AI智能处理任务
            
            ### Activity Categories
            - HUMAN: 人工活动 - 需要人工参与
            - AGENT: Agent活动 - AI代理执行
            - SCENE: 场景活动 - 场景驱动执行
            
            ### Implementation Types
            - IMPL_NO: 无实现 - 手动活动
            - IMPL_TOOL: 工具实现 - 调用工具
            - IMPL_SUBFLOW: 子流程实现 - 调用子流程
            - IMPL_OUTFLOW: 外部流程实现 - 调用外部流程
            - IMPL_DEVICE: 设备实现 - IoT设备交互
            - IMPL_EVENT: 事件实现 - 事件驱动
            - IMPL_SERVICE: 服务实现 - 服务调用
            
            ### Permission Types (PerformType)
            - SINGLE: 单人办理
            - MULTIPLE: 多人办理
            - JOINTSIGN: 会签
            - NEEDNOTSELECT: 无需选择
            - NOSELECT: 不选择
            
            ### Permission Groups (RightGroup)
            - PERFORMER: 办理人
            - SPONSOR: 发起人
            - READER: 阅办人
            - HISTORYPERFORMER: 历史办理人
            - HISSPONSOR: 历史发起人
            - HISTORYREADER: 历史阅办人
            - NORIGHT: 无权限
            - NULL: 访客组
            
            """;
    }

    private String buildRulesSection() {
        return """
            ## Interaction Rules
            
            1. **理解用户意图**
               - 分析用户的自然语言描述
               - 识别要创建或修改的对象类型
               - 提取关键属性和配置信息
            
            2. **提供准确建议**
               - 基于当前上下文提供建议
               - 考虑流程设计的最佳实践
               - 确保建议的可行性
            
            3. **生成规范输出**
               - 使用标准的 JSON 格式输出
               - 确保所有必填字段都有值
               - 使用正确的枚举值
            
            4. **错误处理**
               - 检测用户输入中的问题
               - 提供清晰的错误说明
               - 给出修复建议
            
            5. **上下文感知**
               - 记住之前的对话内容
               - 理解当前编辑状态
               - 提供连贯的交互体验
            
            """;
    }

    private String buildExamplesSection() {
        return """
            ## Example Interactions
            
            ### Example 1: Create Process
            User: "创建一个请假审批流程"
            Response:
            ```json
            {
              "action": "create_process",
              "params": {
                "processDefId": "leave_approval",
                "name": "请假审批流程",
                "description": "员工请假申请审批流程",
                "classification": "NORMAL",
                "accessLevel": "PUBLIC",
                "activities": [
                  {
                    "activityDefId": "start",
                    "name": "开始",
                    "activityType": "START",
                    "position": "START"
                  },
                  {
                    "activityDefId": "submit",
                    "name": "提交申请",
                    "activityType": "TASK",
                    "activityCategory": "HUMAN",
                    "implementation": "IMPL_NO"
                  },
                  {
                    "activityDefId": "approve",
                    "name": "审批",
                    "activityType": "TASK",
                    "activityCategory": "HUMAN",
                    "implementation": "IMPL_NO"
                  },
                  {
                    "activityDefId": "end",
                    "name": "结束",
                    "activityType": "END",
                    "position": "END"
                  }
                ]
              }
            }
            ```
            
            ### Example 2: Add Activity
            User: "添加一个经理审批节点"
            Response:
            ```json
            {
              "action": "add_activity",
              "params": {
                "activityDefId": "manager_approve",
                "name": "经理审批",
                "activityType": "TASK",
                "activityCategory": "HUMAN",
                "implementation": "IMPL_NO",
                "right": {
                  "performType": "SINGLE",
                  "performerSelectedId": "manager_formula"
                }
              }
            }
            ```
            
            ### Example 3: Set Attribute
            User: "设置审批节点的时限为3天"
            Response:
            ```json
            {
              "action": "update_attribute",
              "params": {
                "target": "activity",
                "activityId": "approve",
                "attribute": "timing.limit",
                "value": 3,
                "unit": "D"
              }
            }
            ```
            
            """;
    }

    public String buildUserPrompt(String userInput, DesignerContextDTO context) {
        StringBuilder prompt = new StringBuilder();
        
        if (context != null && context.getConversationHistory() != null 
            && !context.getConversationHistory().isEmpty()) {
            prompt.append("## Conversation History\n");
            for (Map<String, String> msg : context.getConversationHistory()) {
                String role = msg.getOrDefault("role", "unknown");
                String content = msg.getOrDefault("content", "");
                prompt.append(role).append(": ").append(content).append("\n");
            }
            prompt.append("\n");
        }
        
        prompt.append("## Current Request\n");
        prompt.append(userInput).append("\n");
        
        return prompt.toString();
    }

    public String buildValidationPrompt(ProcessDefDTO processDef) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("## Validation Request\n\n");
        prompt.append("Please validate the following process definition:\n\n");
        prompt.append("Process: ").append(processDef.getName()).append("\n");
        prompt.append("ID: ").append(processDef.getProcessDefId()).append("\n");
        prompt.append("Activities: ").append(processDef.getActivities() != null ? processDef.getActivities().size() : 0).append("\n");
        prompt.append("Routes: ").append(processDef.getRoutes() != null ? processDef.getRoutes().size() : 0).append("\n\n");
        
        prompt.append("Check for:\n");
        prompt.append("1. Missing required fields\n");
        prompt.append("2. Invalid enum values\n");
        prompt.append("3. Disconnected activities\n");
        prompt.append("4. Missing start/end nodes\n");
        prompt.append("5. Invalid route conditions\n");
        
        return prompt.toString();
    }

    public String buildSuggestionPrompt(DesignerContextDTO context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("## Suggestion Request\n\n");
        
        if (context.getCurrentProcess() != null) {
            prompt.append("Current process: ").append(context.getCurrentProcess().getName()).append("\n");
        }
        
        if (context.getCurrentActivity() != null) {
            prompt.append("Current activity: ").append(context.getCurrentActivity().getName()).append("\n");
            prompt.append("Activity type: ").append(context.getCurrentActivity().getActivityType()).append("\n");
        }
        
        prompt.append("\nPlease suggest:\n");
        prompt.append("1. Next logical steps\n");
        prompt.append("2. Missing configurations\n");
        prompt.append("3. Best practices to apply\n");
        prompt.append("4. Common patterns for this type\n");
        
        return prompt.toString();
    }
}
