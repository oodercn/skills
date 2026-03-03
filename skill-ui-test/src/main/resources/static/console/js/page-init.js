/**
 * ooderNexus 统一页面初始化模块
 * 所有页面都应该使用此模块进行初始化
 * 
 * 使用方法:
 * 1. 在页面中引入: <script src="../../js/page-init.js"></script>
 * 2. 调用: PageInit.init();
 * 
 * 或者使用自动初始化（推荐）:
 * <script src="../../js/page-init.js" data-auto-init></script>
 */

(function() {
  'use strict';

  /**
   * 页面初始化配置
   */
  const PageInit = {
    version: '1.0.0',
    
    initialized: false,
    
    config: {
      autoInitMenu: true,
      autoBindThemeToggle: true,
      autoBindSidebarToggle: true,
      defaultTitle: 'Nexus Console'
    },
    
    init(options = {}) {
      this.config = { ...this.config, ...options };
      
      if (this.initialized) {
        console.warn('[PageInit] 页面已经初始化，跳过');
        return this;
      }
      
      console.log('[PageInit] 开始初始化页面...');
      console.log('[PageInit] document.readyState:', document.readyState);
      
      if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', async () => await this._doInit());
      } else {
        this._doInit();
      }
      
      return this;
    },
    
    async _doInit() {
      try {
        console.log('[PageInit] _doInit 开始执行...');
        
        console.log('[PageInit] 步骤1: 初始化 Nexus UI...');
        this._initNexusUI();
        
        if (this.config.autoInitMenu) {
          console.log('[PageInit] 步骤2: 初始化菜单...');
          await this._initMenu();
        }
        
        if (this.config.autoBindThemeToggle) {
          console.log('[PageInit] 步骤3: 绑定主题切换...');
          this._bindThemeToggle();
        }
        
        if (this.config.autoBindSidebarToggle) {
          console.log('[PageInit] 步骤4: 绑定侧边栏切换...');
          this._bindSidebarToggle();
        }
        
        console.log('[PageInit] 步骤5: 初始化 LLM Assistant...');
        this._initLlmAssistant();
        
        console.log('[PageInit] 步骤6: 页面特定初始化...');
        this._initPageSpecific();
        
        this.initialized = true;
        console.log('[PageInit] 页面初始化完成');
        
        window.dispatchEvent(new CustomEvent('pageInitComplete'));
        
      } catch (error) {
        console.error('[PageInit] 初始化失败:', error);
      }
    },
    
    _initNexusUI() {
      if (typeof NX === 'undefined') {
        console.warn('[PageInit] NX 对象未找到，部分功能可能不可用');
        return;
      }
      
      if (NX.theme && typeof NX.theme.init === 'function') {
        NX.theme.init();
      }
      
      if (NX.sidebar && typeof NX.sidebar.init === 'function') {
        NX.sidebar.init();
      }
      
      if (NX.nav && typeof NX.nav.init === 'function') {
        NX.nav.init();
      }
      
      if (NX.modal && typeof NX.modal.init === 'function') {
        NX.modal.init();
      }
    },
    
    async _initMenu() {
      console.log('[PageInit] _initMenu 开始...');
      console.log('[PageInit] NexusMenu 是否存在:', typeof NexusMenu !== 'undefined');
      console.log('[PageInit] ApiClient 是否存在:', typeof ApiClient !== 'undefined');
      console.log('[PageInit] ApiClient.getMenu 是否存在:', typeof ApiClient !== 'undefined' && typeof ApiClient.getMenu === 'function');
      
      if (typeof NexusMenu !== 'undefined') {
        console.log('[PageInit] 调用 NexusMenu.init()...');
        await NexusMenu.init();
        console.log('[PageInit] NexusMenu.init() 完成');
      } else {
        console.warn('[PageInit] NexusMenu 未加载');
      }
    },
    
    _renderMenuItems(items, level = 0) {
      return items.map(item => {
        const hasChildren = item.children && item.children.length > 0;
        const isActive = this._isCurrentPage(item.url);
        const url = this._buildMenuUrl(item.url);
        
        let html = `<li class="nav-item ${isActive ? 'active' : ''}" data-level="${level}" data-id="${item.id}">`;
        
        if (hasChildren) {
          html += `
            <a href="javascript:void(0)" class="nav-link has-submenu" data-toggle="submenu">
              ${item.icon ? `<i class="${item.icon}"></i>` : ''}
              <span>${item.name}</span>
              <i class="ri-arrow-down-s-line submenu-icon"></i>
            </a>
            <ul class="submenu ${isActive ? 'show' : ''}">
              ${this._renderMenuItems(item.children, level + 1)}
            </ul>
          `;
        } else {
          html += `
            <a href="${url}" class="nav-link">
              ${item.icon ? `<i class="${item.icon}"></i>` : ''}
              <span>${item.name}</span>
            </a>
          `;
        }
        
        html += '</li>';
        return html;
      }).join('');
    },
    
    _buildMenuUrl(url) {
      if (!url) return 'javascript:void(0)';
      
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url;
      }
      
      if (url.startsWith('/console/')) {
        return url;
      }
      
      if (url.startsWith('/')) {
        return url;
      }
      
      if (url.startsWith('console/')) {
        return `/${url}`;
      }
      
      return `/console/${url}`;
    },
    
    _isCurrentPage(url) {
      if (!url) return false;
      const currentPath = window.location.pathname;
      return currentPath.includes(url) || url.includes(currentPath);
    },
    
    _bindMenuEvents() {
      const navMenu = document.getElementById('nav-menu');
      if (!navMenu) return;
      
      navMenu.addEventListener('click', (e) => {
        const link = e.target.closest('.nav-link');
        if (!link) return;
        
        const hasSubmenu = link.hasAttribute('data-toggle') && link.getAttribute('data-toggle') === 'submenu';
        
        if (hasSubmenu) {
          e.preventDefault();
          e.stopPropagation();
          
          const submenu = link.nextElementSibling;
          if (submenu && submenu.classList.contains('submenu')) {
            const isExpanded = submenu.classList.contains('show');
            
            if (isExpanded) {
              submenu.classList.remove('show');
              link.classList.remove('expanded');
            } else {
              submenu.classList.add('show');
              link.classList.add('expanded');
            }
            
            const menuId = link.closest('.nav-item')?.getAttribute('data-id');
            if (menuId) {
              this._saveMenuState(menuId, !isExpanded);
            }
          }
        }
      });
    },
    
    _saveMenuState(menuId, isExpanded) {
      try {
        let expandedMenus = JSON.parse(localStorage.getItem('expandedMenus') || '[]');
        
        if (isExpanded) {
          if (!expandedMenus.includes(menuId)) {
            expandedMenus.push(menuId);
          }
        } else {
          expandedMenus = expandedMenus.filter(id => id !== menuId);
        }
        
        localStorage.setItem('expandedMenus', JSON.stringify(expandedMenus));
      } catch (e) {
        console.warn('[PageInit] 保存菜单状态失败:', e);
      }
    },
    
    _restoreMenuState() {
      try {
        const expandedMenus = JSON.parse(localStorage.getItem('expandedMenus') || '[]');
        
        expandedMenus.forEach(menuId => {
          const menuItem = document.querySelector(`.nav-item[data-id="${menuId}"]`);
          if (menuItem) {
            const submenu = menuItem.querySelector('.submenu');
            const link = menuItem.querySelector('.nav-link');
            
            if (submenu) {
              submenu.classList.add('show');
            }
            if (link) {
              link.classList.add('expanded');
            }
          }
        });
      } catch (e) {
        console.warn('[PageInit] 恢复菜单状态失败:', e);
      }
    },
    
    _highlightCurrentMenu() {
      const currentPath = window.location.pathname;
      const navLinks = document.querySelectorAll('.nav-menu a');
      
      navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href && !href.startsWith('javascript:')) {
          if (currentPath.includes(href) || href.includes(currentPath)) {
            link.classList.add('active');
            const parentSubmenu = link.closest('.submenu');
            if (parentSubmenu) {
              parentSubmenu.classList.add('show');
              const parentLink = parentSubmenu.previousElementSibling;
              if (parentLink) {
                parentLink.classList.add('active');
              }
            }
          }
        }
      });
      
      this._restoreMenuState();
    },
    
    _bindThemeToggle() {
      const themeToggle = document.querySelector('[data-nx-theme-toggle]');
      if (themeToggle && typeof NX !== 'undefined' && NX.theme) {
        themeToggle.addEventListener('click', () => {
          NX.theme.toggle();
        });
      }
    },
    
    _bindSidebarToggle() {
      const sidebarToggle = document.querySelector('[data-nx-sidebar-toggle]');
      if (sidebarToggle && typeof NX !== 'undefined' && NX.sidebar) {
        sidebarToggle.addEventListener('click', () => {
          NX.sidebar.toggle();
        });
      }
    },
    
    _initLlmAssistant() {
      if (typeof LlmAssistant !== 'undefined') {
        LlmAssistant.init();
      }
    },
    
    _initPageSpecific() {
      if (typeof window._globalsInit === 'function') {
        try {
          window._globalsInit();
        } catch (error) {
          console.error('[PageInit] globals.js 初始化失败:', error);
        }
      }
      
      if (typeof window._commonInit === 'function') {
        try {
          window._commonInit();
        } catch (error) {
          console.error('[PageInit] common.js 初始化失败:', error);
        }
      }
      
      if (typeof window.onPageInit === 'function') {
        try {
          window.onPageInit();
        } catch (error) {
          console.error('[PageInit] 页面特定初始化失败:', error);
        }
      }
    },
    
    setTitle(title) {
      document.title = `${title} - ${this.config.defaultTitle}`;
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
    
    hideLoading(containerId) {
      const container = document.getElementById(containerId);
      if (!container) return;
      
      const loading = container.querySelector('.nx-loading');
      if (loading) {
        loading.remove();
      }
    },
    
    showEmpty(containerId, title = '暂无数据', description = '', icon = 'ri-inbox-line') {
      const container = document.getElementById(containerId);
      if (!container) return;
      
      container.innerHTML = `
        <div class="nx-empty">
          <i class="${icon} nx-empty__icon"></i>
          <div class="nx-empty__title">${title}</div>
          ${description ? `<div class="nx-empty__description">${description}</div>` : ''}
        </div>
      `;
    },
    
    showError(containerId, message = '加载失败') {
      this.showEmpty(containerId, message, '请检查网络连接后重试', 'ri-error-warning-line');
      const container = document.getElementById(containerId);
      if (container) {
        const icon = container.querySelector('.nx-empty__icon');
        if (icon) {
          icon.style.color = 'var(--nx-danger)';
        }
      }
    }
  };

  window.PageInit = PageInit;

  const currentScript = document.currentScript;
  if (currentScript && currentScript.hasAttribute('data-auto-init')) {
    const doInit = () => {
      if (typeof NexusMenu !== 'undefined') {
        PageInit.init();
      } else {
        console.warn('[PageInit] NexusMenu 未加载，菜单可能无法正常显示');
        PageInit.init();
      }
    };
    
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', doInit);
    } else {
      doInit();
    }
  }

})();
