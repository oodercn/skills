(function() {
    'use strict';

    let currentStep = 0;
    const steps = ['welcome', 'modules', 'install', 'admin', 'done'];

    function init() {
        loadTheme();
        checkSetupStatus();
    }

    function loadTheme() {
        const theme = localStorage.getItem('nx-theme') || 'dark';
        applyTheme(theme);
    }

    function applyTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        const icon = document.getElementById('themeIcon');
        if (icon) {
            icon.className = theme === 'dark' ? 'ri-sun-line' : 'ri-moon-line';
        }
        localStorage.setItem('nx-theme', theme);
    }

    window.toggleTheme = function() {
        const current = document.documentElement.getAttribute('data-theme') || 'dark';
        const next = current === 'dark' ? 'light' : 'dark';
        applyTheme(next);
    };

    async function checkSetupStatus() {
        try {
            const response = await fetch('/api/v1/setup/status');
            const result = await response.json();
            
            if (result.data && result.data.installed) {
                window.location.href = '/console/';
            }
        } catch (e) {
            console.log('Setup status check failed, continue with setup');
        }
    }

    function showStep(stepName) {
        steps.forEach((step, index) => {
            const el = document.getElementById('step-' + step);
            if (el) {
                el.classList.remove('setup-step--active');
                if (step === stepName) {
                    el.classList.add('setup-step--active');
                    currentStep = index;
                }
            }
        });
    }

    window.startSetup = function() {
        showStep('modules');
    };

    window.prevStep = function() {
        if (currentStep > 0) {
            showStep(steps[currentStep - 1]);
        }
    };

    window.startInstall = async function() {
        showStep('install');
        
        const progressFill = document.getElementById('progressFill');
        const progressText = document.getElementById('progressText');
        const installStatus = document.getElementById('installStatus');
        const installLog = document.getElementById('installLog');

        const steps = [
            { text: '检查安装环境...', delay: 500 },
            { text: '扫描 skills 目录...', delay: 800 },
            { text: '安装 skill-common...', delay: 1500 },
            { text: '安装 skill-scene...', delay: 2000 },
            { text: '配置模块依赖...', delay: 800 },
            { text: '初始化数据库...', delay: 1000 },
            { text: '注册路由和菜单...', delay: 600 },
            { text: '完成安装...', delay: 500 }
        ];

        for (let i = 0; i < steps.length; i++) {
            const progress = Math.round(((i + 1) / steps.length) * 100);
            
            installStatus.textContent = steps[i].text;
            progressFill.style.width = progress + '%';
            progressText.textContent = progress + '%';
            
            const logItem = document.createElement('div');
            logItem.className = 'setup-log-item';
            logItem.innerHTML = '<span class="setup-log-time">' + new Date().toLocaleTimeString() + '</span><span class="setup-log-text">' + steps[i].text + '</span>';
            installLog.appendChild(logItem);
            installLog.scrollTop = installLog.scrollHeight;

            try {
                if (steps[i].text.includes('安装 skill-')) {
                    const skillId = steps[i].text.includes('common') ? 'skill-common' : 'skill-scene';
                    await fetch('/api/v1/plugin/install', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ skillId: skillId })
                    });
                    logItem.querySelector('.setup-log-text').classList.add('setup-log-text--success');
                    logItem.querySelector('.setup-log-text').innerHTML += ' <i class="ri-checkbox-circle-line"></i>';
                }
            } catch (e) {
                logItem.querySelector('.setup-log-text').classList.add('setup-log-text--error');
                logItem.querySelector('.setup-log-text').innerHTML += ' <i class="ri-error-warning-line"></i>';
            }

            await new Promise(resolve => setTimeout(resolve, steps[i].delay));
        }

        await new Promise(resolve => setTimeout(resolve, 500));
        showStep('admin');
    };

    window.createAdmin = async function(e) {
        e.preventDefault();
        
        const username = document.getElementById('adminUsername').value;
        const password = document.getElementById('adminPassword').value;
        const passwordConfirm = document.getElementById('adminPasswordConfirm').value;

        if (password !== passwordConfirm) {
            alert('两次输入的密码不一致');
            return;
        }

        try {
            const response = await fetch('/api/v1/setup/admin', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            const result = await response.json();
            
            if (result.status === 'success') {
                showStep('done');
            } else {
                alert(result.message || '创建管理员账户失败');
            }
        } catch (e) {
            console.error('Create admin failed:', e);
            showStep('done');
        }
    };

    window.goToLogin = function() {
        window.location.href = '/console/pages/login.html';
    };

    document.addEventListener('DOMContentLoaded', init);
})();
