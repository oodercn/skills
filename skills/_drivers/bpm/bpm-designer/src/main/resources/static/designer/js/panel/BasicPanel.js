class BasicPanel {
    constructor(container) {
        this.container = container;
    }

    render(activity) {
        if (!activity) {
            this.container.innerHTML = '<div class="d-empty">请选择活动</div>';
            return;
        }

        this.container.innerHTML = `
            <div class="d-section">
                <div class="d-section-title">基本信息</div>
                <div class="d-section-content">
                    <div class="d-field">
                        <label class="d-label">活动名称</label>
                        <input type="text" class="d-input" id="fieldName" value="${activity.name}">
                    </div>
                    <div class="d-field">
                        <label class="d-label">活动描述</label>
                        <input type="text" class="d-input" id="fieldDesc" value="${activity.description || ''}">
                    </div>
                    <div class="d-row">
                        <div class="d-field">
                            <label class="d-label">活动类型</label>
                            <select class="d-select" id="fieldType">
                                <option value="TASK" ${activity.activityType === 'TASK' ? 'selected' : ''}>用户任务</option>
                                <option value="SERVICE" ${activity.activityType === 'SERVICE' ? 'selected' : ''}>服务任务</option>
                                <option value="SCRIPT" ${activity.activityType === 'SCRIPT' ? 'selected' : ''}>脚本任务</option>
                                <option value="LLM_TASK" ${activity.activityType === 'LLM_TASK' ? 'selected' : ''}>LLM任务</option>
                                <option value="AGENT_TASK" ${activity.activityType === 'AGENT_TASK' ? 'selected' : ''}>Agent任务</option>
                            </select>
                        </div>
                        <div class="d-field">
                            <label class="d-label">活动分类</label>
                            <select class="d-select" id="fieldCategory">
                                <option value="HUMAN" ${activity.activityCategory === 'HUMAN' ? 'selected' : ''}>人工</option>
                                <option value="AGENT" ${activity.activityCategory === 'AGENT' ? 'selected' : ''}>Agent</option>
                                <option value="SCENE" ${activity.activityCategory === 'SCENE' ? 'selected' : ''}>场景</option>
                            </select>
                        </div>
                    </div>
                    <div class="d-field">
                        <label class="d-label">实现方式</label>
                        <select class="d-select" id="fieldImpl">
                            <option value="MANUAL" ${activity.implementation === 'MANUAL' ? 'selected' : ''}>手动活动</option>
                            <option value="AUTO" ${activity.implementation === 'AUTO' ? 'selected' : ''}>自动活动</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="d-section">
                <div class="d-section-title">执行者</div>
                <div class="d-section-content">
                    <div class="d-field">
                        <label class="d-label">执行者类型</label>
                        <select class="d-select" id="fieldPerformerType">
                            <option value="HUMAN" ${activity.performerType === 'HUMAN' ? 'selected' : ''}>人工</option>
                            <option value="AGENT" ${activity.performerType === 'AGENT' ? 'selected' : ''}>Agent</option>
                            <option value="DEVICE" ${activity.performerType === 'DEVICE' ? 'selected' : ''}>设备</option>
                        </select>
                    </div>
                </div>
            </div>
        `;

        this._bindEvents(activity);
    }

    _bindEvents(activity) {
        const fields = ['fieldName', 'fieldDesc', 'fieldType', 'fieldCategory', 'fieldImpl', 'fieldPerformerType'];
        fields.forEach(id => {
            const el = this.container.querySelector('#' + id);
            if (el) {
                el.addEventListener('change', () => this._updateActivity(activity));
                el.addEventListener('input', () => this._updateActivity(activity));
            }
        });
    }

    _updateActivity(activity) {
        activity.name = this.container.querySelector('#fieldName').value;
        activity.description = this.container.querySelector('#fieldDesc').value;
        activity.activityType = this.container.querySelector('#fieldType').value;
        activity.activityCategory = this.container.querySelector('#fieldCategory').value;
        activity.implementation = this.container.querySelector('#fieldImpl').value;
        activity.performerType = this.container.querySelector('#fieldPerformerType').value;
        
        if (window.app?.store) {
            window.app.store.updateActivity(activity);
        }
    }
}

window.BasicPanel = BasicPanel;
