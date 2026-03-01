/**
 * Menu Loader - 统一菜单加载器
 * 支持动态加载、权限过滤、角色过滤
 */

class MenuLoader {
    
    constructor(options = {}) {
        this.options = {
            apiEndpoint: options.apiEndpoint || '/api/v1/menu',
            staticFallback: options.staticFallback || '/console/menu-config.json',
            cacheKey: options.cacheKey || 'nexus_menu_cache',
            cacheTTL: options.cacheTTL || 5 * 60 * 1000
        };
        this.menuConfig = null;
        this.cache = null;
    }
    
    async init() {
        console.log('[MenuLoader] Initializing...');
        
        try {
            await this.loadMenuConfig();
            console.log('[MenuLoader] Initialized successfully');
        } catch (e) {
            console.error('[MenuLoader] Failed to initialize:', e);
        }
    }
    
    async loadMenuConfig() {
        const cache = this.getCache();
        if (cache) {
            console.log('[MenuLoader] Using cached menu config');
            this.menuConfig = cache;
            return cache;
        }
        
        try {
            const response = await fetch(`${this.options.apiEndpoint}?v=${Date.now()}`);
            if (response.ok) {
                const data = await response.json();
                this.menuConfig = data.data || data;
                this.setCache(this.menuConfig);
                console.log('[MenuLoader] Loaded from API');
                return this.menuConfig;
            }
        } catch (e) {
            console.warn('[MenuLoader] API load failed, trying fallback:', e);
        }
        
        try {
            const response = await fetch(`${this.options.staticFallback}?v=${Date.now()}`);
            if (response.ok) {
                const data = await response.json();
                this.menuConfig = data;
                this.setCache(this.menuConfig);
                console.log('[MenuLoader] Loaded from static file');
                return this.menuConfig;
            }
        } catch (e) {
            console.error('[MenuLoader] Static load failed:', e);
        }
        
        throw new Error('Failed to load menu config');
    }
    
    getCache() {
        try {
            const cached = localStorage.getItem(this.options.cacheKey);
            if (!cached) return null;
            
            const { data, timestamp } = JSON.parse(cached);
            if (Date.now() - timestamp > this.options.cacheTTL) {
                localStorage.removeItem(this.options.cacheKey);
                return null;
            }
            return data;
        } catch (e) {
            return null;
        }
    }
    
    setCache(data) {
        try {
            localStorage.setItem(this.options.cacheKey, JSON.stringify({
                data: data,
                timestamp: Date.now()
            }));
        } catch (e) {
            console.warn('[MenuLoader] Failed to cache menu:', e);
        }
    }
    
    clearCache() {
        localStorage.removeItem(this.options.cacheKey);
    }
    
    getMenu() {
        return this.menuConfig?.menu || this.menuConfig || [];
    }
    
    getMenuByCategory(category) {
        const menu = this.getMenu();
        return menu.filter(item => item.category === category);
    }
    
    filterByPermission(userPermissions) {
        const menu = this.getMenu();
        return this.filterMenuItems(menu, userPermissions);
    }
    
    filterMenuItems(items, permissions) {
        if (!items || !permissions) return items || [];
        
        return items.filter(item => {
            if (!item.permission) return true;
            if (permissions.includes(item.permission)) {
                if (item.children && item.children.length > 0) {
                    item.children = this.filterMenuItems(item.children, permissions);
                }
                return true;
            }
            return false;
        });
    }
    
    filterByRole(userRoles) {
        const menu = this.getMenu();
        return this.filterMenuByRoles(menu, userRoles);
    }
    
    filterMenuByRoles(items, roles) {
        if (!items || !roles) return items || [];
        
        return items.filter(item => {
            if (!item.roles || item.roles.length === 0) return true;
            const hasRole = item.roles.some(role => roles.includes(role));
            if (hasRole && item.children && item.children.length > 0) {
                item.children = this.filterMenuByRoles(item.children, roles);
            }
            return hasRole;
        });
    }
    
    renderMenu(containerId, options = {}) {
        const container = document.getElementById(containerId);
        if (!container) {
            console.error('[MenuLoader] Container not found:', containerId);
            return;
        }
        
        let menu = this.getMenu();
        
        if (options.permissions) {
            menu = this.filterByPermission(options.permissions);
        }
        
        if (options.roles) {
            menu = this.filterByRole(options.roles);
        }
        
        container.innerHTML = this.renderMenuItems(menu, options);
        
        this.bindMenuEvents(container);
    }
    
    renderMenuItems(items, options = {}) {
        if (!items || items.length === 0) {
            return '<li class="nav-item nav-item--empty">暂无菜单</li>';
        }
        
        return items.map((item, index) => {
            const hasChildren = item.children && item.children.length > 0;
            const activeClass = this.isActive(item.href) ? 'nav-item--active' : '';
            const icon = item.icon || 'ri-folder-line';
            
            if (hasChildren) {
                return `
                    <li class="nav-item nav-item--parent ${activeClass}" data-id="${item.id || index}">
                        <a href="javascript:void(0)" class="nav-link">
                            <i class="${icon}"></i>
                            <span>${item.title}</span>
                            <i class="ri-arrow-down-s-line nav-arrow"></i>
                        </a>
                        <ul class="nav-submenu">
                            ${this.renderMenuItems(item.children, options)}
                        </ul>
                    </li>
                `;
            } else {
                const href = item.href || '#';
                return `
                    <li class="nav-item ${activeClass}" data-id="${item.id || index}">
                        <a href="${href}" class="nav-link">
                            <i class="${icon}"></i>
                            <span>${item.title}</span>
                        </a>
                    </li>
                `;
            }
        }).join('');
    }
    
    isActive(href) {
        if (!href || href === '#' || href === 'javascript:void(0)') {
            return false;
        }
        return window.location.pathname.includes(href);
    }
    
    bindMenuEvents(container) {
        container.querySelectorAll('.nav-item--parent > .nav-link').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const parent = link.closest('.nav-item--parent');
                parent.classList.toggle('nav-item--expanded');
            });
        });
    }
    
    async refresh() {
        this.clearCache();
        await this.loadMenuConfig();
    }
}

window.MenuLoader = MenuLoader;

console.log('[MenuLoader] Module loaded');
