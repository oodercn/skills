/**
 * 审批表单系统
 * Approval Form System
 */
const ApprovalForm = {
    // 数据存储
    pendingList: [],
    submittedList: [],
    templates: [],
    records: [],
    statistics: {},
    currentApproval: null,
    currentAction: null,

    // 审批类型映射
    typeMap: {
        leave: { name: '请假申请', icon: 'ri-calendar-line', color: '#3b82f6' },
        expense: { name: '费用报销', icon: 'ri-money-cny-circle-line', color: '#22c55e' },
        purchase: { name: '采购申请', icon: 'ri-shopping-cart-line', color: '#f59e0b' },
        contract: { name: '合同审批', icon: 'ri-file-text-line', color: '#8b5cf6' },
        overtime: { name: '加班申请', icon: 'ri-time-line', color: '#ef4444' },
        business: { name: '出差申请', icon: 'ri-plane-line', color: '#06b6d4' }
    },

    // 优先级映射
    priorityMap: {
        normal: { name: '普通', class: 'normal' },
        urgent: { name: '紧急', class: 'urgent' },
        high: { name: '特急', class: 'high' }
    },

    // 状态映射
    statusMap: {
        pending: { name: '审批中', class: 'processing' },
        approved: { name: '已通过', class: 'approved' },
        rejected: { name: '已拒绝', class: 'rejected' }
    },

    /**
     * 初始化
     */
    init() {
        this.loadMockData();
        this.bindEvents();
        this.loadStatistics();
        this.loadPendingList();
        this.loadTemplates();
        this.renderTemplates();
        // 初始化菜单徽章
        this.updateMenuBadges();
    },

    /**
     * 加载模拟数据
     */
    loadMockData() {
        // 待办审批数据
        this.pendingList = [
            {
                id: 'APP-20250306-001',
                type: 'leave',
                applicant: '张三',
                applyTime: '2026-03-06 09:30:00',
                currentNode: '部门经理审批',
                priority: 'normal',
                status: 'pending',
                detail: {
                    leaveType: 'annual',
                    days: 3,
                    startTime: '2026-03-10 09:00',
                    endTime: '2026-03-12 18:00',
                    reason: '因个人原因需要请假处理家庭事务'
                },
                timeline: [
                    { node: '提交申请', operator: '张三', time: '2026-03-06 09:30:00', status: 'approved', comment: '' },
                    { node: '部门经理审批', operator: '李经理', time: '', status: 'pending', comment: '' }
                ]
            },
            {
                id: 'APP-20250306-002',
                type: 'expense',
                applicant: '李四',
                applyTime: '2026-03-06 10:15:00',
                currentNode: '财务审批',
                priority: 'urgent',
                status: 'pending',
                detail: {
                    amount: 2580.50,
                    category: 'travel',
                    detail: '出差上海参加技术交流会，包含机票、酒店、餐饮费用',
                    attachments: ['发票1.pdf', '发票2.pdf']
                },
                timeline: [
                    { node: '提交申请', operator: '李四', time: '2026-03-06 10:15:00', status: 'approved', comment: '' },
                    { node: '部门经理审批', operator: '王经理', time: '2026-03-06 11:00:00', status: 'approved', comment: '同意报销' },
                    { node: '财务审批', operator: '赵会计', time: '', status: 'pending', comment: '' }
                ]
            },
            {
                id: 'APP-20250306-003',
                type: 'purchase',
                applicant: '王五',
                applyTime: '2026-03-06 11:30:00',
                currentNode: '部门经理审批',
                priority: 'normal',
                status: 'pending',
                detail: {
                    item: '办公电脑',
                    quantity: 2,
                    unitPrice: 8000,
                    totalPrice: 16000,
                    reason: '新员工入职需要配置开发环境'
                },
                timeline: [
                    { node: '提交申请', operator: '王五', time: '2026-03-06 11:30:00', status: 'approved', comment: '' },
                    { node: '部门经理审批', operator: '李经理', time: '', status: 'pending', comment: '' }
                ]
            }
        ];

        // 我发起的审批数据
        this.submittedList = [
            {
                id: 'APP-20250305-001',
                type: 'leave',
                applyTime: '2026-03-05 14:20:00',
                status: 'approved',
                currentApprover: '-',
                detail: { leaveType: 'sick', days: 1, reason: '身体不适' }
            },
            {
                id: 'APP-20250304-002',
                type: 'expense',
                applyTime: '2026-03-04 16:00:00',
                status: 'rejected',
                currentApprover: '李经理',
                detail: { amount: 5000, category: 'entertainment', reason: '客户招待' }
            },
            {
                id: 'APP-20250303-003',
                type: 'contract',
                applyTime: '2026-03-03 10:00:00',
                status: 'pending',
                currentApprover: '张总监',
                detail: { contractName: '技术服务合同', amount: 500000 }
            }
        ];

        // 审批模板数据
        this.templates = [
            { id: 'tpl-leave', type: 'leave', name: '请假申请', desc: '适用于员工请假申请，支持年假、病假、事假等多种类型', icon: 'ri-calendar-line', color: '#3b82f6' },
            { id: 'tpl-expense', type: 'expense', name: '费用报销', desc: '适用于差旅费、招待费、办公费等各类费用报销', icon: 'ri-money-cny-circle-line', color: '#22c55e' },
            { id: 'tpl-purchase', type: 'purchase', name: '采购申请', desc: '适用于办公用品、设备、原材料等采购申请', icon: 'ri-shopping-cart-line', color: '#f59e0b' },
            { id: 'tpl-contract', type: 'contract', name: '合同审批', desc: '适用于各类业务合同的审批流程', icon: 'ri-file-text-line', color: '#8b5cf6' },
            { id: 'tpl-overtime', type: 'overtime', name: '加班申请', desc: '适用于员工加班申请及调休管理', icon: 'ri-time-line', color: '#ef4444' },
            { id: 'tpl-business', type: 'business', name: '出差申请', desc: '适用于员工出差申请及差旅安排', icon: 'ri-plane-line', color: '#06b6d4' }
        ];

        // 审批记录数据
        this.records = [
            { id: 'REC-001', approvalId: 'APP-20250305-001', type: 'leave', applicant: '张三', approver: '李经理', action: 'approved', time: '2026-03-05 15:00:00', comment: '同意请假' },
            { id: 'REC-002', approvalId: 'APP-20250304-002', type: 'expense', applicant: '李四', approver: '李经理', action: 'rejected', time: '2026-03-04 17:00:00', comment: '费用超标，请重新申请' },
            { id: 'REC-003', approvalId: 'APP-20250303-003', type: 'contract', applicant: '王五', approver: '张总监', action: 'approved', time: '2026-03-03 14:00:00', comment: '合同条款无误，同意签署' }
        ];

        // 统计数据
        this.statistics = {
            pending: 5,
            submitted: 12,
            approved: 28,
            rejected: 3,
            trend: {
                labels: ['周一', '周二', '周三', '周四', '周五'],
                approved: [12, 15, 8, 18, 20],
                rejected: [2, 1, 3, 1, 0]
            },
            typeDistribution: [
                { type: 'leave', count: 15, percentage: 35 },
                { type: 'expense', count: 12, percentage: 28 },
                { type: 'purchase', count: 8, percentage: 18 },
                { type: 'contract', count: 5, percentage: 12 },
                { type: 'other', count: 3, percentage: 7 }
            ],
            efficiency: [
                { name: '李经理', dept: '技术部', avgTime: '2.5小时' },
                { name: '王经理', dept: '产品部', avgTime: '3.2小时' },
                { name: '张总监', dept: '运营部', avgTime: '4.1小时' },
                { name: '赵会计', dept: '财务部', avgTime: '5.0小时' }
            ]
        };
    },

    /**
     * 绑定事件
     */
    bindEvents() {
        // 文件上传
        const fileUpload = document.getElementById('file-upload');
        const fileInput = document.getElementById('approval-attachments');
        if (fileUpload && fileInput) {
            fileUpload.addEventListener('click', () => fileInput.click());
            fileUpload.addEventListener('dragover', (e) => {
                e.preventDefault();
                fileUpload.style.borderColor = '#3b82f6';
            });
            fileUpload.addEventListener('dragleave', () => {
                fileUpload.style.borderColor = '';
            });
            fileUpload.addEventListener('drop', (e) => {
                e.preventDefault();
                fileUpload.style.borderColor = '';
                this.handleFiles(e.dataTransfer.files);
            });
            fileInput.addEventListener('change', (e) => {
                this.handleFiles(e.target.files);
            });
        }

        // 采购数量和价格自动计算总价
        const purchaseQty = document.getElementById('purchase-quantity');
        const purchasePrice = document.getElementById('purchase-price');
        const purchaseTotal = document.getElementById('purchase-total');
        if (purchaseQty && purchasePrice && purchaseTotal) {
            const calcTotal = () => {
                const qty = parseFloat(purchaseQty.value) || 0;
                const price = parseFloat(purchasePrice.value) || 0;
                purchaseTotal.value = '¥' + (qty * price).toFixed(2);
            };
            purchaseQty.addEventListener('input', calcTotal);
            purchasePrice.addEventListener('input', calcTotal);
        }
    },

    /**
     * 处理文件上传
     */
    handleFiles(files) {
        const fileList = document.getElementById('file-list');
        if (!fileList) return;

        Array.from(files).forEach(file => {
            const fileItem = document.createElement('div');
            fileItem.className = 'file-item';
            fileItem.innerHTML = `
                <i class="ri-file-line"></i>
                <span>${file.name}</span>
                <button type="button" onclick="this.parentElement.remove()">
                    <i class="ri-close-line"></i>
                </button>
            `;
            fileList.appendChild(fileItem);
        });
    },

    /**
     * 加载统计数据
     */
    loadStatistics() {
        document.getElementById('stat-pending').textContent = this.statistics.pending;
        document.getElementById('stat-submitted').textContent = this.statistics.submitted;
        document.getElementById('stat-approved').textContent = this.statistics.approved;
        document.getElementById('stat-rejected').textContent = this.statistics.rejected;
    },

    /**
     * 加载待办列表
     */
    loadPendingList() {
        const tbody = document.getElementById('pending-table-body');
        if (!tbody) return;

        if (this.pendingList.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="7" class="nx-text-center nx-text-secondary">
                        <i class="ri-inbox-line" style="font-size: 48px; display: block; margin-bottom: 16px;"></i>
                        暂无待办审批
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = this.pendingList.map(item => {
            const typeInfo = this.typeMap[item.type];
            const priorityInfo = this.priorityMap[item.priority];
            return `
                <tr>
                    <td><strong>${item.id}</strong></td>
                    <td>
                        <i class="${typeInfo.icon}" style="color: ${typeInfo.color};"></i>
                        ${typeInfo.name}
                    </td>
                    <td>${item.applicant}</td>
                    <td>${item.applyTime}</td>
                    <td>${item.currentNode}</td>
                    <td><span class="priority-badge ${priorityInfo.class}">${priorityInfo.name}</span></td>
                    <td>
                        <button class="nx-btn nx-btn--sm nx-btn--primary" onclick="ApprovalForm.viewDetail('${item.id}')">
                            <i class="ri-eye-line"></i> 查看
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
    },

    /**
     * 加载已发起列表
     */
    loadSubmittedList() {
        const tbody = document.getElementById('submitted-table-body');
        if (!tbody) return;

        if (this.submittedList.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="nx-text-center nx-text-secondary">
                        <i class="ri-inbox-line" style="font-size: 48px; display: block; margin-bottom: 16px;"></i>
                        暂无已发起审批
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = this.submittedList.map(item => {
            const typeInfo = this.typeMap[item.type];
            const statusInfo = this.statusMap[item.status];
            return `
                <tr>
                    <td><strong>${item.id}</strong></td>
                    <td>
                        <i class="${typeInfo.icon}" style="color: ${typeInfo.color};"></i>
                        ${typeInfo.name}
                    </td>
                    <td>${item.applyTime}</td>
                    <td><span class="status-badge ${statusInfo.class}">${statusInfo.name}</span></td>
                    <td>${item.currentApprover}</td>
                    <td>
                        <button class="nx-btn nx-btn--sm nx-btn--secondary" onclick="ApprovalForm.viewDetail('${item.id}')">
                            <i class="ri-eye-line"></i> 查看
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
    },

    /**
     * 加载模板
     */
    loadTemplates() {
        this.renderTemplates();
    },

    /**
     * 渲染模板
     */
    renderTemplates() {
        const container = document.getElementById('template-grid');
        if (!container) return;

        container.innerHTML = this.templates.map(tpl => `
            <div class="template-card" onclick="ApprovalForm.useTemplate('${tpl.type}')">
                <div class="template-card__icon" style="background: ${tpl.color}20; color: ${tpl.color};">
                    <i class="${tpl.icon}"></i>
                </div>
                <div class="template-card__title">${tpl.name}</div>
                <div class="template-card__desc">${tpl.desc}</div>
            </div>
        `).join('');
    },

    /**
     * 加载审批记录
     */
    loadRecords() {
        const tbody = document.getElementById('records-table-body');
        if (!tbody) return;

        if (this.records.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="7" class="nx-text-center nx-text-secondary">
                        <i class="ri-inbox-line" style="font-size: 48px; display: block; margin-bottom: 16px;"></i>
                        暂无审批记录
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = this.records.map(record => {
            const typeInfo = this.typeMap[record.type];
            const actionText = record.action === 'approved' ? '通过' : '拒绝';
            const actionClass = record.action === 'approved' ? 'success' : 'danger';
            return `
                <tr>
                    <td>${record.approvalId}</td>
                    <td>${typeInfo.name}</td>
                    <td>${record.applicant}</td>
                    <td>${record.approver}</td>
                    <td><span class="nx-text-${actionClass}">${actionText}</span></td>
                    <td>${record.time}</td>
                    <td>${record.comment}</td>
                </tr>
            `;
        }).join('');
    },

    /**
     * 渲染类型分布
     */
    renderTypeDistribution() {
        const container = document.getElementById('type-distribution');
        if (!container) return;

        const colors = ['#3b82f6', '#22c55e', '#f59e0b', '#8b5cf6', '#64748b'];
        container.innerHTML = this.statistics.typeDistribution.map((item, index) => {
            const typeInfo = this.typeMap[item.type] || { name: '其他' };
            return `
                <div class="type-item">
                    <span class="type-label">${typeInfo.name}</span>
                    <div class="type-bar">
                        <div class="type-fill" style="width: ${item.percentage}%; background: ${colors[index % colors.length]};"></div>
                    </div>
                    <span class="type-value">${item.count}</span>
                </div>
            `;
        }).join('');
    },

    /**
     * 渲染效率排行
     */
    renderEfficiencyList() {
        const container = document.getElementById('efficiency-list');
        if (!container) return;

        container.innerHTML = this.statistics.efficiency.map((item, index) => `
            <div class="efficiency-item">
                <div class="efficiency-rank ${index < 3 ? 'top3' : 'normal'}">${index + 1}</div>
                <div class="efficiency-info">
                    <div class="efficiency-name">${item.name}</div>
                    <div class="efficiency-dept">${item.dept}</div>
                </div>
                <div class="efficiency-time">平均 ${item.avgTime}</div>
            </div>
        `).join('');
    },

    /**
     * 切换标签页
     */
    switchTab(tab) {
        // 更新顶部标签按钮状态
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        document.querySelector(`.tab-btn[data-tab="${tab}"]`)?.classList.add('active');
        
        // 更新左侧菜单激活状态
        document.querySelectorAll('.sidebar-menu__item').forEach(item => {
            item.classList.remove('active');
        });
        // 根据tab找到对应的菜单项并激活
        const menuItems = document.querySelectorAll('.sidebar-menu__item');
        menuItems.forEach(item => {
            const onclickAttr = item.getAttribute('onclick');
            if (onclickAttr && (onclickAttr.includes(`switchTab('${tab}')`) || onclickAttr.includes(`switchTab("${tab}")`))) {
                item.classList.add('active');
            }
        });

        // 更新内容显示
        document.querySelectorAll('.tab-content').forEach(content => {
            content.classList.remove('active');
        });
        document.getElementById(`tab-${tab}`)?.classList.add('active');

        // 加载对应数据
        switch (tab) {
            case 'pending':
                this.loadPendingList();
                break;
            case 'submitted':
                this.loadSubmittedList();
                break;
            case 'records':
                this.loadRecords();
                break;
            case 'dashboard':
                this.renderTypeDistribution();
                this.renderEfficiencyList();
                break;
        }
    },

    /**
     * 筛选待办
     */
    filterPending() {
        const type = document.getElementById('pending-filter-type')?.value;
        // 实际应用中这里会调用API进行筛选
        console.log('筛选待办:', type);
        this.loadPendingList();
    },

    /**
     * 筛选已发起
     */
    filterSubmitted() {
        const status = document.getElementById('submitted-filter-status')?.value;
        // 实际应用中这里会调用API进行筛选
        console.log('筛选已发起:', status);
        this.loadSubmittedList();
    },

    /**
     * 筛选记录
     */
    filterRecords() {
        const startDate = document.getElementById('record-start-date')?.value;
        const endDate = document.getElementById('record-end-date')?.value;
        // 实际应用中这里会调用API进行筛选
        console.log('筛选记录:', startDate, endDate);
        this.loadRecords();
    },

    /**
     * 打开创建模态框
     */
    openCreateModal() {
        document.getElementById('create-modal')?.classList.add('modal--open');
        this.resetForm();
    },

    /**
     * 关闭创建模态框
     */
    closeCreateModal() {
        document.getElementById('create-modal')?.classList.remove('modal--open');
    },

    /**
     * 打开模板模态框
     */
    openTemplateModal() {
        // 可以扩展为模板管理模态框
        console.log('打开模板管理');
    },

    /**
     * 使用模板
     */
    useTemplate(type) {
        this.openCreateModal();
        const typeSelect = document.getElementById('approval-type');
        if (typeSelect) {
            typeSelect.value = type;
            this.onTypeChange();
        }
    },

    /**
     * 审批类型改变
     */
    onTypeChange() {
        const type = document.getElementById('approval-type')?.value;
        // 隐藏所有表单区块
        document.querySelectorAll('.form-section').forEach(section => {
            section.style.display = 'none';
        });
        // 显示对应类型的表单
        if (type) {
            const section = document.getElementById(`section-${type}`);
            if (section) {
                section.style.display = 'block';
            }
        }
    },

    /**
     * 重置表单
     */
    resetForm() {
        document.getElementById('approval-form')?.reset();
        document.querySelectorAll('.form-section').forEach(section => {
            section.style.display = 'none';
        });
        document.getElementById('file-list').innerHTML = '';
    },

    /**
     * 提交审批
     */
    submitApproval() {
        const type = document.getElementById('approval-type')?.value;
        const priority = document.getElementById('approval-priority')?.value;
        const reason = document.getElementById('approval-reason')?.value;
        const approver = document.getElementById('approval-approver')?.value;

        if (!type) {
            alert('请选择审批类型');
            return;
        }
        if (!reason) {
            alert('请填写审批事由');
            return;
        }
        if (!approver) {
            alert('请选择审批人');
            return;
        }

        // 构建审批数据
        const approvalData = {
            id: 'APP-' + new Date().toISOString().slice(0, 10).replace(/-/g, '') + '-' + String(Math.random()).slice(2, 5),
            type,
            priority,
            reason,
            approver,
            applicant: '当前用户',
            applyTime: new Date().toLocaleString('zh-CN'),
            status: 'pending',
            currentNode: '待审批'
        };

        // 根据类型添加特定字段
        if (type === 'leave') {
            approvalData.detail = {
                leaveType: document.getElementById('leave-type')?.value,
                days: document.getElementById('leave-days')?.value,
                startTime: document.getElementById('leave-start')?.value,
                endTime: document.getElementById('leave-end')?.value
            };
        } else if (type === 'expense') {
            approvalData.detail = {
                amount: document.getElementById('expense-amount')?.value,
                category: document.getElementById('expense-category')?.value,
                detail: document.getElementById('expense-detail')?.value
            };
        } else if (type === 'purchase') {
            approvalData.detail = {
                item: document.getElementById('purchase-item')?.value,
                quantity: document.getElementById('purchase-quantity')?.value,
                unitPrice: document.getElementById('purchase-price')?.value,
                totalPrice: document.getElementById('purchase-total')?.value
            };
        }

        // 添加到已发起列表
        this.submittedList.unshift(approvalData);
        this.statistics.submitted++;
        this.loadStatistics();

        // 关闭模态框
        this.closeCreateModal();

        // 显示成功提示
        alert('审批提交成功！');

        // 刷新列表
        this.loadSubmittedList();
    },

    /**
     * 查看详情
     */
    viewDetail(id) {
        // 从待办或已发起列表中查找
        let approval = this.pendingList.find(item => item.id === id);
        if (!approval) {
            approval = this.submittedList.find(item => item.id === id);
        }
        if (!approval) return;

        this.currentApproval = approval;

        // 渲染时间线
        this.renderTimeline(approval.timeline);

        // 渲染详情
        this.renderDetail(approval);

        // 显示/隐藏审批按钮
        const footer = document.getElementById('detail-modal-footer');
        if (footer) {
            if (approval.status === 'pending' && this.pendingList.includes(approval)) {
                footer.style.display = 'flex';
            } else {
                footer.style.display = 'none';
            }
        }

        // 打开模态框
        document.getElementById('detail-modal')?.classList.add('modal--open');
    },

    /**
     * 渲染时间线
     */
    renderTimeline(timeline) {
        const container = document.getElementById('approval-timeline');
        if (!container || !timeline) return;

        container.innerHTML = timeline.map((item, index) => {
            const statusClass = item.status === 'approved' ? 'approved' : 
                               item.status === 'rejected' ? 'rejected' : 'pending';
            const icon = item.status === 'approved' ? 'ri-check-line' : 
                        item.status === 'rejected' ? 'ri-close-line' : 'ri-time-line';
            return `
                <div class="timeline-item">
                    <div class="timeline-dot ${statusClass}">
                        <i class="${icon}"></i>
                    </div>
                    <div class="timeline-content">
                        <div class="timeline-title">${item.node}</div>
                        <div class="timeline-desc">${item.operator}${item.comment ? '：' + item.comment : ''}</div>
                    </div>
                    <div class="timeline-time">${item.time || '待处理'}</div>
                </div>
            `;
        }).join('');
    },

    /**
     * 渲染详情
     */
    renderDetail(approval) {
        const container = document.getElementById('approval-detail-content');
        if (!container) return;

        const typeInfo = this.typeMap[approval.type];
        let detailHtml = `
            <div class="detail-section">
                <div class="detail-section__title">基本信息</div>
                <div class="detail-row">
                    <span class="detail-label">审批单号</span>
                    <span class="detail-value">${approval.id}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">审批类型</span>
                    <span class="detail-value">
                        <i class="${typeInfo.icon}" style="color: ${typeInfo.color};"></i>
                        ${typeInfo.name}
                    </span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">申请人</span>
                    <span class="detail-value">${approval.applicant}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">申请时间</span>
                    <span class="detail-value">${approval.applyTime}</span>
                </div>
            </div>
        `;

        // 根据类型添加特定详情
        if (approval.detail) {
            detailHtml += `<div class="detail-section"><div class="detail-section__title">详细信息</div>`;
            if (approval.type === 'leave') {
                const leaveTypeMap = { annual: '年假', sick: '病假', personal: '事假', marriage: '婚假', maternity: '产假' };
                detailHtml += `
                    <div class="detail-row">
                        <span class="detail-label">请假类型</span>
                        <span class="detail-value">${leaveTypeMap[approval.detail.leaveType] || approval.detail.leaveType}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">请假天数</span>
                        <span class="detail-value">${approval.detail.days} 天</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">开始时间</span>
                        <span class="detail-value">${approval.detail.startTime}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">结束时间</span>
                        <span class="detail-value">${approval.detail.endTime}</span>
                    </div>
                `;
            } else if (approval.type === 'expense') {
                const categoryMap = { travel: '差旅费', entertainment: '招待费', office: '办公费', transport: '交通费', other: '其他' };
                detailHtml += `
                    <div class="detail-row">
                        <span class="detail-label">报销金额</span>
                        <span class="detail-value">¥${approval.detail.amount}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">费用类型</span>
                        <span class="detail-value">${categoryMap[approval.detail.category] || approval.detail.category}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">费用明细</span>
                        <span class="detail-value">${approval.detail.detail || '-'}</span>
                    </div>
                `;
            } else if (approval.type === 'purchase') {
                detailHtml += `
                    <div class="detail-row">
                        <span class="detail-label">采购物品</span>
                        <span class="detail-value">${approval.detail.item}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">采购数量</span>
                        <span class="detail-value">${approval.detail.quantity}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">预估单价</span>
                        <span class="detail-value">¥${approval.detail.unitPrice}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">预估总价</span>
                        <span class="detail-value">${approval.detail.totalPrice}</span>
                    </div>
                `;
            }
            detailHtml += '</div>';
        }

        // 审批事由
        detailHtml += `
            <div class="detail-section">
                <div class="detail-section__title">审批事由</div>
                <div class="detail-row">
                    <span class="detail-value">${approval.reason || approval.detail?.reason || '-'}</span>
                </div>
            </div>
        `;

        container.innerHTML = detailHtml;
    },

    /**
     * 关闭详情模态框
     */
    closeDetailModal() {
        document.getElementById('detail-modal')?.classList.remove('modal--open');
        this.currentApproval = null;
    },

    /**
     * 通过审批
     */
    approveApproval() {
        this.currentAction = 'approve';
        document.getElementById('comment-modal-title').textContent = '审批通过';
        document.getElementById('comment-modal')?.classList.add('modal--open');
    },

    /**
     * 拒绝审批
     */
    rejectApproval() {
        this.currentAction = 'reject';
        document.getElementById('comment-modal-title').textContent = '审批拒绝';
        document.getElementById('comment-modal')?.classList.add('modal--open');
    },

    /**
     * 关闭意见模态框
     */
    closeCommentModal() {
        document.getElementById('comment-modal')?.classList.remove('modal--open');
        document.getElementById('approval-comment').value = '';
        this.currentAction = null;
    },

    /**
     * 提交审批意见
     */
    submitComment() {
        const comment = document.getElementById('approval-comment')?.value;
        if (!comment) {
            alert('请输入审批意见');
            return;
        }

        if (!this.currentApproval || !this.currentAction) return;

        // 更新审批状态
        if (this.currentAction === 'approve') {
            this.currentApproval.status = 'approved';
            this.statistics.approved++;
        } else {
            this.currentApproval.status = 'rejected';
            this.statistics.rejected++;
        }
        this.statistics.pending--;

        // 添加到记录
        this.records.unshift({
            id: 'REC-' + Date.now(),
            approvalId: this.currentApproval.id,
            type: this.currentApproval.type,
            applicant: this.currentApproval.applicant,
            approver: '当前用户',
            action: this.currentAction,
            time: new Date().toLocaleString('zh-CN'),
            comment: comment
        });

        // 从待办列表移除
        const index = this.pendingList.findIndex(item => item.id === this.currentApproval.id);
        if (index > -1) {
            this.pendingList.splice(index, 1);
        }

        // 更新统计
        this.loadStatistics();

        // 关闭模态框
        this.closeCommentModal();
        this.closeDetailModal();

        // 刷新列表
        this.loadPendingList();

        // 显示成功提示
        alert(this.currentAction === 'approve' ? '审批已通过' : '审批已拒绝');
    },

    /**
     * Promisify API调用
     */
    promisify(api, method, ...args) {
        return new Promise((resolve, reject) => {
            // NexusAPI 使用单参数回调格式 callback(result)
            // 成功时: result = { status: 'success', data: ... }
            // 错误时: result = { status: 'error', message: ... }
            api[method](...args, (result) => {
                resolve(result || { status: 'success', data: null });
            });
        });
    },
    
    /**
     * 更新左侧菜单徽章
     */
    updateMenuBadges() {
        // 更新待办数量
        const pendingCount = this.pendingList?.length || 0;
        const pendingBadge = document.getElementById('menu-pending-count');
        if (pendingBadge) pendingBadge.textContent = pendingCount;
        
        // 更新已发起数量
        const submittedCount = this.submittedList?.length || 0;
        const submittedBadge = document.getElementById('menu-submitted-count');
        if (submittedBadge) submittedBadge.textContent = submittedCount;
    },
    
    /**
     * 导出报表
     */
    exportReport() {
        alert('报表导出功能开发中...');
    },
    
    /**
     * 批量审批
     */
    openBatchApproval() {
        alert('批量审批功能开发中...');
    }
};

// 初始化
document.addEventListener('DOMContentLoaded', () => {
    ApprovalForm.init();
});
