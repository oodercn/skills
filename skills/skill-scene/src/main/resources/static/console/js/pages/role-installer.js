/**
 * 系统安装者页面脚本
 */
(function() {
    'use strict';

    const requiredSkills = [
        { id: 'skill-scene', name: 'skill-scene 基础场景技能包', desc: '核心场景管理功能', isCurrentSystem: true },
        { id: 'skill-capability', name: 'skill-capability 能力管理包', desc: '能力发现与管理功能' },
        { id: 'skill-health', name: 'skill-health 健康检查包', desc: '系统健康监控功能' },
        { id: 'skill-llm-chat', name: 'skill-llm-chat LLM对话包', desc: '大语言模型对话功能' }
    ];
    
    const installedSkillIds = new Set();
    let currentSyscode = null;
    let completionShown = false;

    async function initPage() {
        await loadSystemConfig();
        await loadInstalledSkills();
        await checkLoopStatus();
    }
    
    async function checkLoopStatus() {
        try {
            const response = await fetch('/api/v1/installer/status/loop1');
            const result = await response.json();
            
            if (result.status === 'success' && result.data) {
                const status = result.data.status;
                const completedAt = result.data.completedAt;
                
                if (status === 'completed') {
                    addLog('闭环一已完成，跳过安装', 'info');
                    requiredSkills.forEach(skill => {
                        installedSkillIds.add(skill.id);
                    });
                    updateProgress();
                    showCompletionMessage();
                }
            }
        } catch (e) {
            console.error('Failed to check loop status:', e);
        }
    }

    async function loadSystemConfig() {
        try {
            const response = await fetch('/api/v1/system/config');
            const result = await response.json();
            if (result.status === 'success' && result.data) {
                currentSyscode = result.data.syscode;
                addLog(`当前系统: ${currentSyscode}`, 'info');
                
                requiredSkills.forEach(skill => {
                    if (skill.id === currentSyscode || currentSyscode.includes(skill.id.replace('skill-', ''))) {
                        skill.isCurrentSystem = true;
                    }
                });
            }
        } catch (e) {
            console.error('Failed to load system config:', e);
            addLog('无法加载系统配置', 'warning');
        }
    }

    async function loadInstalledSkills() {
        addLog('检查已安装技能...', 'info');
        
        try {
            const response = await fetch('/api/v1/discovery/local', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({})
            });
            const result = await response.json();
            
            if (result.status === 'success' && result.data && result.data.capabilities) {
                const capabilities = result.data.capabilities;
                
                capabilities.forEach(cap => {
                    const skillId = cap.skillId || cap.id;
                    if (skillId && requiredSkills.some(s => s.id === skillId)) {
                        installedSkillIds.add(skillId);
                    }
                });
                
                addLog(`发现 ${installedSkillIds.size} 个已安装技能`, 'success');
            }
        } catch (e) {
            console.error('Failed to load installed skills:', e);
            addLog('无法加载已安装技能列表', 'error');
        }
        
        updateProgress();
    }

    function addLog(message, type = 'info') {
        const logContainer = document.getElementById('install-log');
        const icons = {
            'info': 'ri-information-line',
            'success': 'ri-checkbox-circle-line',
            'error': 'ri-error-warning-line',
            'warning': 'ri-alert-line'
        };
        const time = new Date().toLocaleTimeString();
        const div = document.createElement('div');
        div.className = `log-item ${type}`;
        div.innerHTML = `<i class="${icons[type]}"></i><span class="log-time">${time}</span>${message}`;
        logContainer.appendChild(div);
        logContainer.scrollTop = logContainer.scrollHeight;
    }

    function updateProgress() {
        let installedCount = 0;
        
        requiredSkills.forEach((skill, index) => {
            const item = document.getElementById(`check-${index + 1}`);
            if (item) {
                const isCurrentSystem = skill.isCurrentSystem;
                const isInstalled = installedSkillIds.has(skill.id);
                
                if (isInstalled || isCurrentSystem) {
                    installedCount++;
                    item.classList.add('completed');
                    const icon = item.querySelector('.checklist-icon i');
                    if (icon) icon.className = 'ri-checkbox-circle-line';
                    const btn = item.querySelector('button');
                    if (btn) {
                        btn.disabled = true;
                        if (isCurrentSystem) {
                            btn.textContent = '当前系统';
                            btn.classList.remove('nx-btn--primary');
                            btn.classList.add('nx-btn--success');
                        } else {
                            btn.textContent = '已安装';
                            btn.classList.remove('nx-btn--primary');
                            btn.classList.add('nx-btn--secondary');
                        }
                    }
                } else {
                    item.classList.remove('completed');
                    const icon = item.querySelector('.checklist-icon i');
                    if (icon) icon.className = 'ri-checkbox-blank-line';
                    const btn = item.querySelector('button');
                    if (btn) {
                        btn.disabled = false;
                        btn.textContent = '安装';
                        btn.classList.remove('nx-btn--success', 'nx-btn--secondary');
                        btn.classList.add('nx-btn--primary');
                    }
                }
            }
        });
        
        const total = requiredSkills.length;
        const percent = Math.min(100, Math.round((installedCount / total) * 100));
        const pending = Math.max(0, total - installedCount);

        document.getElementById('installed-count').textContent = installedCount;
        document.getElementById('progress-percent').textContent = percent + '%';
        document.getElementById('pending-count').textContent = pending;
        
        checkAllCompleted();
    }
    
    function checkAllCompleted() {
        const total = requiredSkills.length;
        let installedCount = 0;
        
        requiredSkills.forEach(skill => {
            if (installedSkillIds.has(skill.id) || skill.isCurrentSystem) {
                installedCount++;
            }
        });
        
        if (installedCount === total) {
            showCompletionMessage();
        }
    }
    
    function showCompletionMessage() {
        if (completionShown) return;
        completionShown = true;
        
        const logContainer = document.getElementById('install-log');
        const completionDiv = document.createElement('div');
        completionDiv.className = 'log-item success';
        completionDiv.innerHTML = `
            <i class="ri-checkbox-circle-line"></i>
            <span class="log-time">${new Date().toLocaleTimeString()}</span>
            <strong>闭环一完成！所有基础技能包已安装</strong>
        `;
        logContainer.appendChild(completionDiv);
        logContainer.scrollTop = logContainer.scrollHeight;
        
        const mainContainer = document.querySelector('.installer-main');
        const actionDiv = document.createElement('div');
        actionDiv.className = 'completion-banner';
        actionDiv.innerHTML = `
            <h3><i class="ri-checkbox-circle-fill"></i> 闭环一已完成</h3>
            <p>所有基础技能包已成功安装，系统环境初始化完成</p>
            <button class="nx-btn nx-btn--primary" onclick="goToNextStep()">
                <i class="ri-arrow-right-line"></i> 进入闭环二：场景配置
            </button>
        `;
        mainContainer.appendChild(actionDiv);
        
        saveCompletionStatus();
    }
    
    function goToNextStep() {
        window.location.href = '/console/pages/scene-group-management.html';
    }
    
    async function saveCompletionStatus() {
        try {
            const response = await fetch('/api/v1/installer/status', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    loop: 'loop1',
                    status: 'completed',
                    completedAt: new Date().toISOString()
                })
            });
            const result = await response.json();
            if (result.status !== 'success') {
                console.error('Failed to save completion status:', result);
            }
        } catch (e) {
            console.error('Failed to save completion status:', e);
        }
    }

    async function installSkill(skillId) {
        const skill = requiredSkills.find(s => s.id === skillId);
        if (!skill) return;
        
        if (skill.isCurrentSystem) {
            addLog(`${skill.name} 是当前系统，无需安装`, 'warning');
            return;
        }
        
        addLog(`开始安装 ${skill.name}...`, 'info');

        try {
            const response = await fetch('/api/v1/discovery/install', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ 
                    skillId: skillId, 
                    source: 'GITEE'
                })
            });

            const result = await response.json();

            if (result.status === 'success' && result.data) {
                const installResult = result.data;
                
                if (installResult.status === 'installed' || installResult.status === 'success') {
                    installedSkillIds.add(skillId);
                    addLog(`${skill.name} 安装成功`, 'success');
                    
                    if (installResult.installedDependencies && installResult.installedDependencies.length > 0) {
                        addLog(`安装了 ${installResult.installedDependencies.length} 个依赖`, 'info');
                    }
                    
                    updateProgress();
                } else if (installResult.status === 'failed' || installResult.status === 'error') {
                    addLog(`${skill.name} 安装失败: ${installResult.message || '未知错误'}`, 'error');
                } else {
                    installedSkillIds.add(skillId);
                    addLog(`${skill.name} 安装完成 (${installResult.status})`, 'success');
                    updateProgress();
                }
            } else {
                const errorMsg = result.message || '安装请求失败';
                addLog(`${skill.name} 安装失败: ${errorMsg}`, 'error');
            }
        } catch (e) {
            console.error('Install error:', e);
            addLog(`${skill.name} 安装异常: ${e.message}`, 'error');
        }
    }

    window.installSkill = installSkill;
    window.goToNextStep = goToNextStep;

    document.addEventListener('DOMContentLoaded', initPage);

})();
