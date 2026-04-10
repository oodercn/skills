/**
 * 面板插件接口
 * 所有面板插件必须实现此接口
 */
class PanelPlugin {
    constructor(name, icon) {
        this.name = name;
        this.icon = icon;
        this.container = null;
    }

    /**
     * 初始化插件
     * @param {HTMLElement} container - 面板容器元素
     */
    init(container) {
        this.container = container;
    }

    /**
     * 渲染面板内容
     * @param {Object} data - 数据对象
     */
    render(data) {
        throw new Error('PanelPlugin.render() must be implemented');
    }

    /**
     * 销毁插件
     */
    destroy() {
        if (this.container) {
            this.container.innerHTML = '';
        }
    }

    /**
     * 获取面板标题
     * @param {Object} data - 数据对象
     * @returns {string} 面板标题
     */
    getTitle(data) {
        return this.name;
    }

    /**
     * 验证数据是否有效
     * @param {Object} data - 数据对象
     * @returns {boolean} 是否有效
     */
    validate(data) {
        return !!data;
    }
}

/**
 * 基础表单面板插件
 * 提供通用的表单渲染功能，支持手动确认更新，避免频繁重绘
 */
class FormPanelPlugin extends PanelPlugin {
    constructor(name, icon, schema) {
        super(name, icon);
        this.schema = schema;
        this.currentData = null;
        this.originalData = null; // 原始数据副本
        this.store = window.store; // 全局store引用
        this._changeTimeout = null;
        this._pendingChanges = new Map(); // 待确认的变更
    }

    render(data) {
        this.currentData = JSON.parse(JSON.stringify(data)); // 深拷贝
        this.originalData = JSON.parse(JSON.stringify(data)); // 保存原始数据
        this._pendingChanges.clear();
        if (!this.container) return;

        const wrapper = document.createElement('div');
        wrapper.className = 'd-panel-form-wrapper';

        const form = this._createForm();
        wrapper.appendChild(form);

        // 添加确认按钮区域
        const buttonArea = this._createButtonArea();
        wrapper.appendChild(buttonArea);

        this.container.innerHTML = '';
        this.container.appendChild(wrapper);

        // 绑定输入变化事件（不再自动更新，只记录变更）
        this._bindInputEvents(form);
    }

    _createForm() {
        const form = document.createElement('form');
        form.className = 'd-panel-form';

        if (!this.schema || !this.schema.fields) {
            form.innerHTML = '<div class="d-empty">无配置项</div>';
            return form;
        }

        this.schema.fields.forEach(field => {
            const fieldElement = this._createField(field);
            if (fieldElement) {
                form.appendChild(fieldElement);
            }
        });

        return form;
    }

    /**
     * 创建按钮区域（确认、取消、重置）
     */
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

        // 确认按钮
        const confirmBtn = document.createElement('button');
        confirmBtn.type = 'button';
        confirmBtn.className = 'd-btn d-btn-primary';
        confirmBtn.innerHTML = '✓ 确认';
        confirmBtn.style.cssText = `
            padding: 6px 16px;
            background: #1890ff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 13px;
            transition: all 0.2s;
        `;
        confirmBtn.addEventListener('click', () => this._onConfirm());
        confirmBtn.addEventListener('mouseenter', () => {
            confirmBtn.style.background = '#40a9ff';
        });
        confirmBtn.addEventListener('mouseleave', () => {
            confirmBtn.style.background = '#1890ff';
        });

        // 取消按钮
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
            transition: all 0.2s;
        `;
        cancelBtn.addEventListener('click', () => this._onCancel());
        cancelBtn.addEventListener('mouseenter', () => {
            cancelBtn.style.background = '#e8e8e8';
        });
        cancelBtn.addEventListener('mouseleave', () => {
            cancelBtn.style.background = '#f5f5f5';
        });

        // 重置按钮
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
            transition: all 0.2s;
        `;
        resetBtn.addEventListener('click', () => this._onReset());
        resetBtn.addEventListener('mouseenter', () => {
            resetBtn.style.background = '#e8e8e8';
        });
        resetBtn.addEventListener('mouseleave', () => {
            resetBtn.style.background = '#f5f5f5';
        });

        buttonArea.appendChild(resetBtn);
        buttonArea.appendChild(cancelBtn);
        buttonArea.appendChild(confirmBtn);

