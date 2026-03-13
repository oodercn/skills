/**
 * NX App - Application Entry Point
 * 应用入口文件
 */

import { componentRegistry, LLMApi } from './core/component-registry.js';

const NX = {
  version: '2.3.1',
  
  components: componentRegistry,
  
  llm: LLMApi,
  
  init() {
    console.log(`[NX] Initializing NX Framework v${this.version}`);
    
    this._initTheme();
    this._initSidebar();
    this._initComponents();
    
    console.log('[NX] Framework initialized');
  },
  
  _initTheme() {
    const savedTheme = localStorage.getItem('nx-theme') || 'light';
    document.body.setAttribute('data-theme', savedTheme);
  },
  
  _initSidebar() {
    const sidebar = document.getElementById('nxSidebar');
    const toggleBtn = document.getElementById('sidebarToggle');
    
    if (sidebar && toggleBtn) {
      toggleBtn.addEventListener('click', () => {
        sidebar.classList.toggle('nx-sidebar--collapsed');
        document.body.classList.toggle('nx-page--sidebar-collapsed');
        
        const icon = toggleBtn.querySelector('i');
        if (sidebar.classList.contains('nx-sidebar--collapsed')) {
          icon.className = 'ri-menu-unfold-line';
        } else {
          icon.className = 'ri-menu-fold-line';
        }
      });
    }
  },
  
  _initComponents() {
    document.querySelectorAll('[data-nx-component]').forEach(el => {
      const componentType = el.dataset.nxComponent;
      const options = {};
      
      Object.keys(el.dataset).forEach(key => {
        if (key.startsWith('nx')) {
          const optionKey = key.replace('nx', '').toLowerCase();
          options[optionKey] = el.dataset[key];
        }
      });
      
      import(`./components/${componentType}.js`)
        .then(module => {
          if (module.default) {
            new module.default(el, options);
          }
        })
        .catch(err => {
          console.warn(`[NX] Failed to load component: ${componentType}`, err);
        });
    });
  },
  
  setTheme(theme) {
    document.body.setAttribute('data-theme', theme);
    localStorage.setItem('nx-theme', theme);
  },
  
  getTheme() {
    return document.body.getAttribute('data-theme') || 'light';
  },
  
  toggleTheme() {
    const current = this.getTheme();
    const next = current === 'light' ? 'dark' : 'light';
    this.setTheme(next);
    return next;
  }
};

window.NX = NX;

document.addEventListener('DOMContentLoaded', () => {
  NX.init();
});

export default NX;
