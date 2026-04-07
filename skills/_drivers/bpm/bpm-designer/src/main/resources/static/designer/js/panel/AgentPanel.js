class AgentPanel {
    constructor(container) {
        this.container = container;
    }

    render(activity) {
        if (!activity) {
            this.container.innerHTML = '<div class="d-empty">请选择活动</div>';
            return;
        }

        const agent = activity.agentConfig || new AgentDef();

        this.container.innerHTML = `
            <div class="d-section">
                <div class="d-section-title">Agent 类型</div>
                <div class="d-section-content">
                    <div class="d-field">
                        <select class="d-select" id="fieldAgentType">
                            <option value="LLM" ${agent.agentType === 'LLM' ? 'selected' : ''}>LLM (大语言模型)</option>
                            <option value="TASK" ${agent.agentType === 'TASK' ? 'selected' : ''}>TASK (任务执行)</option>
                            <option value="EVENT" ${agent.agentType === 'EVENT' ? 'selected' : ''}>EVENT (事件触发)</option>
                            <option value="HYBRID" ${agent.agentType === 'HYBRID' ? 'selected' : ''}>HYBRID (混合模式)</option>
                            <option value="COORDINATOR" ${agent.agentType === 'COORDINATOR' ? 'selected' : ''}>COORDINATOR (协调器)</option>
                            <option value="TOOL" ${agent.agentType === 'TOOL' ? 'selected' : ''}>TOOL (工具调用)</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="d-section">
                <div class="d-section-title">调度策略</div>
                <div class="d-section-content">
                    <div class="d-field">
                        <select class="d-select" id="fieldSchedule">
                            <option value="SEQUENTIAL" ${agent.scheduleStrategy === 'SEQUENTIAL' ? 'selected' : ''}>顺序执行</option>
                            <option value="PARALLEL" ${agent.scheduleStrategy === 'PARALLEL' ? 'selected' : ''}>并行执行</option>
                            <option value="CONDITIONAL" ${agent.scheduleStrategy === 'CONDITIONAL' ? 'selected' : ''}>条件执行</option>
                            <option value="ROUND_ROBIN" ${agent.scheduleStrategy === 'ROUND_ROBIN' ? 'selected' : ''}>轮询执行</option>
                            <option value="PRIORITY" ${agent.scheduleStrategy === 'PRIORITY' ? 'selected' : ''}>优先级执行</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="d-section">
                <div class="d-section-title">协作模式</div>
                <div class="d-section-content">
                    <div class="d-field">
                        <select class="d-select" id="fieldCollab">
                            <option value="SOLO" ${agent.collaborationMode === 'SOLO' ? 'selected' : ''}>独立模式</option>
                            <option value="HIERARCHICAL" ${agent.collaborationMode === 'HIERARCHICAL' ? 'selected' : ''}>层级模式</option>
                            <option value="PEER" ${agent.collaborationMode === 'PEER' ? 'selected' : ''}>对等模式</option>
                            <option value="DEBATE" ${agent.collaborationMode === 'DEBATE' ? 'selected' : ''}>辩论模式</option>
                            <option value="VOTING" ${agent.collaborationMode === 'VOTING' ? 'selected' : ''}>投票模式</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="d-section">
                <div class="d-section-title">能力配置</div>
                <div class="d-section-content">
                    <div class="d-tag-group" id="capabilitiesList">
                        ${this._renderCapabilities(agent.capabilities)}
                    </div>
                </div>
            </div>
            <div class="d-section">
                <div class="d-section-title">LLM 配置</div>
                <div class="d-section-content">
                    <div class="d-field">
                        <label class="d-label">模型</label>
                        <select class="d-select" id="fieldModel">
                            <option value="gpt-4" ${agent.llmConfig?.model === 'gpt-4' ? 'selected' : ''}>GPT-4</option>
                            <option value="gpt-4-turbo" ${agent.llmConfig?.model === 'gpt-4-turbo' ? 'selected' : ''}>GPT-4 Turbo</option>
                            <option value="gpt-3.5-turbo" ${agent.llmConfig?.model === 'gpt-3.5-turbo' ? 'selected' : ''}>GPT-3.5 Turbo</option>
                            <option value="claude-3" ${agent.llmConfig?.model === 'claude-3' ? 'selected' : ''}>Claude 3</option>
                        </select>
                    </div>
                    <div class="d-row">
                        <div class="d-field">
                            <label class="d-label">温度</label>
                            <input type="number" class="d-input" id="fieldTemp" value="${agent.llmConfig?.temperature || 0.7}" min="0" max="2" step="0.1">
                        </div>
                        <div class="d-field">
                            <label class="d-label">最大Token</label>
                            <input type="number" class="d-input" id="fieldMaxTokens" value="${agent.llmConfig?.maxTokens || 2000}" min="100">
                        </div>
                    </div>
                    <label class="d-checkbox">
                        <input type="checkbox" id="fieldFuncCall" ${agent.llmConfig?.enableFunctionCalling ? 'checked' : ''}>
                        <span>启用函数调用</span>
                    </label>
                    <label class="d-checkbox" style="margin-top: 8px;">
                        <input type="checkbox" id="fieldStream" ${agent.llmConfig?.enableStreaming ? 'checked' : ''}>
                        <span>启用流式输出</span>
                    </label>
                </div>
            </div>
        `;

        this._bindEvents(activity);
    }

    _renderCapabilities(capabilities) {
        const allCaps = ['EMAIL', 'CALENDAR', 'DOCUMENT', 'ANALYSIS', 'SEARCH', 'NOTIFICATION', 'APPROVAL', 'SCHEDULING'];
        return allCaps.map(cap => `
            <label class="d-checkbox">
                <input type="checkbox" data-cap="${cap}" ${capabilities?.includes(cap) ? 'checked' : ''}>
                <span>${cap}</span>
            </label>
        `).join('');
    }

    _bindEvents(activity) {
        const selects = ['fieldAgentType', 'fieldSchedule', 'fieldCollab', 'fieldModel'];
        selects.forEach(id => {
            const el = this.container.querySelector('#' + id);
            if (el) {
                el.addEventListener('change', () => this._updateAgent(activity));
            }
        });

        const inputs = ['fieldTemp', 'fieldMaxTokens'];
        inputs.forEach(id => {
            const el = this.container.querySelector('#' + id);
            if (el) {
                el.addEventListener('input', () => this._updateAgent(activity));
            }
        });

        const checkboxes = ['fieldFuncCall', 'fieldStream'];
        checkboxes.forEach(id => {
            const el = this.container.querySelector('#' + id);
            if (el) {
                el.addEventListener('change', () => this._updateAgent(activity));
            }
        });

        const capCheckboxes = this.container.querySelectorAll('[data-cap]');
        capCheckboxes.forEach(el => {
            el.addEventListener('change', () => this._updateAgent(activity));
        });
    }

    _updateAgent(activity) {
        const capabilities = [];
        this.container.querySelectorAll('[data-cap]:checked').forEach(el => {
            capabilities.push(el.dataset.cap);
        });

        activity.agentConfig = {
            agentType: this.container.querySelector('#fieldAgentType').value,
            scheduleStrategy: this.container.querySelector('#fieldSchedule').value,
            collaborationMode: this.container.querySelector('#fieldCollab').value,
            capabilities: capabilities,
            llmConfig: {
                model: this.container.querySelector('#fieldModel').value,
                temperature: parseFloat(this.container.querySelector('#fieldTemp').value) || 0.7,
                maxTokens: parseInt(this.container.querySelector('#fieldMaxTokens').value) || 2000,
                enableFunctionCalling: this.container.querySelector('#fieldFuncCall').checked,
                enableStreaming: this.container.querySelector('#fieldStream').checked
            }
        };
        
        if (window.app?.store) {
            window.app.store.updateActivity(activity);
        }
    }
}

window.AgentPanel = AgentPanel;
