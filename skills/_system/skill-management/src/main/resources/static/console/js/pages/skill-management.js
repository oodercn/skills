let currentTab = 'installed';
let skills = [];

document.addEventListener('DOMContentLoaded', function() {
    loadSkills();
});

function switchTab(tab) {
    currentTab = tab;
    document.querySelectorAll('.tab').forEach(t => {
        t.classList.toggle('active', t.dataset.tab === tab);
    });
    loadSkills();
}

async function loadSkills() {
    const container = document.getElementById('skillList');
    container.innerHTML = '<div class="loading"><i class="ri-loader-4-line"></i></div>';
    
    const keyword = document.getElementById('searchInput').value;
    const category = document.getElementById('categoryFilter').value;
    
    let url = currentTab === 'installed' 
        ? '/api/skill/list' 
        : '/api/skill/market/list';
    
    const params = new URLSearchParams();
    if (keyword) params.append('keyword', keyword);
    if (category) params.append('category', category);
    params.append('size', '50');
    
    try {
        const response = await fetch(`${url}?${params}`);
        const result = await response.json();
        
        if (result.status === 'success') {
            skills = result.data || [];
            renderSkills(skills);
        } else {
            container.innerHTML = `<div class="empty-state">
                <i class="ri-error-warning-line"></i>
                <p>${result.message || '加载失败'}</p>
            </div>`;
        }
    } catch (error) {
        container.innerHTML = `<div class="empty-state">
            <i class="ri-wifi-off-line"></i>
            <p>网络错误，请稍后重试</p>
        </div>`;
    }
}

function renderSkills(skills) {
    const container = document.getElementById('skillList');
    
    if (!skills || skills.length === 0) {
        container.innerHTML = `<div class="empty-state">
            <i class="ri-inbox-line"></i>
            <p>暂无技能数据</p>
        </div>`;
        return;
    }
    
    container.innerHTML = skills.map(skill => {
        const status = skill.status || 'INSTALLED';
        const statusClass = status.toLowerCase();
        const statusText = getStatusText(status);
        
        return `
            <div class="skill-card">
                <div class="skill-header">
                    <div class="skill-icon">
                        <i class="ri-apps-line"></i>
                    </div>
                    <div class="skill-info">
                        <h4 class="skill-name">${skill.name || skill.skillId}</h4>
                        <span class="skill-category">${skill.category || 'general'}</span>
                    </div>
                    <span class="skill-status status-${statusClass}">${statusText}</span>
                </div>
                <p class="skill-desc">${skill.description || '暂无描述'}</p>
                <div class="skill-meta">
                    <span><i class="ri-download-line"></i> ${skill.downloadCount || 0}</span>
                    <span><i class="ri-star-line"></i> ${skill.rating || 0}</span>
                    <span><i class="ri-time-line"></i> ${skill.version || '1.0.0'}</span>
                </div>
                <div class="skill-actions">
                    ${currentTab === 'installed' ? `
                        <button class="btn btn-default" onclick="startSkill('${skill.skillId}')">
                            <i class="ri-play-line"></i> 启动
                        </button>
                        <button class="btn btn-default" onclick="stopSkill('${skill.skillId}')">
                            <i class="ri-stop-line"></i> 停止
                        </button>
                        <button class="btn btn-default" onclick="deleteSkill('${skill.skillId}')">
                            <i class="ri-delete-bin-line"></i>
                        </button>
                    ` : `
                        <button class="btn btn-primary" onclick="installSkill('${skill.skillId}')">
                            <i class="ri-download-line"></i> 安装
                        </button>
                        <button class="btn btn-default" onclick="viewDetail('${skill.skillId}')">
                            <i class="ri-eye-line"></i> 详情
                        </button>
                    `}
                </div>
            </div>
        `;
    }).join('');
}

function getStatusText(status) {
    const statusMap = {
        'RUNNING': '运行中',
        'INSTALLED': '已安装',
        'STOPPED': '已停止',
        'ERROR': '错误',
        'STARTING': '启动中',
        'STOPPING': '停止中'
    };
    return statusMap[status] || status;
}

function handleSearch(event) {
    if (event.key === 'Enter') {
        loadSkills();
    }
}

function openAddModal() {
    document.getElementById('addModal').classList.add('active');
}

function closeAddModal() {
    document.getElementById('addModal').classList.remove('active');
}

async function submitSkill() {
    const skill = {
        skillId: document.getElementById('skillId').value,
        name: document.getElementById('skillName').value,
        category: document.getElementById('skillCategory').value,
        description: document.getElementById('skillDesc').value,
        version: document.getElementById('skillVersion').value
    };
    
    try {
        const response = await fetch('/api/skill/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(skill)
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            closeAddModal();
            loadSkills();
            alert('技能添加成功');
        } else {
            alert(result.message || '添加失败');
        }
    } catch (error) {
        alert('网络错误，请稍后重试');
    }
}

async function startSkill(skillId) {
    try {
        const response = await fetch(`/api/skill/${skillId}/start`, { method: 'POST' });
        const result = await response.json();
        
        if (result.status === 'success') {
            loadSkills();
        } else {
            alert(result.message || '启动失败');
        }
    } catch (error) {
        alert('网络错误');
    }
}

async function stopSkill(skillId) {
    try {
        const response = await fetch(`/api/skill/${skillId}/stop`, { method: 'POST' });
        const result = await response.json();
        
        if (result.status === 'success') {
            loadSkills();
        } else {
            alert(result.message || '停止失败');
        }
    } catch (error) {
        alert('网络错误');
    }
}

async function deleteSkill(skillId) {
    if (!confirm('确定要删除这个技能吗？')) return;
    
    try {
        const response = await fetch(`/api/skill/${skillId}`, { method: 'DELETE' });
        const result = await response.json();
        
        if (result.status === 'success') {
            loadSkills();
        } else {
            alert(result.message || '删除失败');
        }
    } catch (error) {
        alert('网络错误');
    }
}

async function installSkill(skillId) {
    alert('安装功能开发中: ' + skillId);
}

function viewDetail(skillId) {
    alert('查看详情: ' + skillId);
}
