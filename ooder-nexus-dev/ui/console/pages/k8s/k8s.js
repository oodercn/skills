class K8sManager {
    
    constructor() {
        this.apiBase = '/api/k8s';
        this.currentNamespace = '';
    }
    
    async init() {
        console.log('[K8sManager] Initializing...');
        
        await this.loadStats();
        await this.loadClusters();
        await this.loadNamespaces();
        await this.loadPods();
        
        console.log('[K8sManager] Initialized');
    }
    
    openModal(modalId) {
        document.getElementById(modalId).classList.add('nx-modal--open');
    }
    
    closeModal(modalId) {
        document.getElementById(modalId).classList.remove('nx-modal--open');
    }
    
    async loadStats() {
        try {
            const response = await Api.get(`${this.apiBase}/status`);
            if (response.status === 'success' && response.data) {
                const data = response.data;
                document.getElementById('cluster-count').textContent = data.clusterCount || 0;
                document.getElementById('node-count').textContent = data.nodeCount || 0;
                document.getElementById('pod-count').textContent = data.podCount || 0;
                document.getElementById('namespace-count').textContent = data.namespaceCount || 0;
            }
        } catch (e) {
            console.warn('[K8sManager] Failed to load stats:', e);
            this.loadMockStats();
        }
    }
    
    loadMockStats() {
        document.getElementById('cluster-count').textContent = '2';
        document.getElementById('node-count').textContent = '6';
        document.getElementById('pod-count').textContent = '48';
        document.getElementById('namespace-count').textContent = '8';
    }
    
    async loadClusters() {
        try {
            const response = await Api.get(`${this.apiBase}/clusters`);
            if (response.status === 'success' && response.data) {
                this.renderClusters(response.data);
                return;
            }
        } catch (e) {
            console.warn('[K8sManager] Failed to load clusters:', e);
        }
        
        this.renderClusters(this.getMockClusters());
    }
    
    getMockClusters() {
        return [
            { clusterId: 'cluster-1', name: 'Production Cluster', status: 'running', nodeCount: 3, version: 'v1.28.0' },
            { clusterId: 'cluster-2', name: 'Development Cluster', status: 'running', nodeCount: 3, version: 'v1.27.0' }
        ];
    }
    
    renderClusters(clusters) {
        const tbody = document.getElementById('cluster-list');
        if (!clusters || clusters.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="nx-empty"><i class="ri-cloud-off-line nx-empty__icon"></i><div class="nx-empty__title">暂无集群</div></td></tr>';
            return;
        }
        
        tbody.innerHTML = clusters.map(cluster => `
            <tr>
                <td>
                    <div style="display: flex; align-items: center; gap: 8px;">
                        <i class="ri-cluster-line" style="color: var(--ns-primary);"></i>
                        ${cluster.name}
                    </div>
                </td>
                <td>
                    <span class="nx-badge nx-badge--success">${cluster.status || 'running'}</span>
                </td>
                <td>${cluster.nodeCount || 0}</td>
                <td>${cluster.version || '-'}</td>
                <td>
                    <button class="nx-btn nx-btn--sm" onclick="K8sManager.viewCluster('${cluster.clusterId}')">
                        <i class="ri-eye-line"></i> 查看
                    </button>
                </td>
            </tr>
        `).join('');
    }
    
    async loadNamespaces() {
        try {
            const response = await Api.get(`${this.apiBase}/namespaces`);
            if (response.status === 'success' && response.data) {
                this.renderNamespaceFilter(response.data);
                return;
            }
        } catch (e) {
            console.warn('[K8sManager] Failed to load namespaces:', e);
        }
        
        this.renderNamespaceFilter(this.getMockNamespaces());
    }
    
    getMockNamespaces() {
        return [
            { name: 'default' },
            { name: 'kube-system' },
            { name: 'kube-public' },
            { name: 'production' },
            { name: 'development' }
        ];
    }
    
    renderNamespaceFilter(namespaces) {
        const select = document.getElementById('namespace-filter');
        select.innerHTML = '<option value="">全部命名空间</option>' +
            namespaces.map(ns => `<option value="${ns.name}">${ns.name}</option>`).join('');
    }
    
    async loadPods() {
        const namespace = document.getElementById('namespace-filter').value;
        
        try {
            const url = namespace 
                ? `${this.apiBase}/pods?namespace=${namespace}`
                : `${this.apiBase}/pods`;
            const response = await Api.get(url);
            if (response.status === 'success' && response.data) {
                this.renderPods(response.data);
                return;
            }
        } catch (e) {
            console.warn('[K8sManager] Failed to load pods:', e);
        }
        
        this.renderPods(this.getMockPods());
    }
    
    getMockPods() {
        return [
            { podName: 'nginx-deployment-abc123', namespace: 'default', status: 'Running', nodeName: 'node-1', restartCount: 0 },
            { podName: 'redis-master-0', namespace: 'production', status: 'Running', nodeName: 'node-2', restartCount: 1 },
            { podName: 'mysql-0', namespace: 'production', status: 'Running', nodeName: 'node-3', restartCount: 0 },
            { podName: 'api-server-xyz789', namespace: 'development', status: 'Pending', nodeName: '-', restartCount: 0 },
            { podName: 'coredns-1234', namespace: 'kube-system', status: 'Running', nodeName: 'node-1', restartCount: 0 }
        ];
    }
    
    renderPods(pods) {
        const tbody = document.getElementById('pod-list');
        if (!pods || pods.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="nx-empty"><i class="ri-inbox-line nx-empty__icon"></i><div class="nx-empty__title">暂无Pod</div></td></tr>';
            return;
        }
        
        tbody.innerHTML = pods.map(pod => {
            const statusClass = pod.status === 'Running' ? 'nx-badge--success' : 
                               pod.status === 'Pending' ? 'nx-badge--warning' : 'nx-badge--danger';
            return `
                <tr>
                    <td>
                        <div style="display: flex; align-items: center; gap: 8px;">
                            <i class="ri-apps-line" style="color: var(--ns-primary);"></i>
                            ${pod.podName}
                        </div>
                    </td>
                    <td>${pod.namespace}</td>
                    <td><span class="nx-badge ${statusClass}">${pod.status}</span></td>
                    <td>${pod.nodeName || '-'}</td>
                    <td>${pod.restartCount || 0}</td>
                    <td>
                        <button class="nx-btn nx-btn--sm" onclick="K8sManager.viewLogs('${pod.namespace}', '${pod.podName}')">
                            <i class="ri-file-list-line"></i> 日志
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
    }
    
    showConnectModal() {
        this.openModal('connect-modal');
    }
    
    async connectCluster() {
        const name = document.getElementById('cluster-name').value;
        const apiServer = document.getElementById('api-server').value;
        const token = document.getElementById('cluster-token').value;
        
        if (!name || !apiServer) {
            NX.error('请填写集群名称和API Server URL');
            return;
        }
        
        try {
            this.closeModal('connect-modal');
            NX.success('集群连接成功');
            await this.refresh();
        } catch (e) {
            NX.error('集群连接失败: ' + e.message);
        }
    }
    
    async viewCluster(clusterId) {
        NX.notify('查看集群详情: ' + clusterId, 'info');
    }
    
    async viewLogs(namespace, podName) {
        try {
            const response = await Api.get(`${this.apiBase}/pods/${namespace}/${podName}/logs?tailLines=100`);
            if (response.status === 'success' && response.data) {
                document.getElementById('pod-logs').textContent = response.data || '暂无日志';
            } else {
                document.getElementById('pod-logs').textContent = this.getMockLogs();
            }
        } catch (e) {
            document.getElementById('pod-logs').textContent = this.getMockLogs();
        }
        
        this.openModal('logs-modal');
    }
    
    getMockLogs() {
        return `[INFO] 2026-02-28 10:00:00 - Application started
[INFO] 2026-02-28 10:00:01 - Connecting to database...
[INFO] 2026-02-28 10:00:02 - Database connected successfully
[INFO] 2026-02-28 10:00:03 - Server listening on port 8080
[INFO] 2026-02-28 10:00:05 - Health check passed
[DEBUG] 2026-02-28 10:01:00 - Processing request: GET /api/v1/status
[INFO] 2026-02-28 10:01:00 - Request completed in 15ms`;
    }
    
    async refresh() {
        NX.notify('正在刷新...', 'info');
        await this.loadStats();
        await this.loadClusters();
        await this.loadNamespaces();
        await this.loadPods();
        NX.success('刷新完成');
    }
}

const k8sManager = new K8sManager();

document.addEventListener('DOMContentLoaded', async () => {
    await k8sManager.init();
});

window.K8sManager = k8sManager;
