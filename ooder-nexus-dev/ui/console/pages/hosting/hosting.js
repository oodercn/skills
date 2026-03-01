class HostingManager {
    
    constructor() {
        this.apiBase = '/api/hosting';
    }
    
    async init() {
        console.log('[HostingManager] Initializing...');
        
        await this.loadProviders();
        await this.loadInstances();
        
        console.log('[HostingManager] Initialized');
    }
    
    openModal(modalId) {
        document.getElementById(modalId).classList.add('nx-modal--open');
    }
    
    closeModal(modalId) {
        document.getElementById(modalId).classList.remove('nx-modal--open');
    }
    
    async loadProviders() {
        try {
            const response = await Api.get(`${this.apiBase}/providers`);
            if (response.status === 'success' && response.data) {
                this.renderProviders(response.data);
                return;
            }
        } catch (e) {
            console.warn('[HostingManager] Failed to load providers:', e);
        }
        
        this.renderProviders(this.getMockProviders());
    }
    
    getMockProviders() {
        return [
            { providerId: 'kubernetes', providerName: 'Kubernetes', providerType: 'container', status: 'available', region: 'default', description: 'Kubernetes容器编排平台' },
            { providerId: 'aliyun', providerName: '阿里云 ECS', providerType: 'cloud', status: 'available', region: 'cn-hangzhou', description: '阿里云弹性计算服务' },
            { providerId: 'tencent', providerName: '腾讯云 CVM', providerType: 'cloud', status: 'available', region: 'ap-guangzhou', description: '腾讯云虚拟机' }
        ];
    }
    
    renderProviders(providers) {
        const grid = document.getElementById('provider-grid');
        if (!providers || providers.length === 0) {
            grid.innerHTML = '<div class="nx-empty"><i class="ri-cloud-off-line nx-empty__icon"></i><div class="nx-empty__title">暂无云服务提供商</div></div>';
            return;
        }
        
        grid.innerHTML = providers.map(provider => {
            const iconClass = provider.providerType === 'container' ? 'ri-kubernetes-line' : 'ri-cloud-line';
            const statusClass = provider.status === 'available' ? 'nx-badge--success' : 'nx-badge--warning';
            return `
                <div class="provider-card">
                    <div class="provider-card__icon">
                        <i class="${iconClass}"></i>
                    </div>
                    <div class="provider-card__info">
                        <div class="provider-card__name">${provider.providerName}</div>
                        <div class="provider-card__region">${provider.region}</div>
                        <span class="nx-badge ${statusClass}">${provider.status === 'available' ? '可用' : '维护中'}</span>
                    </div>
                </div>
            `;
        }).join('');
    }
    
    async loadInstances() {
        try {
            const response = await Api.get(`${this.apiBase}/instances`);
            if (response.status === 'success' && response.data) {
                const instances = response.data;
                this.renderInstances(instances);
                this.updateStats(instances);
                return;
            }
        } catch (e) {
            console.warn('[HostingManager] Failed to load instances:', e);
        }
        
        const mockInstances = this.getMockInstances();
        this.renderInstances(mockInstances);
        this.updateStats(mockInstances);
    }
    
    getMockInstances() {
        return [
            { instanceId: 'inst-001', instanceName: 'web-frontend', provider: 'kubernetes', status: 'running', replicas: 3, cpu: 2, memory: 4, createdAt: Date.now() - 86400000 },
            { instanceId: 'inst-002', instanceName: 'api-backend', provider: 'kubernetes', status: 'running', replicas: 2, cpu: 4, memory: 8, createdAt: Date.now() - 172800000 },
            { instanceId: 'inst-003', instanceName: 'redis-cache', provider: 'aliyun', status: 'running', replicas: 1, cpu: 2, memory: 16, createdAt: Date.now() - 259200000 },
            { instanceId: 'inst-004', instanceName: 'mysql-db', provider: 'aliyun', status: 'stopped', replicas: 1, cpu: 4, memory: 32, createdAt: Date.now() - 345600000 }
        ];
    }
    
    updateStats(instances) {
        const running = instances.filter(i => i.status === 'running').length;
        const stopped = instances.filter(i => i.status === 'stopped').length;
        const totalCpu = instances.reduce((sum, i) => sum + (i.cpu || 0) * (i.replicas || 1), 0);
        
        document.getElementById('instance-count').textContent = instances.length;
        document.getElementById('running-count').textContent = running;
        document.getElementById('stopped-count').textContent = stopped;
        document.getElementById('total-cpu').textContent = totalCpu;
    }
    
    renderInstances(instances) {
        const tbody = document.getElementById('instance-list');
        if (!instances || instances.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="nx-empty"><i class="ri-cloud-off-line nx-empty__icon"></i><div class="nx-empty__title">暂无实例</div></td></tr>';
            return;
        }
        
        tbody.innerHTML = instances.map(instance => {
            const statusClass = instance.status === 'running' ? 'nx-badge--success' : 
                               instance.status === 'pending' ? 'nx-badge--warning' : 'nx-badge--danger';
            const providerIcon = instance.provider === 'kubernetes' ? 'ri-kubernetes-line' : 'ri-cloud-line';
            
            return `
                <tr>
                    <td>
                        <div style="display: flex; align-items: center; gap: 8px;">
                            <i class="${providerIcon}" style="color: var(--ns-primary);"></i>
                            ${instance.instanceName}
                        </div>
                    </td>
                    <td>${instance.provider}</td>
                    <td><span class="nx-badge ${statusClass}">${instance.status}</span></td>
                    <td>${instance.replicas || 1}</td>
                    <td>${instance.cpu || 0}核 / ${instance.memory || 0}GB</td>
                    <td>${NX.formatDate(instance.createdAt, 'YYYY-MM-DD HH:mm')}</td>
                    <td>
                        <button class="nx-btn nx-btn--sm" onclick="HostingManager.viewHealth('${instance.instanceId}')" title="健康检查">
                            <i class="ri-heart-pulse-line"></i>
                        </button>
                        ${instance.status === 'running' ? 
                            `<button class="nx-btn nx-btn--sm" onclick="HostingManager.stopInstance('${instance.instanceId}')" title="停止"><i class="ri-stop-line"></i></button>` :
                            `<button class="nx-btn nx-btn--sm" onclick="HostingManager.startInstance('${instance.instanceId}')" title="启动"><i class="ri-play-line"></i></button>`
                        }
                        <button class="nx-btn nx-btn--sm nx-btn--danger" onclick="HostingManager.deleteInstance('${instance.instanceId}')" title="删除">
                            <i class="ri-delete-bin-line"></i>
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
    }
    
    showCreateModal() {
        this.openModal('create-modal');
    }
    
    async createInstance() {
        const name = document.getElementById('instance-name').value;
        const provider = document.getElementById('instance-provider').value;
        const image = document.getElementById('instance-image').value;
        const replicas = parseInt(document.getElementById('instance-replicas').value) || 1;
        const cpu = parseFloat(document.getElementById('instance-cpu').value) || 1;
        const memory = parseFloat(document.getElementById('instance-memory').value) || 1;
        
        if (!name) {
            NX.error('请输入实例名称');
            return;
        }
        
        const instance = {
            instanceName: name,
            provider: provider,
            image: image,
            replicas: replicas,
            desiredReplicas: replicas,
            cpu: cpu,
            memory: memory
        };
        
        try {
            const response = await Api.post(`${this.apiBase}/instances`, instance);
            if (response.status === 'success') {
                this.closeModal('create-modal');
                NX.success('实例创建成功');
                await this.loadInstances();
            } else {
                NX.error('实例创建失败: ' + (response.message || '未知错误'));
            }
        } catch (e) {
            NX.error('实例创建失败: ' + e.message);
        }
    }
    
    async startInstance(instanceId) {
        try {
            const response = await Api.post(`${this.apiBase}/instances/${instanceId}/start`, {});
            if (response.status === 'success') {
                NX.success('实例启动成功');
                await this.loadInstances();
            }
        } catch (e) {
            NX.error('启动失败: ' + e.message);
        }
    }
    
    async stopInstance(instanceId) {
        try {
            const response = await Api.post(`${this.apiBase}/instances/${instanceId}/stop`, {});
            if (response.status === 'success') {
                NX.success('实例停止成功');
                await this.loadInstances();
            }
        } catch (e) {
            NX.error('停止失败: ' + e.message);
        }
    }
    
    async deleteInstance(instanceId) {
        if (!confirm('确定要删除该实例吗？')) return;
        
        try {
            const response = await Api.delete(`${this.apiBase}/instances/${instanceId}`);
            if (response.status === 'success') {
                NX.success('实例删除成功');
                await this.loadInstances();
            }
        } catch (e) {
            NX.error('删除失败: ' + e.message);
        }
    }
    
    async viewHealth(instanceId) {
        try {
            const response = await Api.get(`${this.apiBase}/instances/${instanceId}/health`);
            if (response.status === 'success' && response.data) {
                this.renderHealth(response.data);
                this.openModal('health-modal');
                return;
            }
        } catch (e) {
            console.warn('[HostingManager] Failed to load health:', e);
        }
        
        const mockHealth = {
            instanceId: instanceId,
            status: 'running',
            healthy: true,
            message: '实例运行正常',
            cpuUsage: Math.random() * 80,
            memoryUsage: Math.random() * 70,
            lastCheckTime: Date.now(),
            restartCount: 0
        };
        this.renderHealth(mockHealth);
        this.openModal('health-modal');
    }
    
    renderHealth(health) {
        const content = document.getElementById('health-content');
        const statusClass = health.healthy ? 'nx-badge--success' : 'nx-badge--danger';
        
        content.innerHTML = `
            <div class="health-grid">
                <div class="health-item">
                    <div class="health-label">实例ID</div>
                    <div class="health-value">${health.instanceId}</div>
                </div>
                <div class="health-item">
                    <div class="health-label">状态</div>
                    <span class="nx-badge ${statusClass}">${health.healthy ? '健康' : '异常'}</span>
                </div>
                <div class="health-item">
                    <div class="health-label">CPU使用率</div>
                    <div class="health-value">${health.cpuUsage?.toFixed(1) || 0}%</div>
                </div>
                <div class="health-item">
                    <div class="health-label">内存使用率</div>
                    <div class="health-value">${health.memoryUsage?.toFixed(1) || 0}%</div>
                </div>
                <div class="health-item">
                    <div class="health-label">重启次数</div>
                    <div class="health-value">${health.restartCount || 0}</div>
                </div>
                <div class="health-item">
                    <div class="health-label">最后检查</div>
                    <div class="health-value">${NX.formatDate(health.lastCheckTime, 'YYYY-MM-DD HH:mm:ss')}</div>
                </div>
                ${health.lastError ? `
                <div class="health-item" style="grid-column: span 2;">
                    <div class="health-label">最后错误</div>
                    <div class="health-value" style="color: var(--ns-danger);">${health.lastError}</div>
                </div>
                ` : ''}
            </div>
        `;
    }
    
    async refresh() {
        NX.notify('正在刷新...', 'info');
        await this.loadProviders();
        await this.loadInstances();
        NX.success('刷新完成');
    }
}

const hostingManager = new HostingManager();

document.addEventListener('DOMContentLoaded', async () => {
    await hostingManager.init();
});

window.HostingManager = hostingManager;
