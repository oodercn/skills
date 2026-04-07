class RoutePanel {
    constructor(container) {
        this.container = container;
    }

    render(activity) {
        if (!activity) {
            this.container.innerHTML = '<div class="d-empty">请选择活动</div>';
            return;
        }

        const routing = activity.routing || new RoutingConfig();

        this.container.innerHTML = `
            <div class="d-section">
                <div class="d-section-title">路由设置</div>
                <div class="d-section-content">
                    <div class="d-field">
                        <label class="d-label">等待合并</label>
                        <select class="d-select" id="fieldWaitMerge">
                            <option value="DEFAULT" ${routing.waitMerge === 'DEFAULT' ? 'selected' : ''}>默认</option>
                            <option value="AND" ${routing.waitMerge === 'AND' ? 'selected' : ''}>AND 合并</option>
                            <option value="XOR" ${routing.waitMerge === 'XOR' ? 'selected' : ''}>XOR 合并</option>
                        </select>
                    </div>
                    <div class="d-field">
                        <label class="d-label">并行处理</label>
                        <select class="d-select" id="fieldParallel">
                            <option value="DEFAULT" ${routing.parallelProcess === 'DEFAULT' ? 'selected' : ''}>默认</option>
                            <option value="AND" ${routing.parallelProcess === 'AND' ? 'selected' : ''}>AND 并行</option>
                            <option value="XOR" ${routing.parallelProcess === 'XOR' ? 'selected' : ''}>XOR 并行</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="d-section">
                <div class="d-section-title">退回设置</div>
                <div class="d-section-content">
                    <label class="d-checkbox">
                        <input type="checkbox" id="fieldAllowBack" ${routing.allowBack ? 'checked' : ''}>
                        <span>允许退回</span>
                    </label>
                    <div class="d-field" style="margin-top: 8px;">
                        <label class="d-label">退回路径</label>
                        <select class="d-select" id="fieldBackPath">
                            <option value="PREVIOUS" ${routing.backPath === 'PREVIOUS' ? 'selected' : ''}>上一活动</option>
                            <option value="SPONSOR" ${routing.backPath === 'SPONSOR' ? 'selected' : ''}>发起人</option>
                            <option value="ANY" ${routing.backPath === 'ANY' ? 'selected' : ''}>任意历史活动</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="d-section">
                <div class="d-section-title">特送设置</div>
                <div class="d-section-content">
                    <label class="d-checkbox">
                        <input type="checkbox" id="fieldAllowSpecial" ${routing.allowSpecialSend ? 'checked' : ''}>
                        <span>允许特送</span>
                    </label>
                    <label class="d-checkbox" style="margin-top: 8px;">
                        <input type="checkbox" id="fieldAllowSupplement" ${routing.allowSupplement ? 'checked' : ''}>
                        <span>允许补发</span>
                    </label>
                </div>
            </div>
        `;

        this._bindEvents(activity);
    }

    _bindEvents(activity) {
        const fields = ['fieldWaitMerge', 'fieldParallel', 'fieldAllowBack', 'fieldBackPath', 'fieldAllowSpecial', 'fieldAllowSupplement'];
        fields.forEach(id => {
            const el = this.container.querySelector('#' + id);
            if (el) {
                el.addEventListener('change', () => this._updateRouting(activity));
            }
        });
    }

    _updateRouting(activity) {
        activity.routing = {
            waitMerge: this.container.querySelector('#fieldWaitMerge').value,
            parallelProcess: this.container.querySelector('#fieldParallel').value,
            allowBack: this.container.querySelector('#fieldAllowBack').checked,
            backPath: this.container.querySelector('#fieldBackPath').value,
            allowSpecialSend: this.container.querySelector('#fieldAllowSpecial').checked,
            allowSupplement: this.container.querySelector('#fieldAllowSupplement').checked
        };
        
        if (window.app?.store) {
            window.app.store.updateActivity(activity);
        }
    }
}

window.RoutePanel = RoutePanel;
