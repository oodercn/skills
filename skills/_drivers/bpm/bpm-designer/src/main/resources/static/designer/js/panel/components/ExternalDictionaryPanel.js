/**
 * ExternalDictionaryPanel - 外部字典依赖面板基类
 * 
 * 用于处理需要外部读取字典表构建的面板：
 * - 表达式管理 (Expression Management)
 * - 添加办理人 (Performer Selection)
 * - 添加办理部门 (Department Selection)
 * 
 * 参考原有Swing设计：
 * - ExpressionPanel - 表达式管理面板
 * - PerformerPanel - 办理人选择面板
 * - DepartmentPanel - 部门选择面板
 * 
 * @author AI Assistant
 * @version 1.0
 */

class ExternalDictionaryPanel {
    constructor(options = {}) {
        this.options = {
            // 面板配置
            title: '字典选择',
            icon: 'book',
            
            // 字典配置
            dictionary: {
                type: null,           // 字典类型: 'expression', 'person', 'department', 'role', 'custom'
                url: null,            // 数据加载URL
                params: {},           // 额外参数
                rootId: null,         // 根节点ID
                lazyLoad: true,       // 是否懒加载
                cacheKey: null        // 缓存键
            },
            
            // 选择模式
            selection: {
                mode: 'single',       // single, multiple
                maxCount: 0,          // 最大选择数量，0表示无限制
                minCount: 0,          // 最小选择数量
                allowEmpty: false     // 是否允许空选择
            },
            
            // 显示配置
            display: {
                showTree: true,       // 是否显示树形结构
                showSearch: true,     // 是否显示搜索框
                showPath: true,       // 是否显示选择路径
                showPreview: false,   // 是否显示预览
                columns: []           // 表格列定义（当showTree为false时）
            },
            
            // 字段映射
            fieldMapping: {
                id: 'id',
                code: 'code',
                name: 'name',
                parentId: 'parentId',
                children: 'children',
                leaf: 'leaf',
                icon: 'icon'
            },
            
            // 事件回调
            onLoad: null,             // 数据加载完成
            onSelect: null,           // 选择变更
            onConfirm: null,          // 确认选择
            onCancel: null,           // 取消选择
            onError: null,            // 错误处理
            
            ...options
        };
        
        this.data = [];              // 字典数据
        this.selectedItems = [];     // 已选项
        this.filteredData = [];      // 过滤后的数据
        this.container = null;
        this.searchKeyword = '';
        this.loading = false;
        this.cache = new Map();      // 数据缓存
    }

    /**
     * 渲染面板到容器
     */
    render(container) {
        this.container = container;
        container.innerHTML = '';
        
        const panel = document.createElement('div');
        panel.className = 'external-dictionary-panel';
        
        // 头部区域
        panel.appendChild(this._createHeader());
        
        // 主体区域
        const body = document.createElement('div');
        body.className = 'panel-body';
        
        // 左侧：字典树/列表
        const leftPane = document.createElement('div');
        leftPane.className = 'dictionary-pane';
        leftPane.appendChild(this._createSearchBox());
        leftPane.appendChild(this._createDictionaryView());
        body.appendChild(leftPane);
        
        // 右侧：已选项
        const rightPane = document.createElement('div');
        rightPane.className = 'selection-pane';
        rightPane.appendChild(this._createSelectionHeader());
        rightPane.appendChild(this._createSelectionList());
        body.appendChild(rightPane);
        
        panel.appendChild(body);
        
        // 底部按钮
        panel.appendChild(this._createFooter());
        
        container.appendChild(panel);
        
        // 加载数据
        this.loadData();
        
        return this;
    }

    /**
     * 创建头部
     */
    _createHeader() {
        const header = document.createElement('div');
        header.className = 'panel-header';
        header.innerHTML = `
            <span class="panel-title">
                <i class="icon-${this.options.icon}"></i>
                ${this.options.title}
            </span>
            <span class="selection-count">已选: <span class="count">0</span></span>
        `;
        return header;
    }

    /**
     * 创建搜索框
     */
    _createSearchBox() {
        if (!this.options.display.showSearch) {
            return document.createElement('div');
        }
        
        const searchBox = document.createElement('div');
        searchBox.className = 'search-box';
        searchBox.innerHTML = `
            <input type="text" class="search-input" placeholder="搜索...">
            <button class="btn-search"><i class="icon-search"></i></button>
        `;
        
        const input = searchBox.querySelector('.search-input');
        input.addEventListener('input', (e) => {
            this.searchKeyword = e.target.value.trim();
            this._handleSearch();
        });
        
        return searchBox;
    }

