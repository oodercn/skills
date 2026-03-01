var config = {};
var policies = [];

async function init() {
    await loadConfig();
    await loadPolicies();
    await loadStats();
}

async function loadConfig() {
    try {
        var response = await fetch('/api/security/config');
        var result = await response.json();
        
        var configData;
        if (result && result.code === 200) {
            configData = result.data || {};
        } else if (response.ok) {
            configData = result || {};
        } else {
            configData = {};
        }
        config = configData;
        
        document.getElementById('enableAuth').checked = config.enableAuth !== false;
        document.getElementById('enableEncryption').checked = config.enableEncryption !== false;
        document.getElementById('enableAudit').checked = config.enableAudit !== false;
        document.getElementById('sessionTimeout').value = config.sessionTimeout || 30;
        document.getElementById('maxLoginAttempts').value = config.maxLoginAttempts || 5;
        document.getElementById('keyRotationDays').value = config.keyRotationDays || 90;
        document.getElementById('enableFirewall').checked = config.enableFirewall === true;
        document.getElementById('firewallMode').value = config.firewallMode || 'active';
        document.getElementById('enableAgentAuth').checked = config.enableAgentAuth !== false;
        document.getElementById('enableAgentEncryption').checked = config.enableAgentEncryption !== false;
        document.getElementById('enableAgentIsolation').checked = config.enableAgentIsolation !== false;
        document.getElementById('llmRateLimit').value = config.llmRateLimit || 60;
        document.getElementById('costAlertThreshold').value = config.costAlertThreshold || 100;
        
    } catch (e) {
        console.error('Load config failed:', e);
    }
}

async function loadPolicies() {
    try {
        var response = await fetch('/api/security/policies');
        var result = await response.json();
        
        if (result && result.code === 200) {
            policies = result.data || [];
        } else if (response.ok) {
            policies = result || [];
        } else {
            policies = [];
        }
        renderPolicies();
    } catch (e) {
        console.error('Load policies failed:', e);
        policies = [
            { policyId: 'p1', name: '默认访问控制', type: 'ACCESS_CONTROL', enabled: true, rules: ['用户认证', '权限检查'] },
            { policyId: 'p2', name: '数据加密策略', type: 'DATA_PROTECTION', enabled: true, rules: ['传输加密', '存储加密'] }
        ];
        renderPolicies();
    }
}

async function loadStats() {
    try {
        var response = await fetch('/api/v1/keys');
        var result = await response.json();
        
        var keys;
        if (result && result.code === 200) {
            keys = result.data || [];
        } else if (response.ok) {
            keys = result || [];
        } else {
            keys = [];
        }
        document.getElementById('activeKeys').textContent = keys ? keys.filter(function(k) { return k.status === 'ACTIVE'; }).length : 0;
    } catch (e) {
        document.getElementById('activeKeys').textContent = '0';
    }
    
    try {
        var response = await fetch('/api/security/stats');
        var result = await response.json();
        
        var stats;
        if (result && result.code === 200) {
            stats = result.data || {};
        } else if (response.ok) {
            stats = result || {};
        } else {
            stats = {};
        }
        document.getElementById('threatCount').textContent = stats.threatCount || 0;
        document.getElementById('policyCount').textContent = policies.length;
    } catch (e) {
        document.getElementById('threatCount').textContent = '0';
        document.getElementById('policyCount').textContent = policies.length;
    }
}

function renderPolicies() {
    var container = document.getElementById('policyList');
    
    if (policies.length === 0) {
        container.innerHTML = '<p style="color: var(--ns-secondary); text-align: center; padding: 20px;">暂无安全策略</p>';
        return;
    }
    
    var html = '';
    policies.forEach(function(policy) {
        var iconClass = policy.enabled ? 'active' : 'inactive';
        var rulesHtml = policy.rules ? policy.rules.map(function(r) { return '<span>' + r + '</span>'; }).join('') : '';
        
        html += '<div class="policy-card">';
        html += '  <div class="policy-header">';
        html += '    <div class="policy-name">';
        html += '      <span class="status-dot ' + iconClass + '"></span>';
        html += '      ' + policy.name;
        html += '    </div>';
        html += '    <div>';
        html += '      <button class="nx-btn nx-btn--ghost" onclick="togglePolicy(\'' + policy.policyId + '\')">' + (policy.enabled ? '禁用' : '启用') + '</button>';
        html += '      <button class="nx-btn nx-btn--ghost" style="color: var(--ns-danger);" onclick="deletePolicy(\'' + policy.policyId + '\')">删除</button>';
        html += '    </div>';
        html += '  </div>';
        html += '  <div class="policy-rules">' + rulesHtml + '</div>';
        html += '</div>';
    });
    
    container.innerHTML = html;
}

