/**
 * TableListPanel - 统一表格列表面板组件
 * 
 * 用于管理监听器(Listeners)、权限组(RightGroups)等表格数据属性
 * 提供统一的增删改查、排序、验证功能
 * 
 * 参考原有Swing设计：
 * - ListenersPanel - 监听器配置面板
 * - RightGroupsPanel - 权限组配置面板
 * 
 * @author AI Assistant
 * @version 1.0
 */

class TableListPanel {
    constructor(options = {}) {
        this.options = {
            // 面板配置
            title: '数据列表',
            icon: 'list',
            emptyText: '暂无数据，点击添加',
            addButtonText: '添加',
            
            // 表格列定义
            columns: [],
            
            // 数据配置
            dataKey: 'items',
            idField: 'id',
            
            // 操作配置
            allowAdd: true,
            allowEdit: true,
            allowDelete: true,
            allowSort: true,
            
            // 验证配置
            validationRules: {},
            
            // 编辑对话框配置
            editDialog: {
                title: '编辑',
                width: 500,
                height: 'auto'
            },
            
            // 事件回调
            onAdd: null,
            onEdit: null,
            onDelete: null,
            onChange: null,
            onValidate: null,
            
            ...options
        };
        
        this.data = [];
        this.container = null;
        this.tableElement = null;
        this.selectedIndex = -1;
    }

    /**
     * 渲染面板到容器
     */
    render(container) {
        this.container = container;
        container.innerHTML = '';
        
        // 创建面板结构
        const panel = document.createElement('div');
        panel.className = 'table-list-panel';
        
        // 头部工具栏
        panel.appendChild(this._createToolbar());
        
        // 表格区域
        panel.appendChild(this._createTableArea());
        
        container.appendChild(panel);
        
        // 刷新数据
        this.refresh();
        
        return this;
    }

    /**
     * 创建工具栏
     */
    _createToolbar() {
        const toolbar = document.createElement('div');
        toolbar.className = 'panel-toolbar';
        
        // 标题
        const title = document.createElement('span');
        title.className = 'panel-title';
        title.innerHTML = `<i class="icon-${this.options.icon}"></i> ${this.options.title}`;
        toolbar.appendChild(title);
        
        // 添加按钮
        if (this.options.allowAdd) {
            const addBtn = document.createElement('button');
            addBtn.className = 'btn btn-primary btn-sm';
            addBtn.innerHTML = `<i class="icon-plus"></i> ${this.options.addButtonText}`;
            addBtn.onclick = () => this._handleAdd();
            toolbar.appendChild(addBtn);
        }
        
        return toolbar;
    }

    /**
     * 创建表格区域
     */
    _createTableArea() {
        const area = document.createElement('div');
        area.className = 'table-area';
        
        // 创建表格
        const table = document.createElement('table');
        table.className = 'data-table';
        
        // 表头
        const thead = document.createElement('thead');
        const headerRow = document.createElement('tr');
        
        // 序号列
        const indexTh = document.createElement('th');
        indexTh.className = 'col-index';
        indexTh.textContent = '#';
        headerRow.appendChild(indexTh);
        
        // 数据列
        this.options.columns.forEach(col => {
            const th = document.createElement('th');
            th.className = `col-${col.field}`;
            th.textContent = col.title;
            if (col.width) {
                th.style.width = col.width;
            }
            headerRow.appendChild(th);
        });
        
        // 操作列
        if (this.options.allowEdit || this.options.allowDelete) {
            const actionTh = document.createElement('th');
            actionTh.className = 'col-actions';
            actionTh.textContent = '操作';
            headerRow.appendChild(actionTh);
        }
        
        thead.appendChild(headerRow);
        table.appendChild(thead);
        
        // 表体
        const tbody = document.createElement('tbody');
        tbody.className = 'table-body';
        this.tableElement = tbody;
        table.appendChild(tbody);
        
        area.appendChild(table);
        
        // 空数据提示
        this.emptyTip = document.createElement('div');
        this.emptyTip.className = 'empty-tip';
        this.emptyTip.textContent = this.options.emptyText;
        area.appendChild(this.emptyTip);
        
        return area;
    }