    /**
     * 创建字典视图（树或表格）
     */
    _createDictionaryView() {
        const view = document.createElement('div');
        view.className = 'dictionary-view';
        
        if (this.options.display.showTree) {
            // 树形视图
            const tree = document.createElement('div');
            tree.className = 'dictionary-tree';
            this.treeElement = tree;
            view.appendChild(tree);
        } else {
            // 表格视图
            const table = document.createElement('table');
            table.className = 'dictionary-table';
            
            // 表头
            const thead = document.createElement('thead');
            const headerRow = document.createElement('tr');
            
            // 选择列
            const selectTh = document.createElement('th');
            selectTh.className = 'col-select';
            if (this.options.selection.mode === 'multiple') {
                selectTh.innerHTML = '<input type="checkbox" class="select-all">';
            }
            headerRow.appendChild(selectTh);
            
            // 数据列
            this.options.display.columns.forEach(col => {
                const th = document.createElement('th');
                th.textContent = col.title;
                if (col.width) th.style.width = col.width;
                headerRow.appendChild(th);
            });
            
            thead.appendChild(headerRow);
            table.appendChild(thead);
            
            // 表体
            const tbody = document.createElement('tbody');
            this.tableBody = tbody;
            table.appendChild(tbody);
            
            view.appendChild(table);
        }
        
        // 加载状态
        this.loadingElement = document.createElement('div');
        this.loadingElement.className = 'loading-overlay';
        this.loadingElement.innerHTML = '<i class="icon-loading"></i> 加载中...';
        this.loadingElement.style.display = 'none';
        view.appendChild(this.loadingElement);
        
        return view;
    }

    /**
     * 创建已选项头部
     */
    _createSelectionHeader() {
        const header = document.createElement('div');
        header.className = 'selection-header';
        header.innerHTML = `
            <span>已选项</span>
            <button class="btn-clear-all">清空</button>
        `;
        
        header.querySelector('.btn-clear-all').onclick = () => {
            this.clearSelection();
        };
        
        return header;
    }

    /**
     * 创建已选项列表
     */
    _createSelectionList() {
        const list = document.createElement('div');
        list.className = 'selection-list';
        this.selectionList = list;
        return list;
    }

    /**
     * 创建底部按钮
     */
    _createFooter() {
        const footer = document.createElement('div');
        footer.className = 'panel-footer';
        footer.innerHTML = `
            <button class="btn btn-default btn-cancel">取消</button>
            <button class="btn btn-primary btn-confirm">确定</button>
        `;
        
        footer.querySelector('.btn-cancel').onclick = () => {
            if (this.options.onCancel) {
                this.options.onCancel();
            }
        };
        
        footer.querySelector('.btn-confirm').onclick = () => {
            this._handleConfirm();
        };
        
        return footer;
    }

    /**
     * 加载字典数据
     */
    async loadData(parentId = null) {
        if (this.loading) return;
        
        const cacheKey = this._getCacheKey(parentId);
        
        // 检查缓存
        if (this.options.dictionary.cacheKey && this.cache.has(cacheKey)) {
            this.data = this.cache.get(cacheKey);
            this._renderDictionary();
            return;
        }
        
        this.loading = true;
        this._showLoading(true);
        
        try {
            let data;
            
            if (this.options.dictionary.type === 'custom' && this.options.dictionary.url) {
                // 从URL加载
                data = await this._fetchFromUrl(parentId);
            } else {
                // 使用内置字典加载器
                data = await this._loadBuiltinDictionary(parentId);
            }
            
            // 缓存数据
            if (this.options.dictionary.cacheKey) {
                this.cache.set(cacheKey, data);
            }
            
            this.data = data;
            this._renderDictionary();
            
            if (this.options.onLoad) {
                this.options.onLoad(data);
            }
        } catch (error) {
            console.error('加载字典数据失败:', error);
            if (this.options.onError) {
                this.options.onError(error);
            }
        } finally {
            this.loading = false;
            this._showLoading(false);
        }
    }

