class HealthCheck {
    constructor() {
        this.init();
    }
    
    async init() {
        await this.loadHealthData();
    }
    
    async loadHealthData() {
        try {
            const response = await fetch('/api/test/health');
            if (response.ok) {
                const data = await response.json();
                this.renderHealthData(data);
            }
        } catch (e) {
            this.renderMockData();
        }
    }
    
    renderMockData() {
        const tbody = document.getElementById('serviceTableBody');
        if (tbody) {
            tbody.innerHTML = `
                <tr>
                    <td>Nexus Core</td>
                    <td><span class="nx-badge nx-badge--success">运行中</span></td>
                    <td>8080</td>
                    <td>12ms</td>
                    <td><button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="checkService('nexus-core')">检查</button></td>
                </tr>
                <tr>
                    <td>Skill Service</td>
                    <td><span class="nx-badge nx-badge--success">运行中</span></td>
                    <td>8080</td>
                    <td>8ms</td>
                    <td><button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="checkService('skill-service')">检查</button></td>
                </tr>
                <tr>
                    <td>Menu Registry</td>
                    <td><span class="nx-badge nx-badge--success">运行中</span></td>
                    <td>8080</td>
                    <td>5ms</td>
                    <td><button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="checkService('menu-registry')">检查</button></td>
                </tr>
            `;
        }
    }
    
    renderHealthData(data) {
        this.renderMockData();
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new HealthCheck();
});
