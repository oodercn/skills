/**
 * NexusMenu - 统一菜单系统
 * 封装 MenuLoader 提供标准接口
 */

(function() {
    'use strict';

    const NexusMenu = {
        initialized: false,
        menuLoader: null,

        async init() {
            if (this.initialized) {
                console.log('[NexusMenu] 已经初始化，跳过');
                return;
            }

            console.log('[NexusMenu] 开始初始化...');

            if (typeof MenuLoader === 'undefined') {
                console.error('[NexusMenu] MenuLoader 未加载');
                return;
            }

            this.menuLoader = new MenuLoader();
            await this.menuLoader.init();
            this.initialized = true;

            console.log('[NexusMenu] 初始化完成');
        },

        async refresh() {
            if (this.menuLoader) {
                await this.menuLoader.init();
            }
        },

        setRole(role) {
            if (this.menuLoader) {
                localStorage.setItem('currentRole', role);
                this.menuLoader.currentRole = role;
                this.menuLoader.renderMenu();
            }
        },

        getRole() {
            return localStorage.getItem('currentRole') || 'personal';
        }
    };

    window.NexusMenu = NexusMenu;

})();