    /**
     * 从URL获取数据
     */
    async _fetchFromUrl(parentId) {
        const url = new URL(this.options.dictionary.url, window.location.origin);
        
        // 添加参数
        url.searchParams.append('type', this.options.dictionary.type);
        if (parentId) {
            url.searchParams.append('parentId', parentId);
        }
        if (this.options.dictionary.rootId) {
            url.searchParams.append('rootId', this.options.dictionary.rootId);
        }
        
        // 添加额外参数
        Object.entries(this.options.dictionary.params).forEach(([key, value]) => {
            url.searchParams.append(key, value);
        });
        
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        return this._normalizeData(result.data || result);
    }

    /**
     * 加载内置字典
     */
    async _loadBuiltinDictionary(parentId) {
        const { type } = this.options.dictionary;
        
        // 使用BpmDictionaryService（假设存在）
        if (window.BpmDictionaryService) {
            return await window.BpmDictionaryService.load(type, {
                parentId,
                rootId: this.options.dictionary.rootId,
                ...this.options.dictionary.params
            });
        }
        
        // 模拟数据（开发测试用）
        return this._getMockData(type, parentId);
    }

    /**
     * 获取模拟数据
     */
    _getMockData(type, parentId) {
        const mocks = {
            person: [
                { id: 'user1', code: 'U001', name: '张三', parentId: null, leaf: true },
                { id: 'user2', code: 'U002', name: '李四', parentId: null, leaf: true },
                { id: 'user3', code: 'U003', name: '王五', parentId: null, leaf: true }
            ],
            department: [
                { id: 'dept1', code: 'D001', name: '技术部', parentId: null, leaf: false },
                { id: 'dept2', code: 'D002', name: '产品部', parentId: null, leaf: false },
                { id: 'dept11', code: 'D0011', name: '前端组', parentId: 'dept1', leaf: true },
                { id: 'dept12', code: 'D0012', name: '后端组', parentId: 'dept1', leaf: true }
            ],
            role: [
                { id: 'role1', code: 'R001', name: '管理员', parentId: null, leaf: true },
                { id: 'role2', code: 'R002', name: '普通用户', parentId: null, leaf: true }
            ],
            expression: [
                { id: 'expr1', code: 'EXPR001', name: '部门经理审批', parentId: null, leaf: true, description: '自动选择部门经理' },
                { id: 'expr2', code: 'EXPR002', name: '直属领导审批', parentId: null, leaf: true, description: '自动选择直属领导' }
            ]
        };
        
        const data = mocks[type] || [];
        
        if (parentId) {
            return data.filter(item => item.parentId === parentId);
        }
        
        return data.filter(item => !item.parentId);
    }

    /**
     * 规范化数据
     */
    _normalizeData(data) {
        if (!Array.isArray(data)) {
            data = [data];
        }
        
        const mapping = this.options.fieldMapping;
        
        return data.map(item => ({
            id: item[mapping.id] || item.id,
            code: item[mapping.code] || item.code || item.id,
            name: item[mapping.name] || item.name || item.id,
            parentId: item[mapping.parentId] || item.parentId,
            children: item[mapping.children] || item.children,
            leaf: item[mapping.leaf] !== undefined ? item[mapping.leaf] : !item.children,
            icon: item[mapping.icon] || item.icon,
            ...item  // 保留原始数据
        }));
    }

    /**
     * 渲染字典视图
     */
    _renderDictionary() {
        if (this.options.display.showTree) {
            this._renderTree();
        } else {
            this._renderTable();
        }
    }

