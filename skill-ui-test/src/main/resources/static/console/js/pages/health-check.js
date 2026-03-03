class HealthCheck {
    constructor() {
        this.init();
    }
    
    async init() {
        await this.loadHealthData();
    }
    
    async loadHealthData() {
        try {
            const response = await fetch('/api/system/health');
            if (response.ok) {
                const data = await response.json();
                this.renderHealthData(data);
            }
        } catch (e) {
            console.error('Failed to load health data:', e);
            this.renderErrorData();
        }
    }
    
    renderErrorData() {
        const tbody = document.getElementById('serviceTableBody');
        if (tbody) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="5" style="text-align: center; color: #999;">
                        <i class="ri-error-warning-line"></i> 无法加载服务状态
                    </td>
                </tr>
            `;
        }
    }
    
    renderHealthData(data) {
        const tbody = document.getElementById('serviceTableBody');
        if (!tbody) return;
        
        let html = '';
        
        const sdkStatus = data.sdkInitialized ? 'success' : 'warning';
        const sdkStatusText = data.sdkInitialized ? '已初始化' : '未初始化';
        html += `
            <tr>
                <td>SDK Core</td>
                <td><span class="nx-badge nx-badge--${sdkStatus}">${sdkStatusText}</span></td>
                <td>8080</td>
                <td>N/A</td>
                <td><button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="checkService('sdk-core')">检查</button></td>
            </tr>
        `;
        
        const networkStatus = data.networkServiceReady ? 'success' : 'warning';
        const networkStatusText = data.networkServiceReady ? '运行中' : '未就绪';
        html += `
            <tr>
                <td>Network Service</td>
                <td><span class="nx-badge nx-badge--${networkStatus}">${networkStatusText}</span></td>
                <td>8080</td>
                <td>N/A</td>
                <td><button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="checkService('network-service')">检查</button></td>
            </tr>
        `;
        
        const discoveryStatus = data.discoveryProtocolReady ? 'success' : 'warning';
        const discoveryStatusText = data.discoveryProtocolReady ? '运行中' : '未就绪';
        html += `
            <tr>
                <td>Discovery Protocol</td>
                <td><span class="nx-badge nx-badge--${discoveryStatus}">${discoveryStatusText}</span></td>
                <td>8080</td>
                <td>N/A</td>
                <td><button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="checkService('discovery-protocol')">检查</button></td>
            </tr>
        `;
        
        const a2aStatus = data.a2aCommunicationReady ? 'success' : 'warning';
        const a2aStatusText = data.a2aCommunicationReady ? '运行中' : '未就绪';
        html += `
            <tr>
                <td>A2A Communication</td>
                <td><span class="nx-badge nx-badge--${a2aStatus}">${a2aStatusText}</span></td>
                <td>8080</td>
                <td>N/A</td>
                <td><button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="checkService('a2a-communication')">检查</button></td>
            </tr>
        `;
        
        tbody.innerHTML = html;
    }
}

function checkService(serviceName) {
    fetch('/api/system/health')
        .then(response => response.json())
        .then(data => {
            alert(`服务 ${serviceName} 状态: ${data.status}\nSDK初始化: ${data.sdkInitialized}`);
        })
        .catch(e => {
            alert(`检查服务 ${serviceName} 失败: ${e.message}`);
        });
}

document.addEventListener('DOMContentLoaded', () => {
    new HealthCheck();
});