        return buttonArea;
    }

    _bindInputEvents(form) {
        // 监听所有输入元素的变化
        const inputs = form.querySelectorAll('input, textarea, select');
        inputs.forEach(input => {
            input.addEventListener('change', (e) => this._onFieldChange(e));
            input.addEventListener('blur', (e) => this._onFieldChange(e));
            // 文本输入使用防抖
            if (input.type === 'text' || input.tagName === 'TEXTAREA') {
                input.addEventListener('input', (e) => this._onFieldInput(e));
            }
        });
    }

    _onFieldInput(e) {
        // 防抖处理，避免频繁更新
        if (this._changeTimeout) {
            clearTimeout(this._changeTimeout);
        }
        this._changeTimeout = setTimeout(() => {
            this._onFieldChange(e);
        }, 500);
    }

    _onFieldChange(e) {
        const fieldName = e.target.name;
        let fieldValue = e.target.value;

        // 处理复选框
        if (e.target.type === 'checkbox') {
            fieldValue = e.target.checked;
        }

        // 处理数字输入
        if (e.target.type === 'number' && fieldValue !== '') {
            fieldValue = Number(fieldValue);
        }

        console.log(`[PanelPlugin] Field changed: ${fieldName} = ${fieldValue}`);

        // 只记录到待变更列表，不立即更新
        if (this.currentData) {
            const originalValue = this.originalData[fieldName];

            // 深比较，只有真正变化才记录
            if (!this._deepEqual(originalValue, fieldValue)) {
                this._pendingChanges.set(fieldName, fieldValue);
                this.currentData[fieldName] = fieldValue;
                console.log(`[PanelPlugin] Change recorded: ${fieldName} from ${originalValue} to ${fieldValue}`);
                this._updateButtonState();
            } else {
                // 如果改回原始值，从待变更列表移除
                this._pendingChanges.delete(fieldName);
                this.currentData[fieldName] = fieldValue;
                console.log(`[PanelPlugin] Change reverted to original: ${fieldName}`);
                this._updateButtonState();
            }
        }
    }

    /**
     * 更新按钮状态（根据是否有待确认的变更）
     */
    _updateButtonState() {
        const buttonArea = this.container?.querySelector('.d-panel-button-area');
        if (!buttonArea) return;

        const confirmBtn = buttonArea.querySelector('.d-btn-primary');
        if (confirmBtn) {
            const hasChanges = this._pendingChanges.size > 0;
            confirmBtn.disabled = !hasChanges;
            confirmBtn.style.opacity = hasChanges ? '1' : '0.5';
            confirmBtn.style.cursor = hasChanges ? 'pointer' : 'not-allowed';
            console.log(`[PanelPlugin] Button state updated: hasChanges=${hasChanges}, pending=${this._pendingChanges.size}`);
        }
    }

    /**
     * 确认按钮点击处理
     */
    _onConfirm() {
        if (this._pendingChanges.size === 0) {
            console.log('[PanelPlugin] No changes to confirm');
            return;
        }

        console.log(`[PanelPlugin] Confirming ${this._pendingChanges.size} changes:`, Array.from(this._pendingChanges.keys()));

        // 应用所有变更
        this._pendingChanges.forEach((value, key) => {
            // 触发联动更新
            this._triggerCascadeUpdate(key, value);
        });

        // 通知store更新（一次性更新所有变更）
        this._notifyStoreUpdate();

        // 清空待变更列表
        this._pendingChanges.clear();

        // 更新原始数据为当前数据
        this.originalData = JSON.parse(JSON.stringify(this.currentData));

        // 更新按钮状态
        this._updateButtonState();

        console.log('[PanelPlugin] Changes confirmed and applied');
    }

    /**
     * 取消按钮点击处理
     */
    _onCancel() {
        if (this._pendingChanges.size === 0) {
            console.log('[PanelPlugin] No changes to cancel');
            return;
        }

        console.log(`[PanelPlugin] Canceling ${this._pendingChanges.size} changes`);

        // 恢复所有变更的字段为原始值
        this._pendingChanges.forEach((value, key) => {
            const originalValue = this.originalData[key];
            this.currentData[key] = originalValue;

            // 更新UI显示
            const input = this.container?.querySelector(`[name="${key}"]`);
            if (input) {
                if (input.type === 'checkbox') {
                    input.checked = originalValue;
                } else {
                    input.value = originalValue !== undefined && originalValue !== null ? originalValue : '';
                }
            }
        });

        // 清空待变更列表
        this._pendingChanges.clear();

        // 更新按钮状态
        this._updateButtonState();

        console.log('[PanelPlugin] Changes canceled, values restored');
    }

    /**
     * 重置按钮点击处理
     */
    _onReset() {
        console.log('[PanelPlugin] Resetting all fields to original values');

        // 恢复所有字段为原始值
        this.currentData = JSON.parse(JSON.stringify(this.originalData));
        this._pendingChanges.clear();

        // 重新渲染表单
        const form = this.container?.querySelector('.d-panel-form');
        if (form) {
            // 更新所有输入框的值
            const inputs = form.querySelectorAll('input, textarea, select');
            inputs.forEach(input => {
                const fieldName = input.name;
                const originalValue = this.originalData[fieldName];

                if (input.type === 'checkbox') {
                    input.checked = originalValue;
                } else {
                    input.value = originalValue !== undefined && originalValue !== null ? originalValue : '';
                }
            });
        }

        // 更新按钮状态
        this._updateButtonState();

        console.log('[PanelPlugin] All fields reset');
    }

    /**
     * 深度比较两个值是否相等
     */
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

    /**
     * 触发联动更新
     * 当某些字段变化时，更新相关的其他字段或UI
     */
    _triggerCascadeUpdate(fieldName, fieldValue) {
        // 如果修改的是活动名称，更新面板标题
        if (fieldName === 'name' && this.currentData) {
            // 更新面板标题
            const panelTitle = document.querySelector('#panelTitle');
            if (panelTitle) {
                panelTitle.textContent = fieldValue || '活动属性';
            }
        }
    }

    /**
     * 通知store更新数据
     */
    _notifyStoreUpdate() {
        if (!this.store || !this.currentData) return;
        
        // 根据数据类型决定如何更新store
        if (this.currentData.activityDefId) {
            // 更新活动
            this.store.updateActivity(this.currentData);
            console.log('[PanelPlugin] Store activity updated:', this.currentData.activityDefId);
        } else if (this.currentData.routeDefId) {
            // 更新路由
            this.store.updateRoute(this.currentData);
            console.log('[PanelPlugin] Store route updated:', this.currentData.routeDefId);
        } else if (this.currentData.processDefId) {
            // 更新流程
            this.store.updateProcess(this.currentData);
            console.log('[PanelPlugin] Store process updated:', this.currentData.processDefId);
        }
    }

    _createField(field) {
        const wrapper = document.createElement('div');
        wrapper.className = 'd-form-field';

        switch (field.type) {
            case 'section':
                return this._createSection(field);
            case 'text':
                return this._createTextField(field);
            case 'textarea':
                return this._createTextareaField(field);
            case 'number':
                return this._createNumberField(field);
            case 'select':
                return this._createSelectField(field);
            case 'checkbox':
                return this._createCheckboxField(field);
            default:
                return null;
        }
    }

    _createSection(field) {
        const section = document.createElement('div');
        section.className = 'd-form-section';
        section.innerHTML = `<h4>${field.title}</h4>`;
        return section;
    }

    _createTextField(field) {
        const wrapper = document.createElement('div');
        wrapper.className = 'd-form-field';
        wrapper.innerHTML = `
            <label>${field.label}${field.required ? ' *' : ''}</label>
            <input type="text" name="${field.name}" 
                   value="${this._getValue(field.name)}" 
                   ${field.readonly ? 'readonly' : ''}
                   ${field.required ? 'required' : ''}>
        `;
        return wrapper;
    }

    _createTextareaField(field) {
        const wrapper = document.createElement('div');
        wrapper.className = 'd-form-field';
        wrapper.innerHTML = `
            <label>${field.label}${field.required ? ' *' : ''}</label>
            <textarea name="${field.name}" rows="3"
                      ${field.readonly ? 'readonly' : ''}
                      ${field.required ? 'required' : ''}
                      placeholder="${field.placeholder || ''}">${this._getValue(field.name)}</textarea>
        `;
        return wrapper;
    }

    _createNumberField(field) {
        const wrapper = document.createElement('div');
        wrapper.className = 'd-form-field';
        wrapper.innerHTML = `
            <label>${field.label}${field.required ? ' *' : ''}</label>
            <input type="number" name="${field.name}" 
                   value="${this._getValue(field.name)}" 
                   ${field.min !== undefined ? `min="${field.min}"` : ''}
                   ${field.max !== undefined ? `max="${field.max}"` : ''}
                   ${field.readonly ? 'readonly' : ''}>
        `;
        return wrapper;
    }

    _createSelectField(field) {
        const wrapper = document.createElement('div');
        wrapper.className = 'd-form-field';
        const options = field.options?.map(opt => 
            `<option value="${opt.value}" ${this._getValue(field.name) === opt.value ? 'selected' : ''}>${opt.label}</option>`
        ).join('') || '';
        
        wrapper.innerHTML = `
            <label>${field.label}${field.required ? ' *' : ''}</label>
            <select name="${field.name}" ${field.readonly ? 'disabled' : ''}>
                ${options}
            </select>
        `;
        return wrapper;
    }

    _createCheckboxField(field) {
        const wrapper = document.createElement('div');
        wrapper.className = 'd-form-field d-form-field-checkbox';
        wrapper.innerHTML = `
            <label>
                <input type="checkbox" name="${field.name}" 
                       ${this._getValue(field.name) ? 'checked' : ''}
                       ${field.readonly ? 'disabled' : ''}>
                ${field.label}
            </label>
        `;
        return wrapper;
    }

    _getValue(fieldName) {
        if (!this.currentData) return '';
        const value = this.currentData[fieldName];
        return value !== undefined && value !== null ? value : '';
    }

    /**
     * 获取表单数据
     * @returns {Object} 表单数据
     */
    getData() {
        if (!this.container) return null;
        
        const form = this.container.querySelector('form');
        if (!form) return null;

        const data = {};
        const formData = new FormData(form);
        
        formData.forEach((value, key) => {
            data[key] = value;
        });

        // 处理复选框
        form.querySelectorAll('input[type="checkbox"]').forEach(checkbox => {
            data[checkbox.name] = checkbox.checked;
        });

        return data;
    }
}

// 导出插件类
window.PanelPlugin = PanelPlugin;
window.FormPanelPlugin = FormPanelPlugin;