    /**
     * 刷新表格数据
     */
    refresh() {
        if (!this.tableElement) return;
        
        this.tableElement.innerHTML = '';
        
        if (this.data.length === 0) {
            this.emptyTip.style.display = 'block';
            return;
        }
        
        this.emptyTip.style.display = 'none';
        
        this.data.forEach((item, index) => {
            const row = this._createRow(item, index);
            this.tableElement.appendChild(row);
        });
    }

    /**
     * 创建数据行
     */
    _createRow(item, index) {
        const row = document.createElement('tr');
        row.dataset.index = index;
        
        if (index === this.selectedIndex) {
            row.classList.add('selected');
        }
        
        // 序号
        const indexTd = document.createElement('td');
        indexTd.className = 'col-index';
        indexTd.textContent = index + 1;
        row.appendChild(indexTd);
        
        // 数据列
        this.options.columns.forEach(col => {
            const td = document.createElement('td');
            td.className = `col-${col.field}`;
            
            let value = this._getNestedValue(item, col.field);
            
            // 使用formatter格式化显示
            if (col.formatter) {
                td.innerHTML = col.formatter(value, item, index);
            } else {
                td.textContent = value !== undefined && value !== null ? value : '';
            }
            
            row.appendChild(td);
        });
        
        // 操作列
        if (this.options.allowEdit || this.options.allowDelete) {
            const actionTd = document.createElement('td');
            actionTd.className = 'col-actions';
            
            if (this.options.allowEdit) {
                const editBtn = document.createElement('button');
                editBtn.className = 'btn-icon btn-edit';
                editBtn.title = '编辑';
                editBtn.innerHTML = '<i class="icon-edit"></i>';
                editBtn.onclick = (e) => {
                    e.stopPropagation();
                    this._handleEdit(index);
                };
                actionTd.appendChild(editBtn);
            }
            
            if (this.options.allowDelete) {
                const deleteBtn = document.createElement('button');
                deleteBtn.className = 'btn-icon btn-delete';
                deleteBtn.title = '删除';
                deleteBtn.innerHTML = '<i class="icon-trash"></i>';
                deleteBtn.onclick = (e) => {
                    e.stopPropagation();
                    this._handleDelete(index);
                };
                actionTd.appendChild(deleteBtn);
            }
            
            // 排序按钮
            if (this.options.allowSort) {
                if (index > 0) {
                    const upBtn = document.createElement('button');
                    upBtn.className = 'btn-icon btn-up';
                    upBtn.title = '上移';
                    upBtn.innerHTML = '<i class="icon-arrow-up"></i>';
                    upBtn.onclick = (e) => {
                        e.stopPropagation();
                        this._handleMove(index, -1);
                    };
                    actionTd.appendChild(upBtn);
                }
                
                if (index < this.data.length - 1) {
                    const downBtn = document.createElement('button');
                    downBtn.className = 'btn-icon btn-down';
                    downBtn.title = '下移';
                    downBtn.innerHTML = '<i class="icon-arrow-down"></i>';
                    downBtn.onclick = (e) => {
                        e.stopPropagation();
                        this._handleMove(index, 1);
                    };
                    actionTd.appendChild(downBtn);
                }
            }
            
            row.appendChild(actionTd);
        }
        
        // 行点击选中
        row.onclick = () => {
            this._selectRow(index);
        };
        
        return row;
    }

    /**
     * 获取嵌套属性值
     */
    _getNestedValue(obj, path) {
        return path.split('.').reduce((o, p) => o && o[p], obj);
    }

    /**
     * 设置嵌套属性值
     */
    _setNestedValue(obj, path, value) {
        const parts = path.split('.');
        const last = parts.pop();
        const target = parts.reduce((o, p) => {
            if (!o[p]) o[p] = {};
            return o[p];
        }, obj);
        target[last] = value;
    }

    /**
     * 选中行
     */
    _selectRow(index) {
        this.selectedIndex = index;
        
        // 更新UI
        const rows = this.tableElement.querySelectorAll('tr');
        rows.forEach((row, i) => {
            if (i === index) {
                row.classList.add('selected');
            } else {
                row.classList.remove('selected');
            }
        });
    }

