/**
 * AI招聘助手
 * 参考 skill-llm-assistant-ui 实现
 * 集成LLM聊天功能到招聘管理系统
 */
const RecruitmentAIAssistant = {
    messages: [],
    context: null,
    settings: {
        provider: 'deepseek',
        model: 'deepseek-chat',
        temperature: 0.7,
        maxTokens: 4096,
        stream: false
    },
    providers: [],
    isStreaming: false,
    isOpen: false,
    isMinimized: false,
    
    init() {
        this.loadSettings();
        this.loadState();
        this.loadProviders();
        this.bindEvents();
        
        if (this.isOpen) {
            this.open();
        }
        
        this.refreshContext();
    },
    
    loadSettings() {
        const saved = localStorage.getItem('recruitment-ai-settings');
        if (saved) {
            try {
                const settings = JSON.parse(saved);
                this.settings = { ...this.settings, ...settings };
            } catch (e) {
                console.error('Failed to load settings:', e);
            }
        }
    },
    
    saveSettings() {
        localStorage.setItem('recruitment-ai-settings', JSON.stringify(this.settings));
    },
    
    loadState() {
        const saved = localStorage.getItem('recruitment-ai-state');
        if (saved) {
            try {
                const state = JSON.parse(saved);
                this.isOpen = state.isOpen || false;
                this.messages = state.messages || [];
            } catch (e) {
                console.error('Failed to load state:', e);
            }
        }
    },
    
    saveState() {
        localStorage.setItem('recruitment-ai-state', JSON.stringify({
            isOpen: this.isOpen,
            messages: this.messages.slice(-20)
        }));
    },
    
    async loadProviders() {
        try {
            // 使用 LlmChatController 提供的 API
            const result = await this.promisify(NexusAPI, 'get', '/api/llm/providers');
            if (result.status === 'success' && result.data) {
                // LlmChatController 返回的是数组格式，需要转换
                this.providers = result.data.map(p => ({
                    type: p.id,
                    name: p.name,
                    models: p.models || []
                }));
                this.renderProviders();
            } else {
                // 使用默认配置
                this.useDefaultProviders();
            }
        } catch (error) {
            console.error('Failed to load providers:', error);
            this.useDefaultProviders();
        }
    },
    
    useDefaultProviders() {
        this.providers = [
            { type: 'deepseek', name: 'DeepSeek', models: ['deepseek-chat', 'deepseek-coder'] },
            { type: 'openai', name: 'OpenAI', models: ['gpt-4', 'gpt-3.5-turbo'] },
            { type: 'baidu', name: '百度文心', models: ['ernie-bot'] },
            { type: 'qianwen', name: '通义千问', models: ['qwen-turbo', 'qwen-plus', 'qwen-max'] }
        ];
        this.renderProviders();
    },
    
    renderProviders() {
        const providerSelect = document.getElementById('llm-provider');
        const modelSelect = document.getElementById('llm-model');
        
        if (!providerSelect) return;
        
        providerSelect.innerHTML = '';
        this.providers.forEach(provider => {
            const option = document.createElement('option');
            option.value = provider.type;
            option.textContent = provider.name || this.getProviderDisplayName(provider.type);
            if (provider.type === this.settings.provider) {
                option.selected = true;
            }
            providerSelect.appendChild(option);
        });
        
        this.updateModelSelect();
    },
    
    getProviderDisplayName(type) {
        const names = {
            'openai': 'OpenAI',
            'qianwen': '通义千问',
            'deepseek': 'DeepSeek',
            'baidu': '百度文心'
        };
        return names[type] || type;
    },
    
    updateModelSelect() {
        const providerSelect = document.getElementById('llm-provider');
        const modelSelect = document.getElementById('llm-model');
        
        if (!providerSelect || !modelSelect) return;
        
        const selectedProvider = providerSelect.value;
        const provider = this.providers.find(p => p.type === selectedProvider);
        
        modelSelect.innerHTML = '';
        if (provider && provider.models) {
            provider.models.forEach(model => {
                const option = document.createElement('option');
                option.value = model;
                option.textContent = model;
                if (model === this.settings.model) {
                    option.selected = true;
                }
                modelSelect.appendChild(option);
            });
        }
    },
    
    changeProvider() {
        const providerSelect = document.getElementById('llm-provider');
        this.settings.provider = providerSelect.value;
        this.updateModelSelect();
        this.saveSettings();
    },
    
    changeModel() {
        const modelSelect = document.getElementById('llm-model');
        this.settings.model = modelSelect.value;
        this.saveSettings();
    },
    
    bindEvents() {
        // 输入框回车发送
        const input = document.getElementById('llm-input');
        if (input) {
            input.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    this.sendMessage();
                }
            });
            
            // 自动调整高度
            input.addEventListener('input', () => {
                input.style.height = 'auto';
                input.style.height = Math.min(input.scrollHeight, 120) + 'px';
            });
        }
    },
    
    toggle() {
        if (this.isOpen) {
            this.close();
        } else {
            this.open();
        }
    },
    
    open() {
        const panel = document.getElementById('llm-panel');
        const trigger = document.getElementById('llm-trigger');
        
        if (panel) {
            panel.classList.add('llm-panel--open');
            this.isOpen = true;
            this.saveState();
            
            if (trigger) {
                trigger.style.display = 'none';
            }
            
            this.renderMessages();
            this.refreshContext();
            
            // 聚焦输入框
            setTimeout(() => {
                document.getElementById('llm-input')?.focus();
            }, 100);
        }
    },
    
    close() {
        const panel = document.getElementById('llm-panel');
        const trigger = document.getElementById('llm-trigger');
        
        if (panel) {
            panel.classList.remove('llm-panel--open');
            this.isOpen = false;
            this.saveState();
            
            if (trigger) {
                trigger.style.display = 'flex';
            }
        }
    },
    
    minimize() {
        const panel = document.getElementById('llm-panel');
        const body = document.getElementById('llm-body');
        const footer = panel?.querySelector('.llm-floating-footer');
        
        if (panel) {
            panel.classList.toggle('llm-panel--minimized');
            this.isMinimized = panel.classList.contains('llm-panel--minimized');
            
            if (body) body.style.display = this.isMinimized ? 'none' : 'flex';
            if (footer) footer.style.display = this.isMinimized ? 'none' : 'block';
        }
    },
    
    toggleSettings() {
        alert('设置功能开发中...\n\n当前配置：\n提供商: ' + this.getProviderDisplayName(this.settings.provider) + '\n模型: ' + this.settings.model);
    },
    
    async refreshContext() {
        const statusEl = document.getElementById('llm-context-text');
        
        if (statusEl) {
            statusEl.innerHTML = '<i class="ri-loader-4-line ri-spin"></i> 获取上下文...';
        }
        
        // 收集招聘上下文
        this.context = this.collectRecruitmentContext();
        
        if (statusEl) {
            const jobCount = this.context.jobs?.length || 0;
            const resumeCount = this.context.resumes?.length || 0;
            statusEl.innerHTML = `已加载: ${jobCount}个职位, ${resumeCount}份简历`;
        }
    },
    
    collectRecruitmentContext() {
        const context = {
            page: '招聘管理系统',
            timestamp: new Date().toLocaleString('zh-CN'),
            jobs: [],
            resumes: [],
            interviews: [],
            statistics: {}
        };
        
        // 尝试从RecruitmentManagement获取数据
        if (typeof RecruitmentManagement !== 'undefined') {
            context.jobs = RecruitmentManagement.jobs || [];
            context.resumes = RecruitmentManagement.resumes || [];
            context.interviews = RecruitmentManagement.interviews || [];
            context.offers = RecruitmentManagement.offers || [];
        }
        
        // 计算统计信息
        context.statistics = {
            totalJobs: context.jobs.length,
            activeJobs: context.jobs.filter(j => j.status === 'active').length,
            totalResumes: context.resumes.length,
            newResumes: context.resumes.filter(r => r.status === 'new').length,
            pendingInterviews: context.interviews.filter(i => i.status === 'scheduled').length,
            completedInterviews: context.interviews.filter(i => i.status === 'completed').length
        };
        
        return context;
    },
    
    async sendMessage() {
        const input = document.getElementById('llm-input');
        const message = input?.value.trim();
        
        if (!message || this.isStreaming) return;
        
        // 添加用户消息
        this.addMessage('user', message);
        input.value = '';
        input.style.height = 'auto';
        
        // 显示加载状态
        this.isStreaming = true;
        this.updateSendButton();
        
        try {
            // 构建系统提示词
            const systemPrompt = this.buildSystemPrompt();
            
            // 调用LLM API - 使用 LlmChatController 提供的接口
            const request = {
                message: message,
                provider: this.settings.provider,
                model: this.settings.model,
                temperature: this.settings.temperature,
                maxTokens: this.settings.maxTokens,
                systemPrompt: systemPrompt
            };
            
            const result = await this.promisify(NexusAPI, 'post', '/api/llm/chat', request);
            
            if (result.status === 'success' && result.data) {
                this.addMessage('assistant', result.data.response);
            } else {
                // 降级到本地回复
                const localResponse = this.generateLocalResponse(message);
                this.addMessage('assistant', localResponse);
            }
        } catch (error) {
            console.error('LLM调用失败:', error);
            // 降级到本地回复
            const localResponse = this.generateLocalResponse(message);
            this.addMessage('assistant', localResponse);
        } finally {
            this.isStreaming = false;
            this.updateSendButton();
        }
    },
    
    quickAsk(question) {
        const input = document.getElementById('llm-input');
        if (input) {
            input.value = question;
            this.sendMessage();
        }
    },
    
    buildSystemPrompt() {
        const ctx = this.context || {};
        const stats = ctx.statistics || {};
        
        return `你是AI招聘助手，专门帮助HR和招聘经理优化招聘流程。

当前招聘概况：
- 在招职位: ${stats.activeJobs || 0}个
- 收到简历: ${stats.totalResumes || 0}份
- 待面试: ${stats.pendingInterviews || 0}人
- 已完成面试: ${stats.completedInterviews || 0}人

你可以帮助用户：
1. 分析简历与职位的匹配度
2. 生成针对性的面试问题
3. 评估候选人的优缺点
4. 提供招聘策略建议
5. 分析招聘数据和趋势

请基于以上信息，提供专业、实用的招聘建议。`;
    },
    
    generateLocalResponse(message) {
        const lowerMsg = message.toLowerCase();
        const ctx = this.context || {};
        const stats = ctx.statistics || {};
        
        // 招聘进度相关
        if (lowerMsg.includes('进度') || lowerMsg.includes('情况') || lowerMsg.includes('如何')) {
            return `当前招聘进度如下：

📊 **整体概况**
- 在招职位: ${stats.activeJobs}个
- 收到简历: ${stats.totalResumes}份
- 新简历: ${stats.newResumes}份
- 待面试: ${stats.pendingInterviews}人
- 已完成面试: ${stats.completedInterviews}人

💡 **建议**
${stats.newResumes > 5 ? '新简历较多，建议加快筛选速度。' : '简历量正常，保持当前节奏。'}
${stats.pendingInterviews > 3 ? '面试积压较多，建议增加面试官或调整时间安排。' : '面试安排合理。'}`;
        }
        
        // 简历分析相关
        if (lowerMsg.includes('简历') || lowerMsg.includes('匹配')) {
            const resumes = ctx.resumes || [];
            if (resumes.length === 0) {
                return '目前没有简历数据。建议发布更多职位或拓宽招聘渠道。';
            }
            
            const highMatch = resumes.filter(r => (r.matchScore || 0) >= 80).length;
            const mediumMatch = resumes.filter(r => {
                const score = r.matchScore || 0;
                return score >= 60 && score < 80;
            }).length;
            const lowMatch = resumes.filter(r => (r.matchScore || 0) < 60).length;
            
            return `📋 **简历质量分析**

当前共有${resumes.length}份简历：
- 高匹配(80%+): ${highMatch}份
- 中匹配(60-79%): ${mediumMatch}份  
- 低匹配(<60%): ${lowMatch}份

💡 **建议**
${highMatch > 0 ? `有${highMatch}份高匹配简历，建议优先安排面试。` : '高匹配简历较少，建议调整职位要求或扩大搜索范围。'}
${lowMatch > resumes.length * 0.5 ? '低匹配简历比例较高，建议优化职位描述，明确核心要求。' : ''}`;
        }
        
        // 面试题生成
        if (lowerMsg.includes('面试') || lowerMsg.includes('问题') || lowerMsg.includes('题目')) {
            return `🎯 **Java高级工程师面试题建议**

**技术基础**
1. Java集合框架中ArrayList和LinkedList的区别及使用场景
2. HashMap的底层实现原理，如何解决哈希冲突
3. JVM内存模型及垃圾回收机制
4. 多线程编程中的线程安全问题及解决方案

**框架与中间件**
5. Spring IOC和AOP的实现原理
6. MyBatis的工作原理及缓存机制
7. Redis的数据类型及使用场景
8. Kafka的消息可靠性保证机制

**系统设计**
9. 如何设计一个高并发的秒杀系统
10. 微服务架构中的服务发现与治理

**项目经验**
11. 请介绍你最熟悉的项目，你在其中的角色和贡献
12. 遇到过什么技术难题，如何解决的

需要针对其他职位的面试题吗？`;
        }
        
        // 默认回复
        return `我理解你的问题："${message}"

基于当前招聘数据，我可以为你提供以下帮助：

1. 📊 查看招聘进度和数据分析
2. 📋 分析简历质量和匹配度
3. 🎯 生成面试题目
4. 💡 提供招聘策略建议

请告诉我具体想了解哪方面的信息？`;
    },
    
    addMessage(role, content) {
        const message = {
            id: Date.now(),
            role,
            content,
            timestamp: new Date().toLocaleTimeString('zh-CN')
        };
        
        this.messages.push(message);
        this.saveState();
        this.renderMessages();
    },
    
    renderMessages() {
        const container = document.getElementById('llm-messages');
        if (!container) return;
        
        // 保留欢迎消息
        const welcomeEl = container.querySelector('.llm-message--system');
        
        container.innerHTML = '';
        
        // 添加欢迎消息
        if (welcomeEl) {
            container.appendChild(welcomeEl);
        } else {
            container.innerHTML = `
                <div class="llm-message llm-message--system">
                    <div class="llm-message-avatar">
                        <i class="ri-robot-2-line"></i>
                    </div>
                    <div class="llm-message-content">
                        <p>你好！我是AI招聘助手，可以帮助你：</p>
                        <ul>
                            <li>分析简历匹配度</li>
                            <li>生成面试问题</li>
                            <li>评估候选人</li>
                            <li>提供招聘建议</li>
                        </ul>
                        <p>请告诉我你需要什么帮助？</p>
                    </div>
                </div>
            `;
        }
        
        // 添加历史消息
        this.messages.forEach(msg => {
            const msgEl = document.createElement('div');
            msgEl.className = `llm-message llm-message--${msg.role}`;
            
            if (msg.role === 'user') {
                msgEl.innerHTML = `
                    <div class="llm-message-content">${this.escapeHtml(msg.content)}</div>
                `;
            } else {
                msgEl.innerHTML = `
                    <div class="llm-message-avatar">
                        <i class="ri-robot-2-line"></i>
                    </div>
                    <div class="llm-message-content">${this.formatMessage(msg.content)}</div>
                `;
            }
            
            container.appendChild(msgEl);
        });
        
        // 滚动到底部
        container.scrollTop = container.scrollHeight;
    },
    
    formatMessage(content) {
        // 简单的Markdown格式转换
        let formatted = this.escapeHtml(content);
        
        // 粗体
        formatted = formatted.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>');
        
        // 换行
        formatted = formatted.replace(/\n/g, '<br>');
        
        return formatted;
    },
    
    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    },
    
    updateSendButton() {
        const btn = document.getElementById('llm-send-btn');
        if (btn) {
            btn.innerHTML = this.isStreaming 
                ? '<i class="ri-loader-4-line ri-spin"></i>' 
                : '<i class="ri-send-plane-fill"></i>';
            btn.disabled = this.isStreaming;
        }
    },
    
    // ========== 工具函数 ==========
    promisify(obj, method, ...args) {
        return new Promise((resolve, reject) => {
            // NexusAPI 使用单参数回调格式 callback(result)
            obj[method](...args, (result) => {
                resolve(result || { status: 'success', data: null });
            });
        });
    }
};

// 初始化
document.addEventListener('DOMContentLoaded', () => {
    RecruitmentAIAssistant.init();
});
