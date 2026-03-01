class MenuLoader {
    
    constructor(options = {}) {
        this.options = {
            apiEndpoint: options.apiEndpoint || '/api/v1/menu',
            staticFallback: options.staticFallback || '/console/menu-config.json',
            cacheKey: options.cacheKey || 'nexus_menu_cache',
            cacheTTL: options.cacheTTL || 5 * 60 * 1000
        };
        this.menuConfig = null;
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
    
    renderMenu(containerId, options = {}) {
        const container = document.getElementById(containerId);
        if (!container) {
            console.error('[MenuLoader] Container not found:', containerId);
            return;
        }
        
        let menu = this.getMenu();
        
        if (options.permissions) {
            menu = this.filterByPermission(menu, options.permissions);
        }
        
        if (options.roles) {
            menu = this.filterByRole(menu, options.roles);
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
            const activeClass = this.isActive(item.url) ? 'nav-item--active' : '';
            const icon = item.icon || 'ri-folder-line';
            
            if (hasChildren) {
                return `
                    <li class="nav-item nav-item--parent ${activeClass}" data-id="${item.id || index}">
                        <a href="javascript:void(0)" class="nav-link">
                            <i class="${icon}"></i>
                            <span>${item.name}</span>
                            <i class="ri-arrow-down-s-line nav-arrow"></i>
                        </a>
                        <ul class="nav-submenu">
                            ${this.renderMenuItems(item.children, options)}
                        </ul>
                    </li>
                `;
            } else {
                const href = item.url || '#';
                return `
                    <li class="nav-item ${activeClass}" data-id="${item.id || index}">
                        <a href="${href}" class="nav-link">
                            <i class="${icon}"></i>
                            <span>${item.name}</span>
                        </a>
                    </li>
                `;
            }
        }).join('');
    }
    
    isActive(url) {
        if (!url || url === '#' || url === 'javascript:void(0)') {
            return false;
        }
        return window.location.pathname.includes(url);
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
    
    filterByPermission(items, permissions) {
        if (!items || !permissions) return items || [];
        
        return items.filter(item => {
            if (!item.permission) return true;
            if (permissions.includes(item.permission)) {
                if (item.children && item.children.length > 0) {
                    item.children = this.filterByPermission(item.children, permissions);
                }
                return true;
            }
            return false;
        });
    }
    
    filterByRole(items, roles) {
        if (!items || !roles) return items || [];
        
        return items.filter(item => {
            if (!item.roles || item.roles.length === 0) return true;
            const hasRole = item.roles.some(role => roles.includes(role));
            if (hasRole && item.children && item.children.length > 0) {
                item.children = this.filterByRole(item.children, roles);
            }
            return hasRole;
        });
    }
    
    async refresh() {
        this.clearCache();
        await this.loadMenuConfig();
    }
}

window.MenuLoader = MenuLoader;
