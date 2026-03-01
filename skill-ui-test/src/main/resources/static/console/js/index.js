const IndexPage = {
    async init() {
        console.log('[Index] Initializing...');
        await this.loadRecentAccess();
        this.bindEvents();
    },
    
    async loadRecentAccess() {
        const recentAccessDiv = document.getElementById('recent-access');
        if (!recentAccessDiv) return;
        
        const recentItems = this.getRecentAccess();
        
        if (recentItems.length === 0) {
            recentAccessDiv.innerHTML = `
                <div class="empty-state">
                    <i class="ri-history-line"></i>
                    <p>暂无最近访问记录</p>
                </div>
            `;
            return;
        }
        
        recentAccessDiv.innerHTML = recentItems.map(item => `
            <div class="recent-item" data-type="${item.type}" data-url="${item.url}">
                <i class="${item.icon}"></i>
                <div class="recent-item-info">
                    <h4>${item.name}</h4>
                    <p>${item.description}</p>
                </div>
                <span class="recent-item-time">${this.formatTime(item.time)}</span>
            </div>
        `).join('');
    },
    
    getRecentAccess() {
        try {
            const data = localStorage.getItem('nexus-recent-access');
            return data ? JSON.parse(data) : [];
        } catch {
            return [];
        }
    },
    
    saveRecentAccess(item) {
        const recent = this.getRecentAccess();
        const existingIndex = recent.findIndex(i => i.type === item.type);
        
        if (existingIndex >= 0) {
            recent.splice(existingIndex, 1);
        }
        
        recent.unshift({
            ...item,
            time: Date.now()
        });
        
        localStorage.setItem('nexus-recent-access', JSON.stringify(recent.slice(0, 5)));
    },
    
    formatTime(timestamp) {
        const diff = Date.now() - timestamp;
        const minutes = Math.floor(diff / 60000);
        const hours = Math.floor(diff / 3600000);
        const days = Math.floor(diff / 86400000);
        
        if (minutes < 1) return '刚刚';
        if (minutes < 60) return `${minutes}分钟前`;
        if (hours < 24) return `${hours}小时前`;
        return `${days}天前`;
    },
    
    bindEvents() {
        document.querySelectorAll('.page-type-card').forEach(card => {
            card.addEventListener('click', (e) => {
                const type = card.getAttribute('onclick')?.match(/'(\w+)'/)?.[1];
                if (type) {
                    this.selectPageType(type);
                }
            });
        });
        
        document.querySelectorAll('.recent-item').forEach(item => {
            item.addEventListener('click', () => {
                const url = item.getAttribute('data-url');
                if (url) {
                    window.location.href = url;
                }
            });
        });
    },
    
    selectPageType(type) {
        const pageConfig = {
            home: {
                name: '家庭管理中心',
                icon: 'ri-home-smile-line',
                url: 'pages/home/index.html'
            },
            lan: {
                name: '局域网管理中心',
                icon: 'ri-wifi-line',
                url: 'pages/lan/index.html'
            }
        };
        
        const config = pageConfig[type];
        if (config) {
            this.saveRecentAccess({
                type: type,
                name: config.name,
                icon: config.icon,
                description: '管理中心',
                url: config.url
            });
            
            window.location.href = config.url;
        }
    }
};

function selectPageType(type) {
    IndexPage.selectPageType(type);
}

document.addEventListener('DOMContentLoaded', () => {
    IndexPage.init();
});

window.IndexPage = IndexPage;