    /**
     * 渲染树形结构
     */
    _renderTree() {
        if (!this.treeElement) return;
        
        this.treeElement.innerHTML = '';
        
        const renderNode = (item, level = 0) => {
            const node = document.createElement('div');
            node.className = 'tree-node';
            node.dataset.id = item.id;
            node.style.paddingLeft = (level * 20) + 'px';
            
            const isSelected = this.selectedItems.some(s => s.id === item.id);
            if (isSelected) {
                node.classList.add('selected');
            }
            
            const expander = document.createElement('span');
            expander.className = 'tree-expander';
            if (!item.leaf) {
                expander.innerHTML = '<i class="icon-chevron-right"></i>';
                expander.onclick = (e) => {
                    e.stopPropagation();
                    this._toggleNode(node, item);
                };
            }
            node.appendChild(expander);
            
            const icon = document.createElement('span');
            icon.className = 'tree-icon';
            icon.innerHTML = `<i class="icon-${item.icon || (item.leaf ? 'file' : 'folder')}"></i>`;
            node.appendChild(icon);
            
            const label = document.createElement('span');
            label.className = 'tree-label';
            label.textContent = item.name;
            node.appendChild(label);
            
            // 选择框（多选模式）
            if (this.options.selection.mode === 'multiple') {
                const checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                checkbox.className = 'tree-checkbox';
                checkbox.checked = isSelected;
                checkbox.onchange = (e) => {
                    e.stopPropagation();
                    this._toggleSelection(item, e.target.checked);
                };
                node.appendChild(checkbox);
            }
            
            node.onclick = () => {
                if (this.options.selection.mode === 'single') {
                    this._toggleSelection(item, !isSelected);
                }
            };
            
            return node;
        };
        
        const renderChildren = (items, level = 0) => {
            const container = document.createElement('div');
            container.className = 'tree-level';
            
            items.forEach(item => {
                const node = renderNode(item, level);
                container.appendChild(node);
                
                // 子节点容器
                if (!item.leaf) {
                    const childrenContainer = document.createElement('div');
                    childrenContainer.className = 'tree-children';
                    childrenContainer.style.display = 'none';
                    childrenContainer.dataset.parentId = item.id;
                    container.appendChild(childrenContainer);
                }
            });
            
            return container;
        };
        
        this.treeElement.appendChild(renderChildren(this.data));
    }

    /**
     * 渲染表格
     */
    _renderTable() {
        if (!this.tableBody) return;
        
        this.tableBody.innerHTML = '';
        
        this.data.forEach(item => {
            const row = document.createElement('tr');
            row.dataset.id = item.id;
            
            const isSelected = this.selectedItems.some(s => s.id === item.id);
            if (isSelected) {
                row.classList.add('selected');
            }
            
            // 选择列
            const selectTd = document.createElement('td');
            selectTd.className = 'col-select';
            if (this.options.selection.mode === 'multiple') {
                selectTd.innerHTML = `<input type="checkbox" ${isSelected ? 'checked' : ''}>`;
            } else {
                selectTd.innerHTML = `<input type="radio" name="dictionary-select" ${isSelected ? 'checked' : ''}>`;
            }
            row.appendChild(selectTd);
            
            // 数据列
            this.options.display.columns.forEach(col => {
                const td = document.createElement('td');
                let value = item[col.field];
                if (col.formatter) {
                    td.innerHTML = col.formatter(value, item);
                } else {
                    td.textContent = value !== undefined ? value : '';
                }
                row.appendChild(td);
            });
            
            // 点击行选择
            row.onclick = (e) => {
                if (e.target.tagName === 'INPUT') return;
                
                const checkbox = row.querySelector('input');
                if (this.options.selection.mode === 'multiple') {
                    checkbox.checked = !checkbox.checked;
                    this._toggleSelection(item, checkbox.checked);
                } else {
                    this._toggleSelection(item, true);
                }
            };
            
            this.tableBody.appendChild(row);
        });
    }

    /**
     * 切换节点展开/折叠
     */
    async _toggleNode(nodeElement, item) {
        const childrenContainer = nodeElement.nextElementSibling;
        const expander = nodeElement.querySelector('.tree-expander');
        
        if (childrenContainer.style.display === 'none') {
            // 展开
            if (childrenContainer.children.length === 0 && !item.leaf) {
                // 懒加载子节点
                await this.loadData(item.id);
                const children = this.data.filter(d => d.parentId === item.id);
                children.forEach(child => {
                    const childNode = this._createTreeNode(child, 1);
                    childrenContainer.appendChild(childNode);
                });
            }
            
            childrenContainer.style.display = 'block';
            expander.innerHTML = '<i class="icon-chevron-down"></i>';
            nodeElement.classList.add('expanded');
        } else {
            // 折叠
            childrenContainer.style.display = 'none';
            expander.innerHTML = '<i class="icon-chevron-right"></i>';
            nodeElement.classList.remove('expanded');
        }
    }

