/**
 * MVP API Client - 简化版 API 客户端
 */

(function() {
    'use strict';

    const ApiClient = {
        async request(url, options = {}) {
            const defaultOptions = {
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                },
                ...options
            };

            try {
                const response = await fetch(url, defaultOptions);
                const data = await response.json();
                return data;
            } catch (error) {
                console.error('API request failed:', error);
                throw error;
            }
        },

        async get(url) {
            return this.request(url, { method: 'GET' });
        },

        async post(url, data) {
            return this.request(url, {
                method: 'POST',
                body: JSON.stringify(data)
            });
        },

        async put(url, data) {
            return this.request(url, {
                method: 'PUT',
                body: JSON.stringify(data)
            });
        },

        async delete(url) {
            return this.request(url, { method: 'DELETE' });
        },

        async getMenu() {
            try {
                const response = await this.get('/api/v1/auth/menu-config');
                if (response.status === 'success' && response.data) {
                    return response.data;
                }
                return this.getDefaultMenu();
            } catch (error) {
                console.warn('Failed to load menu, using default:', error);
                return this.getDefaultMenu();
            }
        },

        getDefaultMenu() {
            return [
                { id: 'dashboard', name: '工作台', icon: 'ri-home-line', url: '/console/pages/dashboard.html' },
                { id: 'discovery', name: '能力发现', icon: 'ri-compass-discover-line', url: '/console/pages/capability-discovery.html' },
                { id: 'llm', name: 'LLM配置', icon: 'ri-robot-line', url: '/console/pages/llm-config.html' },
                { id: 'config', name: '系统配置', icon: 'ri-settings-4-line', url: '/console/pages/config-system.html' },
                { id: 'users', name: '用户管理', icon: 'ri-user-settings-line', url: '/console/pages/user-management.html' }
            ];
        }
    };

    window.ApiClient = ApiClient;
})();