    /**
     * 处理添加
     */
    _handleAdd() {
        // 创建空数据对象
        const newItem = {};
        this.options.columns.forEach(col => {
            if (col.defaultValue !== undefined) {
                this._setNestedValue(newItem, col.field, col.defaultValue);
            }
        });
        
        // 调用回调
        if (this.options.onAdd) {
            this.options.onAdd(newItem, (confirmed, item) => {
                if (confirmed && item) {
                    this.addItem(item);
                }
            });
        } else {
            // 默认使用对话框编辑
            this._showEditDialog('添加', newItem, (confirmed, item) => {
                if (confirmed) {
                    this.addItem(item);
                }
            });
        }
    }

    /**
     * 处理编辑
     */
    _handleEdit(index) {
        const item = { ...this.data[index] };
        
        if (this.options.onEdit) {
            this.options.onEdit(item, index, (confirmed, updatedItem) => {
                if (confirmed && updatedItem) {
                    this.updateItem(index, updatedItem);
                }
            });
        } else {
            this._showEditDialog('编辑', item, (confirmed, updatedItem) => {
                if (confirmed) {
                    this.updateItem(index, updatedItem);
                }
            });
        }
    }

    /**
     * 处理删除
     */
    _handleDelete(index) {
        const item = this.data[index];
        
        if (this.options.onDelete) {
            this.options.onDelete(item, index, (confirmed) => {
                if (confirmed) {
                    this.removeItem(index);
                }
            });
        } else {
            // 默认确认对话框
            if (confirm('确定要删除这条数据吗？')) {
                this.removeItem(index);
            }
        }
    }

    /**
     * 处理移动
     */
    _handleMove(index, direction) {
        const newIndex = index + direction;
        if (newIndex < 0 || newIndex >= this.data.length) return;
        
        // 交换数据
        [this.data[index], this.data[newIndex]] = [this.data[newIndex], this.data[index]];
        
        // 更新选中索引
        if (this.selectedIndex === index) {
            this.selectedIndex = newIndex;
        } else if (this.selectedIndex === newIndex) {
            this.selectedIndex = index;
        }
        
        // 刷新并触发变更
        this.refresh();
        this._triggerChange();
    }

    /**
     * 显示编辑对话框
     */
    _showEditDialog(title, item, callback) {
        // 创建对话框
        const dialog = document.createElement('div');
        dialog.className = 'modal-dialog';
        
        const content = document.createElement('div');
        content.className = 'modal-content';
        content.style.width = this.options.editDialog.width + 'px';
        
        // 头部
        const header = document.createElement('div');
        header.className = 'modal-header';
        header.innerHTML = `<h4>${title}</h4><button class="btn-close">&times;</button>`;
        content.appendChild(header);
        
        // 主体 - 表单
        const body = document.createElement('div');
        body.className = 'modal-body';
        
        const form = document.createElement('form');
        form.className = 'edit-form';
        
        this.options.columns.forEach(col => {
            if (col.editable === false) return;
            
            const formGroup = document.createElement('div');
            formGroup.className = 'form-group';
            
            const label = document.createElement('label');
            label.textContent = col.title + (col.required ? ' *' : '');
            formGroup.appendChild(label);
            
            let input;
            const value = this._getNestedValue(item, col.field);
            
            if (col.type === 'select' && col.options) {
                input = document.createElement('select');
                col.options.forEach(opt => {
                    const option = document.createElement('option');
                    option.value = opt.value;
                    option.textContent = opt.label;
                    if (opt.value === value) {
                        option.selected = true;
                    }
                    input.appendChild(option);
                });
            } else if (col.type === 'textarea') {
                input = document.createElement('textarea');
                input.rows = col.rows || 3;
                input.value = value || '';
            } else if (col.type === 'checkbox') {
                input = document.createElement('input');
                input.type = 'checkbox';
                input.checked = !!value;
            } else {
                input = document.createElement('input');
                input.type = col.type || 'text';
                input.value = value || '';
            }
            
            input.className = 'form-control';
            input.name = col.field;
            if (col.required) {
                input.required = true;
            }
            if (col.placeholder) {
                input.placeholder = col.placeholder;
            }
            
            formGroup.appendChild(input);
            form.appendChild(formGroup);
        });
        
        body.appendChild(form);
        content.appendChild(body);
        
        // 底部按钮
        const footer = document.createElement('div');
        footer.className = 'modal-footer';
        footer.innerHTML = `
            <button class="btn btn-default btn-cancel">取消</button>
            <button class="btn btn-primary btn-confirm">确定</button>
        `;
        content.appendChild(footer);
        
        dialog.appendChild(content);
        document.body.appendChild(dialog);
        
        // 遮罩
        const overlay = document.createElement('div');
        overlay.className = 'modal-overlay';
        document.body.appendChild(overlay);
        
        // 事件处理
        const closeDialog = () => {
            dialog.remove();
            overlay.remove();
        };
        
        header.querySelector('.btn-close').onclick = () => {
            closeDialog();
            callback(false);
        };
        
        footer.querySelector('.btn-cancel').onclick = () => {
            closeDialog();
            callback(false);
        };
        
        footer.querySelector('.btn-confirm').onclick = () => {
            // 收集表单数据
            const updatedItem = { ...item };
            this.options.columns.forEach(col => {
                if (col.editable === false) return;
                
                const input = form.querySelector(`[name="${col.field}"]`);
                let value;
                
                if (col.type === 'checkbox') {
                    value = input.checked;
                } else if (col.type === 'number') {
                    value = parseFloat(input.value) || 0;
                } else {
                    value = input.value;
                }
                
                this._setNestedValue(updatedItem, col.field, value);
            });
            
            // 验证
            if (this._validateItem(updatedItem)) {
                closeDialog();
                callback(true, updatedItem);
            }
        };
        
        overlay.onclick = closeDialog;
    }