    /**
     * 切换选择状态
     */
    _toggleSelection(item, selected) {
        if (selected) {
            if (this.options.selection.mode === 'single') {
                // 单选模式，清除其他选择
                this.selectedItems = [item];
            } else {
                // 多选模式
                const exists = this.selectedItems.some(s => s.id === item.id);
                if (!exists) {
                    // 检查最大数量限制
                    if (this.options.selection.maxCount > 0 && 
                        this.selectedItems.length >= this.options.selection.maxCount) {
                        alert(`最多只能选择 ${this.options.selection.maxCount} 项`);
                        return;
                    }
                    this.selectedItems.push(item);
                }
            }
        } else {
            this.selectedItems = this.selectedItems.filter(s => s.id !== item.id);
        }
        
        this._updateSelectionUI();
        
        if (this.options.onSelect) {
            this.options.onSelect(this.selectedItems, item, selected);
        }
    }

    /**
     * 更新选择UI
     */
    _updateSelectionUI() {
        // 更新计数
        const countEl = this.container.querySelector('.selection-count .count');
        if (countEl) {
            countEl.textContent = this.selectedItems.length;
        }
        
        // 更新字典视图中的选择状态
        if (this.options.display.showTree) {
            const nodes = this.treeElement.querySelectorAll('.tree-node');
            nodes.forEach(node => {
                const id = node.dataset.id;
                const isSelected = this.selectedItems.some(s => s.id === id);
                node.classList.toggle('selected', isSelected);
                
                const checkbox = node.querySelector('.tree-checkbox');
                if (checkbox) {
                    checkbox.checked = isSelected;
                }
            });
        } else {
            const rows = this.tableBody.querySelectorAll('tr');
            rows.forEach(row => {
                const id = row.dataset.id;
                const isSelected = this.selectedItems.some(s => s.id === id);
                row.classList.toggle('selected', isSelected);
                
                const input = row.querySelector('input');
                if (input) {
                    input.checked = isSelected;
                }
            });
        }
        
        // 更新已选项列表
        this._renderSelectionList();
    }

    /**
     * 渲染已选项列表
     */
    _renderSelectionList() {
        if (!this.selectionList) return;
        
        this.selectionList.innerHTML = '';
        
        this.selectedItems.forEach((item, index) => {
            const chip = document.createElement('div');
            chip.className = 'selection-chip';
            chip.innerHTML = `
                <span class="chip-text">${item.name}</span>
                <button class="btn-remove" title="移除">&times;</button>
            `;
            
            chip.querySelector('.btn-remove').onclick = () => {
                this._toggleSelection(item, false);
            };
            
            this.selectionList.appendChild(chip);
        });
        
        // 空提示
        if (this.selectedItems.length === 0) {
            this.selectionList.innerHTML = '<div class="empty-tip">未选择任何项</div>';
        }
    }

    /**
     * 处理搜索
     */
    _handleSearch() {
        if (!this.searchKeyword) {
            this.filteredData = this.data;
        } else {
            const keyword = this.searchKeyword.toLowerCase();
            this.filteredData = this.data.filter(item => {
                return item.name.toLowerCase().includes(keyword) ||
                       item.code.toLowerCase().includes(keyword);
            });
        }
        
        this._renderDictionary();
    }

    /**
     * 处理确认
     */
    _handleConfirm() {
        // 验证最小选择数量
        if (this.options.selection.minCount > 0 && 
            this.selectedItems.length < this.options.selection.minCount) {
            alert(`至少需要选择 ${this.options.selection.minCount} 项`);
            return;
        }
        
        // 验证非空
        if (!this.options.selection.allowEmpty && this.selectedItems.length === 0) {
            alert('请至少选择一项');
            return;
        }
        
        if (this.options.onConfirm) {
            this.options.onConfirm(this.selectedItems);
        }
    }

    /**
     * 清空选择
     */
    clearSelection() {
        this.selectedItems = [];
        this._updateSelectionUI();
    }

    /**
     * 设置已选项
     */
    setSelection(items) {
        this.selectedItems = Array.isArray(items) ? [...items] : [];
        this._updateSelectionUI();
        return this;
    }

    /**
     * 获取已选项
     */
    getSelection() {
        return [...this.selectedItems];
    }

    /**
     * 显示/隐藏加载状态
     */
    _showLoading(show) {
        if (this.loadingElement) {
            this.loadingElement.style.display = show ? 'flex' : 'none';
        }
    }

