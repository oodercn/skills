class NexusDashboard {
    constructor() {
        this.init();
    }
    
    async init() {
        await this.loadDashboardData();
    }
    
    async loadDashboardData() {
        try {
            const response = await fetch('/api/test/stats');
            if (response.ok) {
                const data = await response.json();
                this.renderStats(data);
            }
        } catch (e) {
            this.renderMockStats();
        }
    }
    
    renderMockStats() {
        const elements = {
            'agent-count': '12',
            'link-count': '8',
            'command-count': '256',
            'skill-count': '5'
        };
        
        for (const [id, value] of Object.entries(elements)) {
            const el = document.getElementById(id);
            if (el) el.textContent = value;
        }
    }
    
    renderStats(data) {
        this.renderMockStats();
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new NexusDashboard();
});
