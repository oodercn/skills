class Elements {
    constructor(container, store) {
        this.container = container;
        this.store = store;
        this.groups = [
            {
                name: '事件',
                items: [
                    { type: 'start', name: '开始', icon: 'start', activityType: 'START', position: 'START', fillClass: 'd-node-fill-start', shapeClass: 'd-node-type-start' },
                    { type: 'end', name: '结束', icon: 'end', activityType: 'END', position: 'END', fillClass: 'd-node-fill-end', shapeClass: 'd-node-type-end' }
                ]
            },
            {
                name: '人工活动',
                items: [
                    { type: 'task', name: '用户任务', icon: 'user', activityType: 'TASK', position: 'NORMAL', category: 'HUMAN', implementation: 'IMPL_NO', fillClass: 'd-node-fill-task' }
                ]
            },
            {
                name: '自动活动',
                items: [
                    { type: 'service', name: '服务任务', icon: 'service', activityType: 'SERVICE', position: 'NORMAL', category: 'AGENT', implementation: 'IMPL_SERVICE', fillClass: 'd-node-fill-service' },
                    { type: 'script', name: '脚本任务', icon: 'script', activityType: 'SCRIPT', position: 'NORMAL', category: 'AGENT', implementation: 'IMPL_TOOL', fillClass: 'd-node-fill-script' }
                ]
            },
            {
                name: '子流程',
                items: [
                    { type: 'subprocess', name: '子流程', icon: 'subprocess', activityType: 'SUBPROCESS', position: 'NORMAL', category: 'SCENE', implementation: 'IMPL_SUBFLOW', fillClass: 'd-node-fill-subprocess' },
                    { type: 'call', name: '跳转流程', icon: 'external', activityType: 'CALL_ACTIVITY', position: 'NORMAL', category: 'SCENE', implementation: 'IMPL_OUTFLOW', fillClass: 'd-node-fill-subprocess' }
                ]
            },
            {
                name: 'Agent活动',
                items: [
                    { type: 'llm', name: 'LLM任务', icon: 'brain', activityType: 'LLM_TASK', position: 'NORMAL', category: 'AGENT', implementation: 'IMPL_TOOL', fillClass: 'd-node-fill-llm' },
                    { type: 'agent', name: 'Agent任务', icon: 'robot', activityType: 'AGENT_TASK', position: 'NORMAL', category: 'AGENT', implementation: 'IMPL_TOOL', fillClass: 'd-node-fill-agent' },
                    { type: 'coordinator', name: '协调器', icon: 'team', activityType: 'COORDINATOR', position: 'NORMAL', category: 'AGENT', implementation: 'IMPL_TOOL', fillClass: 'd-node-fill-coordinator' }
                ]
            },
            {
                name: '场景活动',
                items: [
                    { type: 'scene', name: '场景', icon: 'grid', activityType: 'SCENE', position: 'NORMAL', category: 'SCENE', implementation: 'IMPL_NO', fillClass: 'd-node-fill-scene' },
                    { type: 'block', name: '活动块', icon: 'block', activityType: 'ACTIVITY_BLOCK', position: 'NORMAL', category: 'SCENE', implementation: 'IMPL_NO', fillClass: 'd-node-fill-block' }
                ]
            }
        ];
        this.render();
        this._bindEvents();
    }

    render() {
        this.container.innerHTML = this.groups.map(group => `
            <div class="d-element-group">
                <div class="d-element-group-title">${group.name}</div>
                <div class="d-element-items">
                    ${group.items.map(item => `
                        <div class="d-element-item ${item.fillClass || ''} ${item.shapeClass || ''}" 
                             data-type="${item.type}"
                             data-activity-type="${item.activityType || ''}"
                             data-element-type="${item.elementType || ''}"
                             data-category="${item.category || ''}"
                             data-position="${item.position || ''}"
                             data-implementation="${item.implementation || ''}"
                             draggable="true">
                            <div class="d-element-icon">
                                ${IconManager.get(item.icon)}
                            </div>
                            <span class="d-element-name">${item.name}</span>
                        </div>
                    `).join('')}
                </div>
            </div>
        `).join('');
    }

    _bindEvents() {
        const items = this.container.querySelectorAll('.d-element-item');
        items.forEach(item => {
            item.addEventListener('dragstart', (e) => {
                const data = {
                    type: item.dataset.type,
                    activityType: item.dataset.activityType,
                    elementType: item.dataset.elementType,
                    category: item.dataset.category,
                    position: item.dataset.position,
                    implementation: item.dataset.implementation,
                    name: item.querySelector('span').textContent
                };
                e.dataTransfer.setData('application/json', JSON.stringify(data));
                e.dataTransfer.effectAllowed = 'copy';
            });
        });

        const searchInput = document.getElementById('elementSearch');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                this._filterItems(e.target.value);
            });
        }
    }

    _filterItems(keyword) {
        const items = this.container.querySelectorAll('.d-element-item');
        const lowerKeyword = keyword.toLowerCase();
        items.forEach(item => {
            const name = item.querySelector('span').textContent.toLowerCase();
            item.style.display = name.includes(lowerKeyword) ? '' : 'none';
        });
    }
}

window.Elements = Elements;
