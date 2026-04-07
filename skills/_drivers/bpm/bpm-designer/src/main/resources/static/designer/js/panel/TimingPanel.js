class TimingPanel {
    constructor(container) {
        this.container = container;
    }

    render(activity) {
        if (!activity) {
            this.container.innerHTML = '<div class="d-empty">请选择活动</div>';
            return;
        }

        const timing = activity.timing || new TimingConfig();

        this.container.innerHTML = `
            <div class="d-section">
                <div class="d-section-title">时限设置</div>
                <div class="d-section-content">
                    <div class="d-row">
                        <div class="d-field">
                            <label class="d-label">时间限制</label>
                            <input type="number" class="d-input" id="fieldDeadline" value="${timing.deadline}" min="0">
                        </div>
                        <div class="d-field">
                            <label class="d-label">单位</label>
                            <select class="d-select" id="fieldDeadlineUnit">
                                <option value="HOUR" ${timing.deadlineUnit === 'HOUR' ? 'selected' : ''}>小时</option>
                                <option value="DAY" ${timing.deadlineUnit === 'DAY' ? 'selected' : ''}>天</option>
                                <option value="WEEK" ${timing.deadlineUnit === 'WEEK' ? 'selected' : ''}>周</option>
                            </select>
                        </div>
                    </div>
                    <div class="d-row">
                        <div class="d-field">
                            <label class="d-label">报警时间</label>
                            <input type="number" class="d-input" id="fieldAlertTime" value="${timing.alertTime}" min="0">
                        </div>
                        <div class="d-field">
                            <label class="d-label">单位</label>
                            <select class="d-select" id="fieldAlertTimeUnit">
                                <option value="HOUR" ${timing.alertTimeUnit === 'HOUR' ? 'selected' : ''}>小时</option>
                                <option value="DAY" ${timing.alertTimeUnit === 'DAY' ? 'selected' : ''}>天</option>
                                <option value="WEEK" ${timing.alertTimeUnit === 'WEEK' ? 'selected' : ''}>周</option>
                            </select>
                        </div>
                    </div>
                    <div class="d-field">
                        <label class="d-label">到期处理</label>
                        <select class="d-select" id="fieldDeadlineOp">
                            <option value="EXTEND" ${timing.deadlineOperation === 'EXTEND' ? 'selected' : ''}>延期办理</option>
                            <option value="AUTO_COMPLETE" ${timing.deadlineOperation === 'AUTO_COMPLETE' ? 'selected' : ''}>自动完成</option>
                            <option value="AUTO_TERMINATE" ${timing.deadlineOperation === 'AUTO_TERMINATE' ? 'selected' : ''}>自动终止</option>
                            <option value="ESCALATE" ${timing.deadlineOperation === 'ESCALATE' ? 'selected' : ''}>升级处理</option>
                        </select>
                    </div>
                </div>
            </div>
        `;

        this._bindEvents(activity);
    }

    _bindEvents(activity) {
        const fields = ['fieldDeadline', 'fieldDeadlineUnit', 'fieldAlertTime', 'fieldAlertTimeUnit', 'fieldDeadlineOp'];
        fields.forEach(id => {
            const el = this.container.querySelector('#' + id);
            if (el) {
                el.addEventListener('change', () => this._updateTiming(activity));
            }
        });
    }

    _updateTiming(activity) {
        activity.timing = {
            deadline: parseInt(this.container.querySelector('#fieldDeadline').value) || 0,
            deadlineUnit: this.container.querySelector('#fieldDeadlineUnit').value,
            alertTime: parseInt(this.container.querySelector('#fieldAlertTime').value) || 0,
            alertTimeUnit: this.container.querySelector('#fieldAlertTimeUnit').value,
            deadlineOperation: this.container.querySelector('#fieldDeadlineOp').value
        };
        
        if (window.app?.store) {
            window.app.store.updateActivity(activity);
        }
    }
}

window.TimingPanel = TimingPanel;
