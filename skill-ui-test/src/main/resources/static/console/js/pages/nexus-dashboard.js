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
            console.error('Failed to load dashboard data:', e);
            this.renderFallbackStats();
        }
    }
    
    renderFallbackStats() {
        const elements = {
            'agent-count': '0',
            'link-count': '0',
            'command-count': '0',
            'skill-count': '0'
        };
        
        for (const [id, value] of Object.entries(elements)) {
            const el = document.getElementById(id);
            if (el) el.textContent = value;
        }
    }
    
    renderStats(data) {
        if (data && data.peers) {
            const agentCount = document.getElementById('agent-count');
            if (agentCount) agentCount.textContent = data.peers.length || 0;
        }
        
        if (data && data.links) {
            const linkCount = document.getElementById('link-count');
            if (linkCount) linkCount.textContent = data.links.length || 0;
        }
        
        if (data && data.capabilities) {
            const commandCount = document.getElementById('command-count');
            if (commandCount) commandCount.textContent = data.capabilities.length || 0;
        }
        
        if (data && data.skills) {
            const skillCount = document.getElementById('skill-count');
            if (skillCount) skillCount.textContent = data.skills.length || 0;
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new NexusDashboard();
});
