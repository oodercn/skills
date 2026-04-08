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
 * 提供通用的表单渲染功能
 */
class FormPanelPlugin extends PanelPlugin {
    constructor(name, icon, schema) {
        super(name, icon);
        this.schema = schema;
        this.currentData = null;
    }

    render(data) {
        this.currentData = data;
        if (!this.container) return;

        const form = this._createForm();
        this.container.innerHTML = '';
        this.container.appendChild(form);
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
