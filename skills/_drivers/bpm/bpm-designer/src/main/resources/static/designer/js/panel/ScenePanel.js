class ScenePanel {
    constructor(container) {
        this.container = container;
    }

    render(activity) {
        if (!activity) {
            this.container.innerHTML = '<div class="d-empty">请选择活动</div>';
            return;
        }

        const scene = activity.sceneConfig || new SceneDef();

        this.container.innerHTML = `
            <div class="d-section">
                <div class="d-section-title">场景定义</div>
                <div class="d-section-content">
                    <div class="d-field">
                        <label class="d-label">场景ID</label>
                        <input type="text" class="d-input" id="fieldSceneId" value="${scene.sceneId || ''}">
                    </div>
                    <div class="d-field">
                        <label class="d-label">场景名称</label>
                        <input type="text" class="d-input" id="fieldSceneName" value="${scene.name || ''}">
                    </div>
                    <div class="d-field">
                        <label class="d-label">场景类型</label>
                        <select class="d-select" id="fieldSceneType">
                            <option value="FORM" ${scene.sceneType === 'FORM' ? 'selected' : ''}>表单场景</option>
                            <option value="LIST" ${scene.sceneType === 'LIST' ? 'selected' : ''}>列表场景</option>
                            <option value="DASHBOARD" ${scene.sceneType === 'DASHBOARD' ? 'selected' : ''}>仪表盘场景</option>
                            <option value="CUSTOM" ${scene.sceneType === 'CUSTOM' ? 'selected' : ''}>自定义场景</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="d-section">
                <div class="d-section-title">PageAgent 配置</div>
                <div class="d-section-content">
                    <div class="d-field">
                        <label class="d-label">Agent ID</label>
                        <input type="text" class="d-input" id="fieldPageAgentId" value="${scene.pageAgent?.agentId || ''}">
                    </div>
                    <div class="d-field">
                        <label class="d-label">页面ID</label>
                        <input type="text" class="d-input" id="fieldPageId" value="${scene.pageAgent?.pageId || ''}">
                    </div>
                    <div class="d-field">
                        <label class="d-label">页面类型</label>
                        <select class="d-select" id="fieldPageType">
                            <option value="form" ${scene.pageAgent?.pageType === 'form' ? 'selected' : ''}>表单</option>
                            <option value="list" ${scene.pageAgent?.pageType === 'list' ? 'selected' : ''}>列表</option>
                            <option value="custom" ${scene.pageAgent?.pageType === 'custom' ? 'selected' : ''}>自定义</option>
                        </select>
                    </div>
                    <div class="d-field">
                        <label class="d-label">模板路径</label>
                        <input type="text" class="d-input" id="fieldTplPath" value="${scene.pageAgent?.templatePath || ''}" placeholder="/templates/...">
                    </div>
                </div>
            </div>
            <div class="d-section">
                <div class="d-section-title">存储配置</div>
                <div class="d-section-content">
                    <div class="d-field">
                        <label class="d-label">存储类型</label>
                        <select class="d-select" id="fieldStorageType">
                            <option value="VFS" ${scene.storage?.type === 'VFS' ? 'selected' : ''}>VFS 文件存储</option>
                            <option value="SQL" ${scene.storage?.type === 'SQL' ? 'selected' : ''}>SQL 数据库</option>
                            <option value="HYBRID" ${scene.storage?.type === 'HYBRID' ? 'selected' : ''}>混合存储</option>
                        </select>
                    </div>
                    <div class="d-field">
                        <label class="d-label">VFS 路径</label>
                        <input type="text" class="d-input" id="fieldVfsPath" value="${scene.storage?.vfsPath || ''}" placeholder="/skills/...">
                    </div>
                </div>
            </div>
        `;

        this._bindEvents(activity);
    }

    _bindEvents(activity) {
        const fields = ['fieldSceneId', 'fieldSceneName', 'fieldSceneType', 'fieldPageAgentId', 'fieldPageId', 'fieldPageType', 'fieldTplPath', 'fieldStorageType', 'fieldVfsPath'];
        fields.forEach(id => {
            const el = this.container.querySelector('#' + id);
            if (el) {
                el.addEventListener('change', () => this._updateScene(activity));
                el.addEventListener('input', () => this._updateScene(activity));
            }
        });
    }

    _updateScene(activity) {
        activity.sceneConfig = {
            sceneId: this.container.querySelector('#fieldSceneId').value,
            name: this.container.querySelector('#fieldSceneName').value,
            sceneType: this.container.querySelector('#fieldSceneType').value,
            pageAgent: {
                agentId: this.container.querySelector('#fieldPageAgentId').value,
                pageId: this.container.querySelector('#fieldPageId').value,
                pageType: this.container.querySelector('#fieldPageType').value,
                templatePath: this.container.querySelector('#fieldTplPath').value
            },
            storage: {
                type: this.container.querySelector('#fieldStorageType').value,
                vfsPath: this.container.querySelector('#fieldVfsPath').value
            }
        };
        
        if (window.app?.store) {
            window.app.store.updateActivity(activity);
        }
    }
}

window.ScenePanel = ScenePanel;
