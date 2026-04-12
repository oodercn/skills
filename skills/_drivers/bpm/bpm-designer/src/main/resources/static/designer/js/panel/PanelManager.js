class PanelManager {
    constructor(container, store) {
        this.container = container;
        this.store = store;
        this.currentType = null;
        this.currentData = null;
        this.originalData = null;
        this.currentTab = 'basic';
        this.pendingChanges = new Map();
        this._changeTimeout = null;
    }

    render(type, data) {
        console.log('[PanelManager] render called:', type, data);
        this.currentType = type;
        this.currentData = JSON.parse(JSON.stringify(data));
        this.originalData = JSON.parse(JSON.stringify(data));
        this.pendingChanges.clear();
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
                if (this.pendingChanges.size > 0) {
                    this._confirmAndSwitchTab(tab.dataset.tab);
                } else {
                    this._switchTab(tab.dataset.tab);
                }
            });
        });
    }

    _confirmAndSwitchTab(newTab) {
        const confirmed = confirm('当前有未保存的更改，是否保存？');
        if (confirmed) {
            this._onConfirm();
        } else {
            this._onCancel();
        }
        this._switchTab(newTab);
    }

    _switchTab(tabId) {
        this.currentTab = tabId;
        const tabsContainer = this.container.querySelector('#panelTabs');
        if (tabsContainer) {
            tabsContainer.querySelectorAll('.d-tab').forEach(t => {
                t.classList.toggle('active', t.dataset.tab === this.currentTab);
            });
        }
        
        let schema;
        if (this.currentType === 'activity' && this.currentData) {
            schema = PanelSchema.getActivitySchema(this.currentData);
        } else {
            schema = PanelSchema[this.currentType];
        }
        
        if (schema) {
            this._renderContent(schema.fields[this.currentTab], this.currentData);
        }
    }

    _renderContent(fields, data) {
        const contentContainer = this.container.querySelector('#panelContent');
        if (!contentContainer || !fields) {
            if (contentContainer) {
                contentContainer.innerHTML = '<div class="d-empty">无配置项</div>';
            }
            return;
        }

        const wrapper = document.createElement('div');
        wrapper.className = 'd-panel-content-wrapper';
        wrapper.style.cssText = 'display: flex; flex-direction: column; height: 100%;';
        
        const formContainer = document.createElement('div');
        formContainer.className = 'd-panel-form-container';
        formContainer.style.cssText = 'flex: 1; overflow-y: auto; padding: 16px;';
        formContainer.innerHTML = fields.map(field => this._renderField(field, data)).join('');
        
        const buttonArea = this._createButtonArea();
        
        wrapper.appendChild(formContainer);
        wrapper.appendChild(buttonArea);
        
        contentContainer.innerHTML = '';
        contentContainer.appendChild(wrapper);
        
        this._bindFieldEvents(formContainer, data);
    }

    _createButtonArea() {
        const buttonArea = document.createElement('div');
        buttonArea.className = 'd-panel-button-area';
        buttonArea.style.cssText = `
            position: sticky;
            bottom: 0;
            background: var(--panel-bg, #fff);
            padding: 12px 16px;
            border-top: 1px solid var(--border-color, #e0e0e0);
            display: flex;
            gap: 8px;
            justify-content: flex-end;
            z-index: 10;
        `;

        const resetBtn = document.createElement('button');
        resetBtn.type = 'button';
        resetBtn.className = 'd-btn';
        resetBtn.innerHTML = '↺ 重置';
        resetBtn.style.cssText = `
            padding: 6px 16px;
            background: #f5f5f5;
            color: #666;
            border: 1px solid #d9d9d9;
            border-radius: 4px;
            cursor: pointer;
            font-size: 13px;
        `;
        resetBtn.addEventListener('click', () => this._onReset());

        const cancelBtn = document.createElement('button');
        cancelBtn.type = 'button';
        cancelBtn.className = 'd-btn';
        cancelBtn.innerHTML = '✕ 取消';
        cancelBtn.style.cssText = `
            padding: 6px 16px;
            background: #f5f5f5;
            color: #666;
            border: 1px solid #d9d9d9;
            border-radius: 4px;
            cursor: pointer;
            font-size: 13px;
        `;
        cancelBtn.addEventListener('click', () => this._onCancel());

        const confirmBtn = document.createElement('button');
        confirmBtn.type = 'button';
        confirmBtn.className = 'd-btn d-btn-primary';
        confirmBtn.id = 'panelConfirmBtn';
        confirmBtn.innerHTML = '✓ 确认';
        confirmBtn.style.cssText = `
            padding: 6px 16px;
            background: #1890ff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 13px;
            opacity: 0.5;
        `;
        confirmBtn.disabled = true;
        confirmBtn.addEventListener('click', () => this._onConfirm());

        buttonArea.appendChild(resetBtn);
        buttonArea.appendChild(cancelBtn);
        buttonArea.appendChild(confirmBtn);

        return buttonArea;
    }

    _updateButtonState() {
        const confirmBtn = this.container.querySelector('#panelConfirmBtn');
        if (confirmBtn) {
            const hasChanges = this.pendingChanges.size > 0;
            confirmBtn.disabled = !hasChanges;
            confirmBtn.style.opacity = hasChanges ? '1' : '0.5';
            confirmBtn.style.cursor = hasChanges ? 'pointer' : 'not-allowed';
        }
    }

    _onConfirm() {
        if (this.pendingChanges.size === 0) {
            console.log('[PanelManager] No changes to confirm');
            return;
        }

        console.log(`[PanelManager] Confirming ${this.pendingChanges.size} changes`);
        
        this._applyChanges();
        this.pendingChanges.clear();
        this.originalData = JSON.parse(JSON.stringify(this.currentData));
        this._updateButtonState();
        
        console.log('[PanelManager] Changes confirmed and applied');
    }

    _onCancel() {
        if (this.pendingChanges.size === 0) {
            console.log('[PanelManager] No changes to cancel');
            return;
        }

        console.log(`[PanelManager] Canceling ${this.pendingChanges.size} changes`);
        
        this.pendingChanges.forEach((value, key) => {
            const originalValue = this._getValue(key, this.originalData);
            this._setValue(key, originalValue, this.currentData);
            
            const input = this.container.querySelector(`[name="${key}"]`);
            if (input) {
                if (input.type === 'checkbox') {
                    input.checked = originalValue;
                } else {
                    input.value = originalValue !== undefined && originalValue !== null ? originalValue : '';
                }
            }
        });
        
        this.pendingChanges.clear();
        this._updateButtonState();
        
        console.log('[PanelManager] Changes canceled');
    }

    _onReset() {
        console.log('[PanelManager] Resetting all fields');
        
        this.currentData = JSON.parse(JSON.stringify(this.originalData));
        this.pendingChanges.clear();
        
        let schema;
        if (this.currentType === 'activity' && this.currentData) {
            schema = PanelSchema.getActivitySchema(this.currentData);
        } else {
            schema = PanelSchema[this.currentType];
        }
        
        if (schema) {
            this._renderContent(schema.fields[this.currentTab], this.currentData);
        }
        
        this._updateButtonState();
        
        console.log('[PanelManager] All fields reset');
    }

    _applyChanges() {
        if (this.currentType === 'activity' && this.store) {
            this.store.updateActivity(this.currentData);
            console.log('[PanelManager] Activity updated:', this.currentData.activityDefId);
        } else if (this.currentType === 'process' && this.store) {
            this.store.updateProcess(this.currentData);
            console.log('[PanelManager] Process updated:', this.currentData.processDefId);
        } else if (this.currentType === 'route' && this.store) {
            this.store.updateRoute(this.currentData);
            console.log('[PanelManager] Route updated:', this.currentData.routeDefId);
        }
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

            el.addEventListener('change', (e) => {
                let value;
                if (el.type === 'checkbox') {
                    value = el.checked;
                } else if (el.type === 'number') {
                    value = parseFloat(el.value);
                } else {
                    value = el.value;
                }
                
                this._onFieldChange(name, value);
            });

            el.addEventListener('input', (e) => {
                if (this._changeTimeout) {
                    clearTimeout(this._changeTimeout);
                }
                this._changeTimeout = setTimeout(() => {
                    let value;
                    if (el.type === 'number') {
                        value = parseFloat(el.value) || 0;
                    } else {
                        value = el.value;
                    }
                    this._onFieldChange(name, value);
                }, 500);
            });
        });

        container.querySelectorAll('.d-btn-add').forEach(btn => {
            btn.addEventListener('click', () => {
                const listName = btn.dataset.listName;
                const list = this._getValue(listName, this.currentData) || [];
                list.push({});
                this._setValue(listName, list, this.currentData);
                this.pendingChanges.set(listName, list);
                this._updateButtonState();
                this.render(this.currentType, this.currentData);
            });
        });

        container.querySelectorAll('.d-btn-add-kv').forEach(btn => {
            btn.addEventListener('click', () => {
                const kvName = btn.dataset.kvName;
                const kv = this._getValue(kvName, this.currentData) || {};
                kv['new_key'] = '';
                this._setValue(kvName, kv, this.currentData);
                this.pendingChanges.set(kvName, kv);
                this._updateButtonState();
                this.render(this.currentType, this.currentData);
            });
        });

        container.querySelectorAll('.d-btn-remove').forEach(btn => {
            btn.addEventListener('click', () => {
                const item = btn.closest('.d-list-item, .d-kv-item');
                if (item) {
                    item.remove();
                    this.pendingChanges.set('_list_modified', true);
                    this._updateButtonState();
                }
            });
        });
    }

    _onFieldChange(fieldName, fieldValue) {
        const originalValue = this._getValue(fieldName, this.originalData);
        
        if (!this._deepEqual(originalValue, fieldValue)) {
            this.pendingChanges.set(fieldName, fieldValue);
            this._setValue(fieldName, fieldValue, this.currentData);
            console.log(`[PanelManager] Change recorded: ${fieldName} from ${originalValue} to ${fieldValue}`);
        } else {
            this.pendingChanges.delete(fieldName);
            this._setValue(fieldName, fieldValue, this.currentData);
            console.log(`[PanelManager] Change reverted to original: ${fieldName}`);
        }
        
        this._updateButtonState();
    }

    _deepEqual(a, b) {
        if (a === b) return true;
        if (a == null || b == null) return false;
        if (typeof a !== typeof b) return false;

        if (typeof a === 'object') {
            const keysA = Object.keys(a);
            const keysB = Object.keys(b);
            if (keysA.length !== keysB.length) return false;
            return keysA.every(key => this._deepEqual(a[key], b[key]));
        }

        return false;
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
