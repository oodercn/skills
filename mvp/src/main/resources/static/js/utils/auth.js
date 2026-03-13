/**
 * MVP 认证工具类
 */

const AuthUtils = {
    TOKEN_KEY: 'mvp_token',
    USER_KEY: 'mvp_user',
    
    saveSession(data) {
        if (data.token) {
            localStorage.setItem(this.TOKEN_KEY, data.token);
        }
        if (data) {
            localStorage.setItem(this.USER_KEY, JSON.stringify(data));
        }
    },
    
    getToken() {
        return localStorage.getItem(this.TOKEN_KEY);
    },
    
    getUser() {
        const userStr = localStorage.getItem(this.USER_KEY);
        if (userStr) {
            try {
                return JSON.parse(userStr);
            } catch (e) {
                return null;
            }
        }
        return null;
    },
    
    isLoggedIn() {
        return !!this.getToken();
    },
    
    hasRole(role) {
        const user = this.getUser();
        return user && (user.role === role || user.roleType === role);
    },
    
    hasPermission(permission) {
        const user = this.getUser();
        if (!user || !user.permissions) return false;
        return user.permissions.includes(permission);
    },
    
    logout() {
        localStorage.removeItem(this.TOKEN_KEY);
        localStorage.removeItem(this.USER_KEY);
        window.location.href = '/login.html';
    },
    
    requireAuth() {
        if (!this.isLoggedIn()) {
            window.location.href = '/login.html';
            return false;
        }
        return true;
    },
    
    requireRole(role) {
        if (!this.requireAuth()) return false;
        if (!this.hasRole(role)) {
            alert('您没有权限访问此页面');
            window.location.href = '/';
            return false;
        }
        return true;
    },
    
    updateUser(data) {
        const user = this.getUser();
        if (user) {
            const updatedUser = { ...user, ...data };
            localStorage.setItem(this.USER_KEY, JSON.stringify(updatedUser));
        }
    },
    
    getDisplayName() {
        const user = this.getUser();
        return user ? (user.name || user.username || '用户') : '游客';
    },
    
    getRoleName() {
        const user = this.getUser();
        if (!user) return '';
        
        const roleNames = {
            'installer': '系统安装者',
            'admin': '系统管理员',
            'leader': '主导者',
            'collaborator': '协作者'
        };
        
        return roleNames[user.roleType || user.role] || '用户';
    }
};

const Toast = {
    show(message, type = 'info', duration = 3000) {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            container.style.cssText = `
                position: fixed;
                top: 20px;
                right: 20px;
                z-index: 10000;
                display: flex;
                flex-direction: column;
                gap: 10px;
            `;
            document.body.appendChild(container);
        }
        
        const toast = document.createElement('div');
        toast.className = 'toast toast-' + type;
        toast.style.cssText = `
            padding: 14px 20px;
            border-radius: 10px;
            color: white;
            font-size: 14px;
            font-weight: 500;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            display: flex;
            align-items: center;
            gap: 10px;
            animation: slideIn 0.3s ease;
        `;
        
        const colors = {
            success: '#10b981',
            error: '#ef4444',
            warning: '#f59e0b',
            info: '#3b82f6'
        };
        
        toast.style.background = colors[type] || colors.info;
        toast.innerHTML = message;
        
        container.appendChild(toast);
        
        setTimeout(() => {
            toast.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, duration);
    },
    
    success(message) {
        this.show('<i class="ri-check-line"></i> ' + message, 'success');
    },
    
    error(message) {
        this.show('<i class="ri-error-warning-line"></i> ' + message, 'error');
    },
    
    warning(message) {
        this.show('<i class="ri-alert-line"></i> ' + message, 'warning');
    },
    
    info(message) {
        this.show('<i class="ri-information-line"></i> ' + message, 'info');
    }
};

const Loading = {
    show(message = '加载中...') {
        let overlay = document.getElementById('loading-overlay');
        if (!overlay) {
            overlay = document.createElement('div');
            overlay.id = 'loading-overlay';
            overlay.style.cssText = `
                position: fixed;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: rgba(0,0,0,0.5);
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 10001;
            `;
            overlay.innerHTML = `
                <div style="
                    background: white;
                    padding: 24px 32px;
                    border-radius: 12px;
                    display: flex;
                    align-items: center;
                    gap: 12px;
                    font-size: 16px;
                    color: #333;
                ">
                    <i class="ri-loader-4-line ri-spin" style="font-size: 24px; color: #667eea;"></i>
                    <span id="loading-message">${message}</span>
                </div>
            `;
            document.body.appendChild(overlay);
        }
    },
    
    hide() {
        const overlay = document.getElementById('loading-overlay');
        if (overlay) {
            overlay.remove();
        }
    },
    
    setMessage(message) {
        const msgEl = document.getElementById('loading-message');
        if (msgEl) {
            msgEl.textContent = message;
        }
    }
};
