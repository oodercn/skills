(function() {
    'use strict';

    window.NX = {
        version: '2.0.0',
        
        theme: {
            current: localStorage.getItem('nx-theme') || 'dark',
            
            init() {
                this.apply(this.current);
                this.setupToggle();
            },
            
            apply(theme) {
                this.current = theme;
                document.documentElement.setAttribute('data-theme', theme);
                localStorage.setItem('nx-theme', theme);
                window.dispatchEvent(new CustomEvent('nx:themechange', { detail: { theme } }));
            },
            
            toggle() {
                const newTheme = this.current === 'dark' ? 'light' : 'dark';
                this.apply(newTheme);
            },
            
            setupToggle() {
                document.querySelectorAll('[data-nx-theme-toggle]').forEach(btn => {
                    btn.addEventListener('click', () => this.toggle());
                });
            }
        },
        
        init() {
            this.theme.init();
            console.log(`Nexus UI v${this.version} initialized`);
        },
        
        notify(message, type = 'info', duration = 3000) {
            const existing = document.querySelector('.nx-notification');
            if (existing) existing.remove();
            
            const notification = document.createElement('div');
            notification.className = `nx-notification nx-notification--${type}`;
            notification.textContent = message;
            document.body.appendChild(notification);
            
            setTimeout(() => {
                notification.style.opacity = '0';
                notification.style.transform = 'translateX(100%)';
                setTimeout(() => notification.remove(), 300);
            }, duration);
        },
        
        success(message, duration) {
            this.notify(message, 'success', duration);
        },
        
        error(message, duration) {
            this.notify(message, 'error', duration);
        },
        
        warning(message, duration) {
            this.notify(message, 'warning', duration);
        },
        
        showLoading(containerId, text = '加载中...') {
            const container = document.getElementById(containerId);
            if (!container) return;
            
            container.innerHTML = `
                <div class="nx-loading">
                    <div class="nx-loading__spinner"></div>
                    <div class="nx-loading__text">${text}</div>
                </div>
            `;
        },
        
        hideLoading(containerId, html = '') {
            const container = document.getElementById(containerId);
            if (!container) return;
            
            const loading = container.querySelector('.nx-loading');
            if (loading) {
                loading.remove();
            }
            if (html) {
                container.innerHTML = html;
            }
        },
        
        formatDate(date, format = 'YYYY-MM-DD') {
            const d = new Date(date);
            if (isNaN(d.getTime())) return '-';
            
            const year = d.getFullYear();
            const month = String(d.getMonth() + 1).padStart(2, '0');
            const day = String(d.getDate()).padStart(2, '0');
            const hours = String(d.getHours()).padStart(2, '0');
            const minutes = String(d.getMinutes()).padStart(2, '0');
            const seconds = String(d.getSeconds()).padStart(2, '0');
            
            return format
                .replace('YYYY', year)
                .replace('MM', month)
                .replace('DD', day)
                .replace('HH', hours)
                .replace('mm', minutes)
                .replace('ss', seconds);
        },
        
        formatNumber(num, decimals = 0, unit = '') {
            if (num === null || num === undefined || isNaN(num)) return '-';
            
            if (num >= 1000000) {
                return (num / 1000000).toFixed(1) + 'M' + unit;
            } else if (num >= 1000) {
                return (num / 1000).toFixed(1) + 'K' + unit;
            }
            
            return Number(num).toFixed(decimals) + unit;
        },
        
        formatFileSize(bytes) {
            if (bytes === 0) return '0 B';
            const k = 1024;
            const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
            const i = Math.floor(Math.log(bytes) / Math.log(k));
            return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
        },
        
        debounce(func, wait = 300) {
            let timeout;
            return function executedFunction(...args) {
                const later = () => {
                    clearTimeout(timeout);
                    func(...args);
                };
                clearTimeout(timeout);
                timeout = setTimeout(later, wait);
            };
        },
        
        throttle(func, limit = 300) {
            let inThrottle;
            return function executedFunction(...args) {
                if (!inThrottle) {
                    func(...args);
                    inThrottle = true;
                    setTimeout(() => inThrottle = false, limit);
                }
            };
        },
        
        storage: {
            get(key, defaultValue = null) {
                try {
                    const item = localStorage.getItem(key);
                    return item ? JSON.parse(item) : defaultValue;
                } catch (e) {
                    return defaultValue;
                }
            },
            
            set(key, value) {
                try {
                    localStorage.setItem(key, JSON.stringify(value));
                    return true;
                } catch (e) {
                    return false;
                }
            },
            
            remove(key) {
                localStorage.removeItem(key);
            }
        }
    };

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => NX.init());
    } else {
        NX.init();
    }
})();
