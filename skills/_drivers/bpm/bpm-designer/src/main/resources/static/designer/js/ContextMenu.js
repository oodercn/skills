class ContextMenu {
    constructor() {
        this.menu = null;
    }

    _ensureEvents() {
        if (this._eventsBound) return;
        this._eventsBound = true;
        
        document.addEventListener('click', () => this.hide());
        document.addEventListener('contextmenu', (e) => {
            if (this.menu && !this.menu.contains(e.target)) {
                this.hide();
            }
        });
    }

    show(x, y, items) {
        this._ensureEvents();
        this.hide();

        this.menu = document.createElement('div');
        this.menu.className = 'd-context-menu';
        
        items.forEach(item => {
            if (item.divider) {
                const divider = document.createElement('div');
                divider.className = 'd-context-menu-divider';
                this.menu.appendChild(divider);
            } else {
                const menuItem = document.createElement('div');
                menuItem.className = 'd-context-menu-item';
                if (item.disabled) {
                    menuItem.classList.add('disabled');
                }
                
                let iconHtml = '';
                if (item.icon && window.IconManager) {
                    try {
                        iconHtml = IconManager.render(item.icon, 16);
                    } catch (e) {
                        console.warn('[ContextMenu] Failed to render icon:', item.icon, e);
                    }
                }
                
                menuItem.innerHTML = `
                    <span class="d-context-menu-icon">${iconHtml}</span>
                    <span class="d-context-menu-label">${item.label}</span>
                    ${item.shortcut ? `<span class="d-context-menu-shortcut">${item.shortcut}</span>` : ''}
                `;
                
                if (!item.disabled && item.action) {
                    menuItem.addEventListener('click', (e) => {
                        e.stopPropagation();
                        this.hide();
                        item.action();
                    });
                }
                
                this.menu.appendChild(menuItem);
            }
        });

        document.body.appendChild(this.menu);

        const rect = this.menu.getBoundingClientRect();
        const viewportWidth = window.innerWidth;
        const viewportHeight = window.innerHeight;

        if (x + rect.width > viewportWidth) {
            x = viewportWidth - rect.width - 10;
        }
        if (y + rect.height > viewportHeight) {
            y = viewportHeight - rect.height - 10;
        }

        this.menu.style.left = x + 'px';
        this.menu.style.top = y + 'px';
    }

    hide() {
        if (this.menu) {
            this.menu.remove();
            this.menu = null;
        }
    }
}

window.ContextMenu = new ContextMenu();
