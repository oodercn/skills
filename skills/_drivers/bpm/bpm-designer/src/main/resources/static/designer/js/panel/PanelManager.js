class PanelManager {
    constructor(container, store) {
        this.container = container;
        this.store = store;
        this.currentType = null;
        this.currentData = null;
        this.currentTab = 'basic';
    }

    render(type, data) {
        console.log('[PanelManager] render called:', type, data);
        this.currentType = type;
        this.currentData = data;
        this.currentTab = 'basic';
        
        let schema;
        if (type === 'activity' && data) {
            schema = PanelSchema.getActivitySchema(data);
            console.log('[PanelManager] Activity schema:', schema);
        } else {
            schema = PanelSchema[type];
            console.log('[PanelManager] Schema from PanelSchema:', schema);
        }
        
        if (!schema) {
            console.log('[PanelManager] No schema found, showing empty message');
            this.container.innerHTML = '<div class="d-empty">请选择元素</div>';
            return;
        }

        console.log('[PanelManager] Rendering tabs and content...');
        this._renderTabs(schema.tabs);
        this._renderContent(schema.fields[this.currentTab], data);
        console.log('[PanelManager] Render complete');
    }

    _renderTabs(tabs) {
        const tabsContainer = this.container.querySelector('#panelTabs');
        if (!tabsContainer) return;

        tabsContainer.innerHTML = tabs.map(tab => `
            <button class="d-tab ${tab.id === this.currentTab ? 'active' : ''}" 
                    data-tab="${tab.id}">
                <span class="d-tab-icon">${IconManager.render(tab.icon, 16)}</span>
                <span>${tab.name}</span>
            </button>
        `).join('');

        tabsContainer.querySelectorAll('.d-tab').forEach(tab => {
            tab.addEventListener('click', () => {
                this.currentTab = tab.dataset.tab;
                tabsContainer.querySelectorAll('.d-tab').forEach(t => {
                    t.classList.toggle('active', t.dataset.tab === this.currentTab);
                });
                
                let schema;
                if (this.currentType === 'activity' && this.currentData) {
                    schema = PanelSchema.getActivitySchema(this.currentData);
                } else {
                    schema = PanelSchema[this.currentType];
                }
                
                if (schema) {
                    this._renderContent(schema.fields[this.currentTab], this.currentData);
                }
            });
        });
    }

    _renderContent(fields, data) {
        const contentContainer = this.container.querySelector('#panelContent');
        if (!contentContainer || !fields) {
            if (contentContainer) {
                contentContainer.innerHTML = '<div class="d-empty">无配置项</div>';
            }
            return;
        }

        contentContainer.innerHTML = fields.map(field => this._renderField(field, data)).join('');
        this._bindFieldEvents(contentContainer, data);
    }

    _renderField(field, data) {
        const value = this._getValue(field.name, data);

        switch (field.type) {
            case 'section':
                return `<div class="d-section-title">${field.title}</div>`;
            
            case 'text':
                return `
                    <div class="d-field">
                        <label class="d-label">${field.label}${field.required ? ' *' : ''}</label>
                        <input type="text" class="d-input" 
                               name="${field.name}" 
                               value="${value || ''}"
                               ${field.readonly ? 'readonly' : ''}>
                    </div>
                `;
            
            case 'textarea':
                return `
                    <div class="d-field">
                        <label class="d-label">${field.label}</label>
                        <textarea class="d-input" name="${field.name}" 
                                  style="min-height: 60px; resize: vertical;"
                                  ${field.readonly ? 'readonly' : ''}>${value || ''}</textarea>
                    </div>
                `;
            
            case 'number':
                return `
                    <div class="d-field">
                        <label class="d-label">${field.label}</label>
                        <div style="display: flex; align-items: center; gap: 8px;">
                            <input type="number" class="d-input" 
                                   name="${field.name}" 
                                   value="${value !== undefined ? value : ''}"
                                   min="${field.min || ''}"
                                   max="${field.max || ''}"
                                   step="${field.step || 1}"
                                   ${field.readonly ? 'readonly' : ''}>
                            ${field.unit ? `<span style="color: var(--text-secondary); font-size: 12px;">${field.unit}</span>` : ''}
                        </div>
                    </div>
                `;
            
            case 'select':
                return `
                    <div class="d-field">
                        <label class="d-label">${field.label}</label>
                        <select class="d-select" name="${field.name}" ${field.readonly ? 'disabled' : ''}>
                            ${field.options.map(opt => `
                                <option value="${opt.value}" ${value === opt.value ? 'selected' : ''}>
                                    ${opt.label}
                                </option>
                            `).join('')}
                        </select>
                    </div>
                `;
            
            case 'radio':
                return `
                    <div class="d-field">
                        <label class="d-label">${field.label}</label>
                        <div style="display: flex; flex-wrap: wrap; gap: 12px; margin-top: 4px;">
                            ${field.options.map(opt => `
                                <label class="d-radio">
                                    <input type="radio" name="${field.name}" 
                                           value="${opt.value}" 
                                           ${value === opt.value ? 'checked' : ''}>
                                    <span>${opt.label}</span>
                                </label>
                            `).join('')}
                        </div>
                    </div>
                `;
            
            case 'checkbox':
                return `
                    <div class="d-field">
                        <label class="d-checkbox">
                            <input type="checkbox" name="${field.name}" ${value ? 'checked' : ''}>
                            <span>${field.label}</span>
                        </label>
                    </div>
                `;
            
            case 'list':
                const items = value || [];
                return `
                    <div class="d-field">
                        <label class="d-label">${field.label || ''}</label>
                        <div class="d-list-items" data-list-name="${field.name}">
                            ${items.map((item, i) => `
                                <div class="d-list-item" data-index="${i}">
                                    ${field.fields.map(f => `
                                        <input type="text" class="d-input d-input-sm" 
                                               placeholder="${f.label}"
                                               value="${item[f.name] || ''}"
                                               data-field="${f.name}">
                                    `).join('')}
                                    <button class="d-btn d-btn-icon d-btn-sm d-btn-remove">
                                        ${IconManager.render('close', 14)}
                                    </button>
                                </div>
                            `).join('')}
                        </div>
                        <button class="d-btn d-btn-secondary d-btn-sm d-btn-add" data-list-name="${field.name}">
                            ${IconManager.render('plus', 14)} ${field.addText || '添加'}
                        </button>
                    </div>
                `;
            
            case 'keyvalue':
                const kv = value || {};
                const kvEntries = Object.entries(kv);
                return `
                    <div class="d-field">
                        <div class="d-kv-items" data-kv-name="${field.name}">
                            ${kvEntries.map(([k, v], i) => `
                                <div class="d-kv-item" data-key="${k}">
                                    <input type="text" class="d-input d-input-sm" 
                                           placeholder="属性名" value="${k}" data-type="key">
                                    <input type="text" class="d-input d-input-sm" 
                                           placeholder="属性值" value="${v}" data-type="value">
                                    <button class="d-btn d-btn-icon d-btn-sm d-btn-remove">
                                        ${IconManager.render('close', 14)}
                                    </button>
                                </div>
                            `).join('')}
                        </div>
                        <button class="d-btn d-btn-secondary d-btn-sm d-btn-add-kv" data-kv-name="${field.name}">
                            ${IconManager.render('plus', 14)} ${field.addText || '添加'}
                        </button>
                    </div>
                `;
            
            default:
                return '';
        }
    }

    _getValue(name, data) {
        if (!name || !data) return undefined;
        
        const parts = name.split('.');
        let value = data;
        
        for (const part of parts) {
            if (value && typeof value === 'object' && part in value) {
                value = value[part];
            } else {
                return undefined;
            }
        }
        
        return value;
    }

    _setValue(name, value, data) {
        if (!name || !data) return;
        
        const parts = name.split('.');
        let obj = data;
        
        for (let i = 0; i < parts.length - 1; i++) {
            const part = parts[i];
            if (!(part in obj)) {
                obj[part] = {};
            }
            obj = obj[part];
        }
        
        obj[parts[parts.length - 1]] = value;
    }

    _bindFieldEvents(container, data) {
        container.querySelectorAll('input, select, textarea').forEach(el => {
            const name = el.name || el.dataset.field;
            if (!name) return;

            el.addEventListener('change', () => {
                let value;
                if (el.type === 'checkbox') {
                    value = el.checked;
                } else if (el.type === 'number') {
                    value = parseFloat(el.value);
                } else {
                    value = el.value;
                }
                
                this._setValue(name, value, data);
                this._onDataChange(data);
            });

            el.addEventListener('input', () => {
                if (el.type === 'number') {
                    this._setValue(name, parseFloat(el.value) || 0, data);
                } else {
                    this._setValue(name, el.value, data);
                }
            });
        });

        container.querySelectorAll('.d-btn-add').forEach(btn => {
            btn.addEventListener('click', () => {
                const listName = btn.dataset.listName;
                const list = this._getValue(listName, data) || [];
                list.push({});
                this._setValue(listName, list, data);
                this.render(this.currentType, data);
            });
        });

        container.querySelectorAll('.d-btn-add-kv').forEach(btn => {
            btn.addEventListener('click', () => {
                const kvName = btn.dataset.kvName;
                const kv = this._getValue(kvName, data) || {};
                kv['new_key'] = '';
                this._setValue(kvName, kv, data);
                this.render(this.currentType, data);
            });
        });

        container.querySelectorAll('.d-btn-remove').forEach(btn => {
            btn.addEventListener('click', () => {
                const item = btn.closest('.d-list-item, .d-kv-item');
                if (item) {
                    item.remove();
                }
            });
        });
    }

    _onDataChange(data) {
        if (this.currentType === 'activity' && this.store) {
            this.store.updateActivity(data);
        } else if (this.currentType === 'process' && this.store) {
            this.store.setDirty(true);
        }
    }
}

window.PanelManager = PanelManager;