    /**
     * 验证数据项
     */
    _validateItem(item) {
        const rules = this.options.validationRules;
        
        for (const field in rules) {
            const value = this._getNestedValue(item, field);
            const rule = rules[field];
            
            if (rule.required && (!value || value === '')) {
                alert(`${field} 不能为空`);
                return false;
            }
            
            if (rule.pattern && !rule.pattern.test(value)) {
                alert(rule.message || `${field} 格式不正确`);
                return false;
            }
            
            if (rule.validator && !rule.validator(value, item)) {
                alert(rule.message || `${field} 验证失败`);
                return false;
            }
        }
        
        if (this.options.onValidate) {
            return this.options.onValidate(item);
        }
        
        return true;
    }

    /**
     * 添加数据项
     */
    addItem(item) {
        // 生成ID
        if (!item[this.options.idField]) {
            item[this.options.idField] = this._generateId();
        }
        
        this.data.push(item);
        this.refresh();
        this._triggerChange();
        
        return this;
    }

    /**
     * 更新数据项
     */
    updateItem(index, item) {
        if (index < 0 || index >= this.data.length) return this;
        
        // 保留原ID
        if (!item[this.options.idField]) {
            item[this.options.idField] = this.data[index][this.options.idField];
        }
        
        this.data[index] = item;
        this.refresh();
        this._triggerChange();
        
        return this;
    }

    /**
     * 删除数据项
     */
    removeItem(index) {
        if (index < 0 || index >= this.data.length) return this;
        
        this.data.splice(index, 1);
        
        if (this.selectedIndex === index) {
            this.selectedIndex = -1;
        } else if (this.selectedIndex > index) {
            this.selectedIndex--;
        }
        
        this.refresh();
        this._triggerChange();
        
        return this;
    }

    /**
     * 设置数据
     */
    setData(data) {
        this.data = Array.isArray(data) ? [...data] : [];
        this.selectedIndex = -1;
        this.refresh();
        return this;
    }

    /**
     * 获取数据
     */
    getData() {
        return [...this.data];
    }

    /**
     * 生成唯一ID
     */
    _generateId() {
        return 'id_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    }

    /**
     * 触发变更事件
     */
    _triggerChange() {
        if (this.options.onChange) {
            this.options.onChange(this.getData());
        }
    }

    /**
     * 销毁面板
     */
    destroy() {
        if (this.container) {
            this.container.innerHTML = '';
        }
        this.data = [];
        this.selectedIndex = -1;
    }
}

// 导出
if (typeof module !== 'undefined' && module.exports) {
    module.exports = TableListPanel;
}
