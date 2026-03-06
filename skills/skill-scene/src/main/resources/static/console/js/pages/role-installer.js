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
    
    const installedSkills = new Set();
    let currentUser = null;
    let currentSyscode = null;

    async function checkLogin() {
        try {
            const response = await fetch('/api/v1/auth/session');
            const result = await response.json();
            if (result.status === 'success' && result.data) {
                currentUser = result.data;
                if (currentUser.roleType !== 'installer') {
                    window.location.href = '/console/pages/login.html';
                    return;
                }
                document.getElementById('user-name').textContent = currentUser.name;
                await loadSystemConfig();
                await loadInstalledSkills();
            } else {
                window.location.href = '/console/pages/login.html';
            }
        } catch (e) {
            console.error('Session check failed:', e);
            window.location.href = '/console/pages/login.html';
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
                    if (skillId) {
                        installedSkills.add(skillId);
                    }
                });
                
                installedSkills.add(currentSyscode);
                
                addLog(`发现 ${capabilities.length} 个已安装技能`, 'success');
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
        const count = installedSkills.size;
        const total = requiredSkills.length;
        const percent = Math.round((count / total) * 100);

        document.getElementById('installed-count').textContent = count;
        document.getElementById('progress-percent').textContent = percent + '%';
        document.getElementById('pending-count').textContent = total - count;

        requiredSkills.forEach((skill, index) => {
            const item = document.getElementById(`check-${index + 1}`);
            if (item) {
                const isInstalled = installedSkills.has(skill.id);
                const isCurrentSystem = skill.isCurrentSystem;
                
                if (isInstalled || isCurrentSystem) {
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
                }
            }
        });
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
                    installedSkills.add(skillId);
                    addLog(`${skill.name} 安装成功`, 'success');
                    
                    if (installResult.installedDependencies && installResult.installedDependencies.length > 0) {
                        addLog(`安装了 ${installResult.installedDependencies.length} 个依赖`, 'info');
                    }
                    
                    updateProgress();
                } else if (installResult.status === 'failed' || installResult.status === 'error') {
                    addLog(`${skill.name} 安装失败: ${installResult.message || '未知错误'}`, 'error');
                } else {
                    installedSkills.add(skillId);
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

    function handleLogout() {
        fetch('/api/v1/auth/logout', { method: 'POST' }).catch(() => {});
        localStorage.clear();
        window.location.href = '/console/pages/login.html';
    }

    function initThemeToggle() {
        const toggleBtn = document.getElementById('theme-toggle');
        if (toggleBtn) {
            toggleBtn.addEventListener('click', function() {
                const html = document.documentElement;
                const icon = this.querySelector('i');
                
                if (html.classList.contains('light-theme')) {
                    html.classList.remove('light-theme');
                    localStorage.setItem('theme', 'dark');
                    icon.className = 'ri-sun-line';
                } else {
                    html.classList.add('light-theme');
                    localStorage.setItem('theme', 'light');
                    icon.className = 'ri-moon-line';
                }
            });

            const savedTheme = localStorage.getItem('theme');
            if (savedTheme === 'light') {
                document.documentElement.classList.add('light-theme');
                toggleBtn.querySelector('i').className = 'ri-moon-line';
            }
        }
    }

    window.installSkill = installSkill;
    window.handleLogout = handleLogout;

    document.addEventListener('DOMContentLoaded', function() {
        checkLogin();
        initThemeToggle();
    });

})();