    /**
     * 获取缓存键
     */
    _getCacheKey(parentId) {
        return `${this.options.dictionary.cacheKey}_${parentId || 'root'}`;
    }

    /**
     * 销毁面板
     */
    destroy() {
        if (this.container) {
            this.container.innerHTML = '';
        }
        this.data = [];
        this.selectedItems = [];
        this.cache.clear();
    }
}

// 导出
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ExternalDictionaryPanel;
}

/**
 * ExternalDictionaryPlugins - 外部字典面板插件集合
 * 
 * 包含：
 * - ExpressionPanelPlugin: 表达式编辑器
 * - PerformerSelectionPanelPlugin: 办理人选择器
 * - DepartmentSelectionPanelPlugin: 部门选择器
 */
const ExternalDictionaryPlugins = {
    ExpressionPanelPlugin: {
        name: '表达式编辑器',
        icon: 'formula',
        render(container, options = {}) {
            const panel = new ExternalDictionaryPanel({
                title: '表达式管理',
                icon: 'formula',
                dictionary: {
                    type: 'expression',
                    lazyLoad: false
                },
                selection: {
                    mode: 'single',
                    allowEmpty: true
                },
                display: {
                    showTree: false,
                    showSearch: true,
                    columns: [
                        { field: 'name', title: '名称', width: '150px' },
                        { field: 'expression', title: '表达式', width: '200px' },
                        { field: 'description', title: '说明' }
                    ]
                },
                onConfirm: (items) => {
                    if (options.onConfirm && items.length > 0) {
                        options.onConfirm(items[0]);
                    }
                },
                onCancel: options.onCancel
            });
            panel.render(container);
            return panel;
        }
    },

    PerformerSelectionPanelPlugin: {
        name: '办理人选择器',
        icon: 'person',
        render(container, options = {}) {
            const panel = new ExternalDictionaryPanel({
                title: '选择办理人',
                icon: 'person',
                dictionary: {
                    type: 'person',
                    lazyLoad: false
                },
                selection: {
                    mode: options.multiSelect ? 'multiple' : 'single',
                    maxCount: options.maxCount || 0,
                    allowEmpty: false
                },
                display: {
                    showTree: true,
                    showSearch: true
                },
                fieldMapping: {
                    id: 'id',
                    code: 'code',
                    name: 'name',
                    parentId: 'orgId'
                },
                onConfirm: (items) => {
                    if (options.onConfirm) {
                        options.onConfirm(items);
                    }
                },
                onCancel: options.onCancel
            });
            panel.render(container);
            return panel;
        }
    },

    DepartmentSelectionPanelPlugin: {
        name: '部门选择器',
        icon: 'folder',
        render(container, options = {}) {
            const panel = new ExternalDictionaryPanel({
                title: '选择部门',
                icon: 'folder',
                dictionary: {
                    type: 'department',
                    lazyLoad: true
                },
                selection: {
                    mode: options.multiSelect ? 'multiple' : 'single',
                    maxCount: options.maxCount || 0,
                    allowEmpty: false
                },
                display: {
                    showTree: true,
                    showSearch: true
                },
                onConfirm: (items) => {
                    if (options.onConfirm) {
                        options.onConfirm(items);
                    }
                },
                onCancel: options.onCancel
            });
            panel.render(container);
            return panel;
        }
    },

    RoleSelectionPanelPlugin: {
        name: '角色选择器',
        icon: 'role',
        render(container, options = {}) {
            const panel = new ExternalDictionaryPanel({
                title: '选择角色',
                icon: 'role',
                dictionary: {
                    type: 'role',
                    lazyLoad: false
                },
                selection: {
                    mode: options.multiSelect ? 'multiple' : 'single',
                    maxCount: options.maxCount || 0,
                    allowEmpty: false
                },
                display: {
                    showTree: false,
                    showSearch: true,
                    columns: [
                        { field: 'name', title: '角色名称', width: '150px' },
                        { field: 'code', title: '角色编码', width: '100px' },
                        { field: 'description', title: '描述' }
                    ]
                },
                onConfirm: (items) => {
                    if (options.onConfirm) {
                        options.onConfirm(items);
                    }
                },
                onCancel: options.onCancel
            });
            panel.render(container);
            return panel;
        }
    }
};

window.ExternalDictionaryPlugins = ExternalDictionaryPlugins;
