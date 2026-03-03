let p2pRunning = false;

document.addEventListener('DOMContentLoaded', function() {
    loadData();
});

function switchTab(tab) {
    document.querySelectorAll('.tab').forEach(t => {
        t.classList.toggle('active', t.dataset.tab === tab);
    });
    document.querySelectorAll('.tab-content').forEach(c => {
        c.style.display = 'none';
    });
    document.getElementById(tab + 'Tab').style.display = 'block';
    
    if (tab === 'topology') {
        loadTopology();
    }
}

async function loadData() {
    await Promise.all([
        loadStatus(),
        loadNodes(),
        loadDevices()
    ]);
}

async function loadStatus() {
    try {
        const response = await fetch('/api/network/status');
        const result = await response.json();
        
        if (result.status === 'success') {
            const data = result.data;
            p2pRunning = data.p2pRunning;
            
            document.getElementById('totalNodes').textContent = data.totalNodes + 1;
            document.getElementById('onlineNodes').textContent = data.onlineNodes + 1;
            
            document.getElementById('startBtn').style.display = p2pRunning ? 'none' : 'inline-flex';
            document.getElementById('stopBtn').style.display = p2pRunning ? 'inline-flex' : 'none';
        }
    } catch (error) {
        console.error('Load status error:', error);
    }
}

async function loadNodes() {
    const container = document.getElementById('nodeList');
    
    try {
        const response = await fetch('/api/network/nodes');
        const result = await response.json();
        
        if (result.status === 'success') {
            const nodes = result.data;
            
            if (nodes.length === 0) {
                container.innerHTML = '<div style="text-align: center; padding: 40px; color: var(--ns-text-secondary);">暂无节点数据</div>';
                return;
            }
            
            container.innerHTML = nodes.map(node => {
                const status = node.status ? node.status.code : 'offline';
                const statusClass = status === 'online' ? 'status-online' : 'status-offline';
                const statusText = status === 'online' ? '在线' : '离线';
                const type = node.type ? node.type.description : '未知';
                
                return `
                    <div class="node-card">
                        <div class="node-header">
                            <div class="node-icon">
                                <i class="ri-computer-line"></i>
                            </div>
                            <div class="node-info">
                                <div class="node-name">${node.name}</div>
                                <div class="node-type">${type}</div>
                            </div>
                            <span class="node-status ${statusClass}">${statusText}</span>
                        </div>
                        <div class="node-details">
                            <div><i class="ri-ip-line"></i> IP: ${node.ip || '-'}</div>
                            <div><i class="ri-router-line"></i> 端口: ${node.port || '-'}</div>
                        </div>
                    </div>
                `;
            }).join('');
        }
    } catch (error) {
        container.innerHTML = '<div style="text-align: center; padding: 40px; color: var(--ns-error);">加载失败</div>';
    }
}

async function loadDevices() {
    const tbody = document.getElementById('deviceTable');
    
    try {
        const response = await fetch('/api/network/devices');
        const result = await response.json();
        
        if (result.status === 'success') {
            const devices = result.data;
            
            if (devices.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; padding: 40px; color: var(--ns-text-secondary);">暂无设备数据</td></tr>';
                return;
            }
            
            tbody.innerHTML = devices.map(device => {
                const statusClass = device.isOnline() ? 'status-online' : 'status-offline';
                const statusText = device.isOnline() ? '在线' : '离线';
                
                return `
                    <tr>
                        <td>${device.name}</td>
                        <td>${device.type}</td>
                        <td>${device.ipAddress}</td>
                        <td>${device.macAddress}</td>
                        <td><span class="node-status ${statusClass}">${statusText}</span></td>
                        <td>${device.vendor || '-'}</td>
                    </tr>
                `;
            }).join('');
        }
    } catch (error) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; padding: 40px; color: var(--ns-error);">加载失败</td></tr>';
    }
}

async function loadTopology() {
    const canvas = document.getElementById('topologyCanvas');
    const ctx = canvas.getContext('2d');
    
    canvas.width = canvas.offsetWidth;
    canvas.height = canvas.offsetHeight;
    
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    try {
        const response = await fetch('/api/network/topology');
        const result = await response.json();
        
        if (result.status === 'success') {
            const topology = result.data;
            
            ctx.fillStyle = '#f5f5f5';
            ctx.fillRect(0, 0, canvas.width, canvas.height);
            
            topology.links.forEach(link => {
                const source = topology.nodes.find(n => n.id === link.source);
                const target = topology.nodes.find(n => n.id === link.target);
                
                if (source && target) {
                    ctx.beginPath();
                    ctx.moveTo(source.x || 100, source.y || 200);
                    ctx.lineTo(target.x || 300, target.y || 200);
                    ctx.strokeStyle = '#1890ff';
                    ctx.lineWidth = 2;
                    ctx.stroke();
                }
            });
            
            topology.nodes.forEach(node => {
                const x = node.x || 100;
                const y = node.y || 200;
                
                ctx.beginPath();
                ctx.arc(x, y, 25, 0, Math.PI * 2);
                ctx.fillStyle = node.status === 'online' ? '#52c41a' : '#faad14';
                ctx.fill();
                
                ctx.fillStyle = '#fff';
                ctx.font = '12px sans-serif';
                ctx.textAlign = 'center';
                ctx.fillText(node.name.substring(0, 6), x, y + 4);
            });
        }
    } catch (error) {
        ctx.fillStyle = '#ff4d4f';
        ctx.font = '14px sans-serif';
        ctx.textAlign = 'center';
        ctx.fillText('加载拓扑失败', canvas.width / 2, canvas.height / 2);
    }
}

async function startP2P() {
    try {
        const response = await fetch('/api/network/start', { method: 'POST' });
        const result = await response.json();
        
        if (result.status === 'success') {
            p2pRunning = true;
            document.getElementById('startBtn').style.display = 'none';
            document.getElementById('stopBtn').style.display = 'inline-flex';
            loadData();
        } else {
            alert(result.message || '启动失败');
        }
    } catch (error) {
        alert('网络错误');
    }
}

async function stopP2P() {
    try {
        const response = await fetch('/api/network/stop', { method: 'POST' });
        const result = await response.json();
        
        if (result.status === 'success') {
            p2pRunning = false;
            document.getElementById('startBtn').style.display = 'inline-flex';
            document.getElementById('stopBtn').style.display = 'none';
            loadData();
        } else {
            alert(result.message || '停止失败');
        }
    } catch (error) {
        alert('网络错误');
    }
}
