class Palette {
    constructor(container) {
        this.container = container;
        this.groups = [
            {
                name: '事件',
                items: [
                    { type: 'start', name: '开始', icon: 'play-circle', activityType: 'START' },
                    { type: 'end', name: '结束', icon: 'stop-circle', activityType: 'END' }
                ]
            },
            {
                name: '活动',
                items: [
                    { type: 'task', name: '用户任务', icon: 'user', activityType: 'TASK', category: 'HUMAN' },
                    { type: 'service', name: '服务任务', icon: 'server', activityType: 'SERVICE', category: 'AGENT' },
                    { type: 'script', name: '脚本任务', icon: 'code', activityType: 'SCRIPT', category: 'AGENT' }
                ]
            },
            {
                name: '网关',
                items: [
                    { type: 'gateway', name: '排他网关', icon: 'git-branch', activityType: 'XOR_GATEWAY' },
                    { type: 'gateway', name: '并行网关', icon: 'git-merge', activityType: 'AND_GATEWAY' },
                    { type: 'gateway', name: '包容网关', icon: 'git-commit', activityType: 'OR_GATEWAY' }
                ]
            },
            {
                name: 'Agent',
                items: [
                    { type: 'agent', name: 'LLM任务', icon: 'robot', activityType: 'LLM_TASK', category: 'AGENT' },
                    { type: 'agent', name: 'Agent任务', icon: 'cpu', activityType: 'AGENT_TASK', category: 'AGENT' },
                    { type: 'agent', name: '协调器', icon: 'team', activityType: 'COORDINATOR', category: 'AGENT' }
                ]
            },
            {
                name: '场景',
                items: [
                    { type: 'scene', name: '场景活动', icon: 'layout-grid', activityType: 'SCENE', category: 'SCENE' }
                ]
            }
        ];
        this.render();
        this._bindEvents();
    }

    render() {
        const groupsContainer = this.container.querySelector('#paletteGroups');
        if (!groupsContainer) return;

        groupsContainer.innerHTML = this.groups.map(group => `
            <div class="d-palette-group">
                <div class="d-palette-group-title">${group.name}</div>
                <div class="d-palette-items">
                    ${group.items.map(item => `
                        <div class="d-palette-item" 
                             data-type="${item.type}" 
                             data-activity-type="${item.activityType}"
                             data-category="${item.category || ''}"
                             draggable="true">
                            <span class="d-palette-item-icon">${Icons[item.icon] || ''}</span>
                            <span class="d-palette-item-name">${item.name}</span>
                        </div>
                    `).join('')}
                </div>
            </div>
        `).join('');
    }

    _bindEvents() {
        const items = this.container.querySelectorAll('.d-palette-item');
        items.forEach(item => {
            item.addEventListener('dragstart', (e) => {
                const data = {
                    activityType: item.dataset.activityType,
                    category: item.dataset.category,
                    name: item.querySelector('.d-palette-item-name').textContent
                };
                e.dataTransfer.setData('application/json', JSON.stringify(data));
                e.dataTransfer.effectAllowed = 'copy';
            });
        });

        const searchInput = this.container.querySelector('#paletteSearch');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                this._filterItems(e.target.value);
            });
        }
    }

    _filterItems(keyword) {
        const items = this.container.querySelectorAll('.d-palette-item');
        const lowerKeyword = keyword.toLowerCase();
        items.forEach(item => {
            const name = item.querySelector('.d-palette-item-name').textContent.toLowerCase();
            item.style.display = name.includes(lowerKeyword) ? '' : 'none';
        });
    }
}

window.Palette = Palette;