async function saveConfig() {
    var configData = {
        enableAuth: document.getElementById('enableAuth').checked,
        enableEncryption: document.getElementById('enableEncryption').checked,
        enableAudit: document.getElementById('enableAudit').checked,
        sessionTimeout: parseInt(document.getElementById('sessionTimeout').value),
        maxLoginAttempts: parseInt(document.getElementById('maxLoginAttempts').value),
        keyRotationDays: parseInt(document.getElementById('keyRotationDays').value),
        enableFirewall: document.getElementById('enableFirewall').checked,
        firewallMode: document.getElementById('firewallMode').value,
        enableAgentAuth: document.getElementById('enableAgentAuth').checked,
        enableAgentEncryption: document.getElementById('enableAgentEncryption').checked,
        enableAgentIsolation: document.getElementById('enableAgentIsolation').checked,
        llmRateLimit: parseInt(document.getElementById('llmRateLimit').value),
        costAlertThreshold: parseInt(document.getElementById('costAlertThreshold').value)
    };
    
    try {
        var response = await fetch('/api/security/config', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(configData)
        });
        
        var result = await response.json();
        
        if (response.ok && (result.code === 200 || result.code === undefined)) {
            alert('配置保存成功');
        } else {
            var errorMsg = result.message || result.error || '配置保存失败';
            alert('配置保存失败: ' + errorMsg);
        }
    } catch (e) {
        console.error('Save config failed:', e);
        alert('配置保存失败: ' + e.message);
    }
}

function showCreatePolicyModal() {
    document.getElementById('createPolicyModal').classList.add('nx-modal--open');
    document.getElementById('policyForm').reset();
}

function hideCreatePolicyModal() {
    document.getElementById('createPolicyModal').classList.remove('nx-modal--open');
}

async function createPolicy(e) {
    e.preventDefault();
    
    var policy = {
        name: document.getElementById('policyName').value,
        type: document.getElementById('policyType').value,
        description: document.getElementById('policyDescription').value,
        enabled: true,
        rules: []
    };
    
    try {
        var response = await fetch('/api/security/policies', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(policy)
        });
        
        var result = await response.json();
        
        if (response.ok && (result.code === 200 || result.code === undefined)) {
            hideCreatePolicyModal();
            loadPolicies();
            alert('策略创建成功');
        } else {
            var errorMsg = result.message || result.error || '创建失败';
            alert('创建失败: ' + errorMsg);
        }
    } catch (e) {
        console.error('Create policy failed:', e);
        alert('创建失败: ' + e.message);
    }
}

async function togglePolicy(policyId) {
    var policy = policies.find(function(p) { return p.policyId === policyId; });
    if (!policy) return;
    
    var action = policy.enabled ? '禁用' : '启用';
    if (!confirm('确定要' + action + '此策略吗？')) return;
    
    try {
        var url = policy.enabled 
            ? '/api/security/policies/' + policyId + '/disable'
            : '/api/security/policies/' + policyId + '/enable';
        
        var response = await fetch(url, {
            method: 'POST'
        });
        
        var result = await response.json();
        
        if (response.ok && (result === true || result.code === 200)) {
            loadPolicies();
            alert('策略已' + action);
        } else {
            var errorMsg = result.message || result.error || action + '失败';
            alert(action + '失败: ' + errorMsg);
        }
    } catch (e) {
        console.error('Toggle policy failed:', e);
        alert(action + '失败: ' + e.message);
    }
}

async function deletePolicy(policyId) {
    if (!confirm('确定要删除此策略吗？')) return;
    
    try {
        var response = await fetch('/api/security/policies/' + policyId, {
            method: 'DELETE'
        });
        
        var result = await response.json();
        
        if (response.ok && (result.code === 200 || result.code === undefined)) {
            loadPolicies();
            alert('策略删除成功');
        } else {
            var errorMsg = result.message || result.error || '删除失败';
            alert('删除失败: ' + errorMsg);
        }
    } catch (e) {
        console.error('Delete policy failed:', e);
        alert('删除失败: ' + e.message);
    }
}

init();
