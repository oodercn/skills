(function() {
    'use strict';
    
    const PageInit = {
        async init() {
            console.log('[PageInit] Initializing page...');
            
            await this.initMenu();
            this.initTheme();
            this.initSidebar();
            this.initModals();
            
            console.log('[PageInit] Page initialized');
        },
        
        async initMenu() {
            const menuContainer = document.getElementById('nav-menu');
            if (!menuContainer) return;
            
            const menuLoader = new MenuLoader();
            await menuLoader.init();
            menuLoader.renderMenu('nav-menu');
        },
        
        initTheme() {
            const savedTheme = localStorage.getItem('nx-theme') || 'dark';
            document.documentElement.setAttribute('data-theme', savedTheme);
            
            document.querySelectorAll('[data-nx-theme-toggle]').forEach(btn => {
                btn.addEventListener('click', () => {
                    const currentTheme = document.documentElement.getAttribute('data-theme');
                    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
                    document.documentElement.setAttribute('data-theme', newTheme);
                    localStorage.setItem('nx-theme', newTheme);
                });
            });
        },
        
        initSidebar() {
            document.querySelectorAll('[data-nx-sidebar-toggle]').forEach(btn => {
                btn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    const sidebar = document.querySelector('.nx-page__sidebar');
                    if (sidebar) {
                        if (window.innerWidth <= 768) {
                            sidebar.classList.toggle('nx-page__sidebar--open');
                        } else {
                            sidebar.classList.toggle('nx-page__sidebar--collapsed');
                        }
                    }
                });
            });
            
            document.addEventListener('click', (e) => {
                const sidebar = document.querySelector('.nx-page__sidebar');
                if (sidebar?.classList.contains('nx-page__sidebar--open')) {
                    if (!sidebar.contains(e.target) && !e.target.closest('[data-nx-sidebar-toggle]')) {
                        sidebar.classList.remove('nx-page__sidebar--open');
                    }
                }
            });
        },
        
        initModals() {
            document.addEventListener('click', (e) => {
                if (e.target.classList.contains('nx-modal')) {
                    e.target.classList.remove('nx-modal--open');
                }
            });
            
            document.addEventListener('keydown', (e) => {
                if (e.key === 'Escape') {
                    const openModal = document.querySelector('.nx-modal--open');
                    if (openModal) {
                        openModal.classList.remove('nx-modal--open');
                    }
                }
            });
        }
    };
    
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => PageInit.init());
    } else {
        PageInit.init();
    }
    
    window.PageInit = PageInit;
})();
