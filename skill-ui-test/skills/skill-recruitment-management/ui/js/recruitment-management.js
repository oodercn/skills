const RecruitmentManagement = {
    jobs: [],
    resumes: [],
    interviews: [],
    offers: [],
    currentJob: null,
    currentInterview: null,
    currentOffer: null,
    
    init() {
        this.initTheme();
        this.loadData();
        this.initEventListeners();
    },
    
    initEventListeners() {
        // 标签页切换
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const tab = e.currentTarget.dataset.tab;
                this.switchTab(tab);
            });
        });
    },
    
    async loadData() {
        await Promise.all([
            this.loadJobs(),
            this.loadResumes(),
            this.loadInterviews(),
            this.loadOffers(),
            this.loadStatistics()
        ]);
        // 数据加载完成后更新左侧菜单徽章
        this.updateMenuBadges();
    },
    
    // ========== 职位管理 ==========
    async loadJobs() {
        try {
            const response = await this.promisify(NexusAPI, 'get', '/api/recruitment/jobs');
            if (response.status === 'success' && response.data) {
                this.jobs = response.data.jobs || [];
            } else {
                this.jobs = this.getMockJobs();
            }
        } catch (error) {
            console.error('Failed to load jobs:', error);
            this.jobs = this.getMockJobs();
        }
        this.renderJobs();
        this.updateJobFilterOptions();
    },
    
    getMockJobs() {
        return [
            {
                id: 'job-001',
                title: '高级Java工程师',
                department: '技术部',
                salaryMin: 25,
                salaryMax: 40,
                location: '北京',
                experience: '3-5年',
                education: '本科',
                description: '负责后端系统开发',
                status: 'active',
                resumeCount: 15,
                createdAt: Date.now() - 86400000 * 5
            },
            {
                id: 'job-002',
                title: '产品经理',
                department: '产品部',
                salaryMin: 20,
                salaryMax: 35,
                location: '上海',
                experience: '3-5年',
                education: '本科',
                description: '负责产品规划',
                status: 'active',
                resumeCount: 8,
                createdAt: Date.now() - 86400000 * 3
            },
            {
                id: 'job-003',
                title: 'UI设计师',
                department: '设计部',
                salaryMin: 15,
                salaryMax: 25,
                location: '深圳',
                experience: '1-3年',
                education: '本科',
                description: '负责界面设计',
                status: 'paused',
                resumeCount: 12,
                createdAt: Date.now() - 86400000 * 10
            }
        ];
    },
    
    renderJobs() {
        const tbody = document.getElementById('job-table-body');
        if (!tbody) return;
        
        if (!this.jobs.length) {
            tbody.innerHTML = '<tr><td colspan="8" class="nx-text-center nx-text-secondary">暂无职位数据</td></tr>';
            return;
        }
        
        tbody.innerHTML = this.jobs.map(job => `
            <tr>
                <td><strong>${job.title}</strong></td>
                <td>${job.department}</td>
                <td>${job.salaryMin || 0}-${job.salaryMax || 0}K</td>
                <td>${job.location || '-'}</td>
                <td><span class="nx-badge">${job.resumeCount || 0}</span></td>
                <td>${this.getJobStatusBadge(job.status)}</td>
                <td>${this.formatDate(job.createdAt)}</td>
                <td>
                    <div class="action-btns">
                        <button class="nx-btn nx-btn--ghost nx-btn--icon nx-btn--sm" onclick="RecruitmentManagement.editJob('${job.id}')" title="编辑">
                            <i class="ri-edit-line"></i>
                        </button>
                        <button class="nx-btn nx-btn--ghost nx-btn--icon nx-btn--sm" onclick="RecruitmentManagement.deleteJob('${job.id}')" title="删除">
                            <i class="ri-delete-bin-line"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    },
    
    getJobStatusBadge(status) {
        const statusMap = {
            'active': '<span class="status-badge status-badge--success">招聘中</span>',
            'paused': '<span class="status-badge status-badge--warning">已暂停</span>',
            'closed': '<span class="status-badge status-badge--secondary">已关闭</span>'
        };
        return statusMap[status] || status;
    },
    
    updateJobFilterOptions() {
        const select = document.getElementById('resume-filter-job');
        if (select) {
            select.innerHTML = '<option value="">全部职位</option>';
            this.jobs.forEach(job => {
                select.innerHTML += `<option value="${job.id}">${job.title}</option>`;
            });
        }
    },
    
    openJobModal() {
        this.currentJob = null;
        document.getElementById('job-modal-title').textContent = '发布职位';
        document.getElementById('job-form').reset();
        document.getElementById('job-id').value = '';
        document.getElementById('job-modal').classList.add('modal--open');
    },
    
    editJob(id) {
        const job = this.jobs.find(j => j.id === id);
        if (!job) return;
        
        this.currentJob = job;
        document.getElementById('job-modal-title').textContent = '编辑职位';
        document.getElementById('job-id').value = job.id;
        document.getElementById('job-title').value = job.title;
        document.getElementById('job-department').value = job.department;
        document.getElementById('job-salary-min').value = job.salaryMin;
        document.getElementById('job-salary-max').value = job.salaryMax;
        document.getElementById('job-location').value = job.location || '';
        document.getElementById('job-experience').value = job.experience || '';
        document.getElementById('job-education').value = job.education || '';
        document.getElementById('job-description').value = job.description || '';
        
        const statusRadio = document.querySelector(`input[name="job-status"][value="${job.status}"]`);
        if (statusRadio) statusRadio.checked = true;
        
        document.getElementById('job-modal').classList.add('modal--open');
    },
    
    closeJobModal() {
        document.getElementById('job-modal').classList.remove('modal--open');
    },
    
    async saveJob() {
        const id = document.getElementById('job-id').value;
        const job = {
            title: document.getElementById('job-title').value,
            department: document.getElementById('job-department').value,
            salaryMin: parseInt(document.getElementById('job-salary-min').value) || 0,
            salaryMax: parseInt(document.getElementById('job-salary-max').value) || 0,
            location: document.getElementById('job-location').value,
            experience: document.getElementById('job-experience').value,
            education: document.getElementById('job-education').value,
            description: document.getElementById('job-description').value,
            status: document.querySelector('input[name="job-status"]:checked')?.value || 'active'
        };
        
        if (!job.title || !job.department) {
            alert('请填写必填字段');
            return;
        }
        
        try {
            let response;
            if (id) {
                response = await this.promisify(NexusAPI, 'put', `/api/recruitment/jobs/${id}`, job);
            } else {
                response = await this.promisify(NexusAPI, 'post', '/api/recruitment/jobs', job);
            }
            
            if (response.status === 'success') {
                this.closeJobModal();
                await this.loadJobs();
            } else {
                // 模拟保存
                if (!id) {
                    job.id = 'job-' + Date.now();
                    job.resumeCount = 0;
                    job.createdAt = Date.now();
                    this.jobs.unshift(job);
                } else {
                    const index = this.jobs.findIndex(j => j.id === id);
                    if (index >= 0) {
                        this.jobs[index] = { ...this.jobs[index], ...job };
                    }
                }
                this.renderJobs();
                this.closeJobModal();
            }
        } catch (error) {
            console.error('Failed to save job:', error);
            // 模拟保存
            if (!id) {
                job.id = 'job-' + Date.now();
                job.resumeCount = 0;
                job.createdAt = Date.now();
                this.jobs.unshift(job);
            } else {
                const index = this.jobs.findIndex(j => j.id === id);
                if (index >= 0) {
                    this.jobs[index] = { ...this.jobs[index], ...job };
                }
            }
            this.renderJobs();
            this.closeJobModal();
        }
    },
    
    async deleteJob(id) {
        if (!confirm('确定要删除此职位吗？')) return;
        
        try {
            await this.promisify(NexusAPI, 'delete', `/api/recruitment/jobs/${id}`);
        } catch (error) {
            console.error('Failed to delete job:', error);
        }
        
        this.jobs = this.jobs.filter(j => j.id !== id);
        this.renderJobs();
    },
    
    filterJobs() {
        const department = document.getElementById('job-filter-department')?.value;
        const status = document.getElementById('job-filter-status')?.value;
        
        let filtered = this.jobs;
        if (department) {
            filtered = filtered.filter(j => j.department === department);
        }
        if (status) {
            filtered = filtered.filter(j => j.status === status);
        }
        
        // 临时替换并渲染
        const originalJobs = this.jobs;
        this.jobs = filtered;
        this.renderJobs();
        this.jobs = originalJobs;
    },
    
    // ========== 简历管理 ==========
    async loadResumes() {
        try {
            const response = await this.promisify(NexusAPI, 'get', '/api/recruitment/resumes');
            if (response.status === 'success' && response.data) {
                this.resumes = response.data.resumes || [];
            } else {
                this.resumes = this.getMockResumes();
            }
        } catch (error) {
            console.error('Failed to load resumes:', error);
            this.resumes = this.getMockResumes();
        }
        this.renderResumes();
    },
    
    getMockResumes() {
        return [
            {
                id: 'resume-001',
                name: '张三',
                jobId: 'job-001',
                jobTitle: '高级Java工程师',
                phone: '13800138001',
                email: 'zhangsan@example.com',
                experience: '5年',
                matchScore: 85,
                status: 'new',
                createdAt: Date.now() - 86400000 * 1
            },
            {
                id: 'resume-002',
                name: '李四',
                jobId: 'job-001',
                jobTitle: '高级Java工程师',
                phone: '13800138002',
                email: 'lisi@example.com',
                experience: '3年',
                matchScore: 72,
                status: 'screening',
                createdAt: Date.now() - 86400000 * 2
            },
            {
                id: 'resume-003',
                name: '王五',
                jobId: 'job-002',
                jobTitle: '产品经理',
                phone: '13800138003',
                email: 'wangwu@example.com',
                experience: '4年',
                matchScore: 90,
                status: 'interview',
                createdAt: Date.now() - 86400000 * 3
            }
        ];
    },
    
    renderResumes() {
        const tbody = document.getElementById('resume-table-body');
        if (!tbody) return;
        
        if (!this.resumes.length) {
            tbody.innerHTML = '<tr><td colspan="8" class="nx-text-center nx-text-secondary">暂无简历数据</td></tr>';
            return;
        }
        
        tbody.innerHTML = this.resumes.map(resume => `
            <tr>
                <td>
                    <strong>${resume.name}</strong>
                    <div class="nx-text-xs nx-text-secondary">${resume.email}</div>
                </td>
                <td>${resume.jobTitle}</td>
                <td>${resume.phone}</td>
                <td>${resume.experience}</td>
                <td>
                    <div class="match-score match-score--${resume.matchScore >= 80 ? 'high' : resume.matchScore >= 60 ? 'medium' : 'low'}">
                        ${resume.matchScore}%
                    </div>
                </td>
                <td>${this.getResumeStatusBadge(resume.status)}</td>
                <td>${this.formatDate(resume.createdAt)}</td>
                <td>
                    <div class="action-btns">
                        <button class="nx-btn nx-btn--ghost nx-btn--icon nx-btn--sm" onclick="RecruitmentManagement.viewResume('${resume.id}')" title="查看">
                            <i class="ri-eye-line"></i>
                        </button>
                        <button class="nx-btn nx-btn--ghost nx-btn--icon nx-btn--sm" onclick="RecruitmentManagement.scheduleInterview('${resume.id}')" title="安排面试">
                            <i class="ri-calendar-check-line"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    },
    
    getResumeStatusBadge(status) {
        const statusMap = {
            'new': '<span class="status-badge status-badge--info">新投递</span>',
            'screening': '<span class="status-badge status-badge--warning">筛选中</span>',
            'interview': '<span class="status-badge status-badge--primary">面试中</span>',
            'offer': '<span class="status-badge status-badge--success">录用中</span>',
            'rejected': '<span class="status-badge status-badge--secondary">已淘汰</span>'
        };
        return statusMap[status] || status;
    },
    
    filterResumes() {
        const jobId = document.getElementById('resume-filter-job')?.value;
        const status = document.getElementById('resume-filter-status')?.value;
        
        let filtered = this.resumes;
        if (jobId) {
            filtered = filtered.filter(r => r.jobId === jobId);
        }
        if (status) {
            filtered = filtered.filter(r => r.status === status);
        }
        
        const originalResumes = this.resumes;
        this.resumes = filtered;
        this.renderResumes();
        this.resumes = originalResumes;
    },
    
    viewResume(id) {
        alert('查看简历详情功能开发中...');
    },
    
    scheduleInterview(resumeId) {
        const resume = this.resumes.find(r => r.id === resumeId);
        if (!resume) return;
        
        this.openInterviewModal();
        document.getElementById('interview-resume').value = resumeId;
        document.getElementById('interview-job').value = resume.jobId;
    },
    
    // ========== 面试管理 ==========
    async loadInterviews() {
        try {
            const response = await this.promisify(NexusAPI, 'get', '/api/recruitment/interviews');
            if (response.status === 'success' && response.data) {
                this.interviews = response.data.interviews || [];
            } else {
                this.interviews = this.getMockInterviews();
            }
        } catch (error) {
            console.error('Failed to load interviews:', error);
            this.interviews = this.getMockInterviews();
        }
        this.renderInterviews();
    },
    
    getMockInterviews() {
        return [
            {
                id: 'interview-001',
                candidateName: '张三',
                jobTitle: '高级Java工程师',
                interviewTime: Date.now() + 86400000 * 2,
                interviewers: '技术总监,HR经理',
                type: 'onsite',
                status: 'scheduled'
            },
            {
                id: 'interview-002',
                candidateName: '王五',
                jobTitle: '产品经理',
                interviewTime: Date.now() - 86400000 * 1,
                interviewers: '产品总监',
                type: 'video',
                status: 'completed'
            }
        ];
    },
    
    renderInterviews() {
        const tbody = document.getElementById('interview-table-body');
        if (!tbody) return;
        
        if (!this.interviews.length) {
            tbody.innerHTML = '<tr><td colspan="7" class="nx-text-center nx-text-secondary">暂无面试安排</td></tr>';
            return;
        }
        
        tbody.innerHTML = this.interviews.map(interview => `
            <tr>
                <td><strong>${interview.candidateName}</strong></td>
                <td>${interview.jobTitle}</td>
                <td>${this.formatDateTime(interview.interviewTime)}</td>
                <td>${interview.interviewers}</td>
                <td>${this.getInterviewTypeLabel(interview.type)}</td>
                <td>${this.getInterviewStatusBadge(interview.status)}</td>
                <td>
                    <div class="action-btns">
                        <button class="nx-btn nx-btn--ghost nx-btn--icon nx-btn--sm" onclick="RecruitmentManagement.editInterview('${interview.id}')" title="编辑">
                            <i class="ri-edit-line"></i>
                        </button>
                        <button class="nx-btn nx-btn--ghost nx-btn--icon nx-btn--sm" onclick="RecruitmentManagement.cancelInterview('${interview.id}')" title="取消">
                            <i class="ri-close-circle-line"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    },
    
    getInterviewTypeLabel(type) {
        const typeMap = {
            'phone': '电话面试',
            'video': '视频面试',
            'onsite': '现场面试'
        };
        return typeMap[type] || type;
    },
    
    getInterviewStatusBadge(status) {
        const statusMap = {
            'scheduled': '<span class="status-badge status-badge--primary">待进行</span>',
            'completed': '<span class="status-badge status-badge--success">已完成</span>',
            'cancelled': '<span class="status-badge status-badge--secondary">已取消</span>'
        };
        return statusMap[status] || status;
    },
    
    openInterviewModal() {
        this.currentInterview = null;
        document.getElementById('interview-modal-title').textContent = '安排面试';
        document.getElementById('interview-form').reset();
        document.getElementById('interview-id').value = '';
        
        // 更新候选人选项
        const resumeSelect = document.getElementById('interview-resume');
        resumeSelect.innerHTML = '<option value="">请选择候选人</option>';
        this.resumes.forEach(r => {
            resumeSelect.innerHTML += `<option value="${r.id}">${r.name} - ${r.jobTitle}</option>`;
        });
        
        // 更新职位选项
        const jobSelect = document.getElementById('interview-job');
        jobSelect.innerHTML = '<option value="">请选择职位</option>';
        this.jobs.forEach(j => {
            jobSelect.innerHTML += `<option value="${j.id}">${j.title}</option>`;
        });
        
        document.getElementById('interview-modal').classList.add('modal--open');
    },
    
    closeInterviewModal() {
        document.getElementById('interview-modal').classList.remove('modal--open');
    },
    
    async saveInterview() {
        const interview = {
            resumeId: document.getElementById('interview-resume').value,
            jobId: document.getElementById('interview-job').value,
            interviewTime: new Date(document.getElementById('interview-time').value).getTime(),
            type: document.getElementById('interview-type').value,
            interviewers: document.getElementById('interviewers').value,
            location: document.getElementById('interview-location').value,
            notes: document.getElementById('interview-notes').value
        };
        
        if (!interview.resumeId || !interview.jobId || !interview.interviewTime) {
            alert('请填写必填字段');
            return;
        }
        
        try {
            const response = await this.promisify(NexusAPI, 'post', '/api/recruitment/interviews', interview);
            if (response.status === 'success') {
                this.closeInterviewModal();
                await this.loadInterviews();
            } else {
                // 模拟保存
                interview.id = 'interview-' + Date.now();
                const resume = this.resumes.find(r => r.id === interview.resumeId);
                const job = this.jobs.find(j => j.id === interview.jobId);
                interview.candidateName = resume?.name || '未知';
                interview.jobTitle = job?.title || '未知';
                interview.status = 'scheduled';
                this.interviews.unshift(interview);
                this.renderInterviews();
                this.closeInterviewModal();
            }
        } catch (error) {
            console.error('Failed to save interview:', error);
            // 模拟保存
            interview.id = 'interview-' + Date.now();
            const resume = this.resumes.find(r => r.id === interview.resumeId);
            const job = this.jobs.find(j => j.id === interview.jobId);
            interview.candidateName = resume?.name || '未知';
            interview.jobTitle = job?.title || '未知';
            interview.status = 'scheduled';
            this.interviews.unshift(interview);
            this.renderInterviews();
            this.closeInterviewModal();
        }
    },
    
    editInterview(id) {
        alert('编辑面试功能开发中...');
    },
    
    cancelInterview(id) {
        if (!confirm('确定要取消此面试吗？')) return;
        
        const index = this.interviews.findIndex(i => i.id === id);
        if (index >= 0) {
            this.interviews[index].status = 'cancelled';
            this.renderInterviews();
        }
    },
    
    // ========== 录用审批 ==========
    async loadOffers() {
        try {
            const response = await this.promisify(NexusAPI, 'get', '/api/recruitment/offers');
            if (response.status === 'success' && response.data) {
                this.offers = response.data.offers || [];
            } else {
                this.offers = this.getMockOffers();
            }
        } catch (error) {
            console.error('Failed to load offers:', error);
            this.offers = this.getMockOffers();
        }
        this.renderOffers();
    },
    
    getMockOffers() {
        return [
            {
                id: 'offer-001',
                candidateName: '王五',
                jobTitle: '产品经理',
                salary: 30,
                startDate: '2024-04-01',
                applicant: 'HR经理',
                status: 'pending'
            },
            {
                id: 'offer-002',
                candidateName: '赵六',
                jobTitle: 'UI设计师',
                salary: 22,
                startDate: '2024-03-15',
                applicant: '设计总监',
                status: 'approved'
            }
        ];
    },
    
    renderOffers() {
        const tbody = document.getElementById('offer-table-body');
        if (!tbody) return;
        
        if (!this.offers.length) {
            tbody.innerHTML = '<tr><td colspan="7" class="nx-text-center nx-text-secondary">暂无录用审批</td></tr>';
            return;
        }
        
        tbody.innerHTML = this.offers.map(offer => `
            <tr>
                <td><strong>${offer.candidateName}</strong></td>
                <td>${offer.jobTitle}</td>
                <td>${offer.salary}K</td>
                <td>${offer.startDate}</td>
                <td>${offer.applicant}</td>
                <td>${this.getOfferStatusBadge(offer.status)}</td>
                <td>
                    <div class="action-btns">
                        ${offer.status === 'pending' ? `
                            <button class="nx-btn nx-btn--ghost nx-btn--icon nx-btn--sm" onclick="RecruitmentManagement.openOfferModal('${offer.id}')" title="审批">
                                <i class="ri-check-line"></i>
                            </button>
                        ` : '-'}
                    </div>
                </td>
            </tr>
        `).join('');
    },
    
    getOfferStatusBadge(status) {
        const statusMap = {
            'pending': '<span class="status-badge status-badge--warning">待审批</span>',
            'approved': '<span class="status-badge status-badge--success">已通过</span>',
            'rejected': '<span class="status-badge status-badge--danger">已拒绝</span>'
        };
        return statusMap[status] || status;
    },
    
    filterOffers() {
        const status = document.getElementById('offer-filter-status')?.value;
        
        let filtered = this.offers;
        if (status) {
            filtered = filtered.filter(o => o.status === status);
        }
        
        const originalOffers = this.offers;
        this.offers = filtered;
        this.renderOffers();
        this.offers = originalOffers;
    },
    
    openOfferModal(id) {
        const offer = this.offers.find(o => o.id === id);
        if (!offer) return;
        
        this.currentOffer = offer;
        document.getElementById('offer-id').value = offer.id;
        document.getElementById('offer-candidate').value = offer.candidateName;
        document.getElementById('offer-job').value = offer.jobTitle;
        document.getElementById('offer-salary').value = offer.salary;
        document.getElementById('offer-start-date').value = offer.startDate;
        document.getElementById('offer-comment').value = '';
        
        document.getElementById('offer-modal').classList.add('modal--open');
    },
    
    closeOfferModal() {
        document.getElementById('offer-modal').classList.remove('modal--open');
    },
    
    async approveOffer() {
        if (!this.currentOffer) return;
        
        const comment = document.getElementById('offer-comment').value;
        
        try {
            await this.promisify(NexusAPI, 'post', `/api/recruitment/offers/${this.currentOffer.id}/approve`, {
                status: 'approved',
                comment
            });
        } catch (error) {
            console.error('Failed to approve offer:', error);
        }
        
        const index = this.offers.findIndex(o => o.id === this.currentOffer.id);
        if (index >= 0) {
            this.offers[index].status = 'approved';
            this.renderOffers();
        }
        this.closeOfferModal();
    },
    
    async rejectOffer() {
        if (!this.currentOffer) return;
        
        const comment = document.getElementById('offer-comment').value;
        if (!comment) {
            alert('请填写拒绝原因');
            return;
        }
        
        try {
            await this.promisify(NexusAPI, 'post', `/api/recruitment/offers/${this.currentOffer.id}/approve`, {
                status: 'rejected',
                comment
            });
        } catch (error) {
            console.error('Failed to reject offer:', error);
        }
        
        const index = this.offers.findIndex(o => o.id === this.currentOffer.id);
        if (index >= 0) {
            this.offers[index].status = 'rejected';
            this.renderOffers();
        }
        this.closeOfferModal();
    },
    
    // ========== 数据统计 ==========
    async loadStatistics() {
        try {
            const response = await this.promisify(NexusAPI, 'get', '/api/recruitment/statistics');
            if (response.status === 'success' && response.data) {
                this.updateStatistics(response.data);
            } else {
                this.updateStatistics(this.getMockStatistics());
            }
        } catch (error) {
            console.error('Failed to load statistics:', error);
            this.updateStatistics(this.getMockStatistics());
        }
    },
    
    getMockStatistics() {
        return {
            activeJobs: 8,
            newResumes: 25,
            pendingInterviews: 6,
            pendingOffers: 3,
            funnel: {
                resumes: 120,
                screening: 80,
                interviews: 45,
                offers: 15,
                hired: 8
            },
            deptStats: [
                { dept: '技术部', jobs: 5, resumes: 60 },
                { dept: '产品部', jobs: 2, resumes: 30 },
                { dept: '运营部', jobs: 1, resumes: 30 }
            ]
        };
    },
    
    updateStatistics(data) {
        // 更新顶部统计卡片
        document.getElementById('stat-active-jobs').textContent = data.activeJobs || 0;
        document.getElementById('stat-new-resumes').textContent = data.newResumes || 0;
        document.getElementById('stat-interviews').textContent = data.pendingInterviews || 0;
        document.getElementById('stat-offers').textContent = data.pendingOffers || 0;
        
        // 更新快捷统计
        document.getElementById('stat-today-resumes').textContent = data.todayResumes || Math.floor(Math.random() * 10) + 3;
        document.getElementById('stat-today-interviews').textContent = data.todayInterviews || Math.floor(Math.random() * 5) + 1;
        document.getElementById('stat-week-hired').textContent = data.weekHired || Math.floor(Math.random() * 3) + 1;
        document.getElementById('stat-avg-process').textContent = data.avgProcessDays || (Math.floor(Math.random() * 10) + 15);
        
        // 更新漏斗图
        if (data.funnel) {
            const resumes = data.funnel.resumes || 0;
            const screening = data.funnel.screening || 0;
            const interviews = data.funnel.interviews || 0;
            const offers = data.funnel.offers || 0;
            const hired = data.funnel.hired || 0;
            
            document.getElementById('funnel-resumes').textContent = resumes;
            document.getElementById('funnel-screening').textContent = screening;
            document.getElementById('funnel-interviews').textContent = interviews;
            document.getElementById('funnel-offers').textContent = offers;
            document.getElementById('funnel-hired').textContent = hired;
            
            // 更新转化率
            document.getElementById('rate-screening').textContent = resumes > 0 ? Math.round(screening / resumes * 100) + '%' : '0%';
            document.getElementById('rate-interview').textContent = screening > 0 ? Math.round(interviews / screening * 100) + '%' : '0%';
            document.getElementById('rate-offer').textContent = interviews > 0 ? Math.round(offers / interviews * 100) + '%' : '0%';
            document.getElementById('rate-hired').textContent = offers > 0 ? Math.round(hired / offers * 100) + '%' : '0%';
        }
        
        // 更新部门统计
        if (data.deptStats) {
            const deptStatsEl = document.getElementById('dept-stats');
            deptStatsEl.innerHTML = data.deptStats.map(ds => `
                <div class="dept-stat-item">
                    <div class="dept-name">${ds.dept}</div>
                    <div class="dept-progress">
                        <div class="progress-bar" style="width: ${Math.min(ds.resumes / 100 * 100, 100)}%"></div>
                    </div>
                    <div class="dept-numbers">
                        <span>${ds.jobs}个职位</span>
                        <span>${ds.resumes}份简历</span>
                    </div>
                </div>
            `).join('');
        }
        
        // 渲染简历来源渠道
        this.renderSourceChart(data.sourceStats);
        
        // 渲染面试官工作量
        this.renderInterviewerStats(data.interviewerStats);
        
        // 渲染近期动态
        this.renderActivityTimeline(data.activities);
    },
    
    // ========== 渲染简历来源渠道 ==========
    renderSourceChart(sourceStats) {
        const container = document.getElementById('source-chart');
        if (!container) return;
        
        // 模拟数据
        const sources = sourceStats || [
            { name: 'BOSS直聘', count: 45, icon: 'ri-briefcase-line', color: '#3b82f6' },
            { name: '智联招聘', count: 32, icon: 'ri-user-search-line', color: '#22c55e' },
            { name: '猎聘网', count: 28, icon: 'ri-vip-crown-line', color: '#f59e0b' },
            { name: '前程无忧', count: 20, icon: 'ri-globe-line', color: '#8b5cf6' },
            { name: '内部推荐', count: 15, icon: 'ri-share-forward-line', color: '#ec4899' }
        ];
        
        const maxCount = Math.max(...sources.map(s => s.count));
        
        container.innerHTML = sources.map(source => {
            const percentage = Math.round(source.count / maxCount * 100);
            return `
                <div class="source-item">
                    <div class="source-icon" style="background: ${source.color}20; color: ${source.color};">
                        <i class="${source.icon}"></i>
                    </div>
                    <div class="source-info">
                        <div class="source-name">${source.name}</div>
                        <div class="source-count">${source.count}份简历</div>
                    </div>
                    <div class="source-bar">
                        <div class="source-fill" style="width: ${percentage}%; background: ${source.color};"></div>
                    </div>
                    <div class="source-percent">${percentage}%</div>
                </div>
            `;
        }).join('');
    },
    
    // ========== 渲染面试官工作量 ==========
    renderInterviewerStats(interviewerStats) {
        const container = document.getElementById('interviewer-stats');
        if (!container) return;
        
        // 模拟数据
        const interviewers = interviewerStats || [
            { name: '张经理', dept: '技术部', count: 12, rating: 4.8 },
            { name: '李总监', dept: '产品部', count: 8, rating: 4.5 },
            { name: '王主管', dept: '运营部', count: 6, rating: 4.9 },
            { name: '赵经理', dept: '市场部', count: 5, rating: 4.3 }
        ];
        
        container.innerHTML = interviewers.map(interviewer => `
            <div class="interviewer-item">
                <div class="interviewer-avatar">${interviewer.name.charAt(0)}</div>
                <div class="interviewer-info">
                    <div class="interviewer-name">${interviewer.name}</div>
                    <div class="interviewer-dept">${interviewer.dept}</div>
                </div>
                <div class="interviewer-count">
                    <div class="interviewer-count__value">${interviewer.count}</div>
                    <div class="interviewer-count__label">场面试</div>
                </div>
                <div class="interviewer-rating">
                    <i class="ri-star-fill"></i> ${interviewer.rating}
                </div>
            </div>
        `).join('');
    },
    
    // ========== 渲染近期动态时间线 ==========
    renderActivityTimeline(activities) {
        const container = document.getElementById('activity-timeline');
        if (!container) return;
        
        // 模拟数据
        const activityData = activities || [
            { type: 'resume', title: '新简历投递', desc: '张三 投递了 高级Java工程师 职位', time: '10分钟前', tag: 'new' },
            { type: 'interview', title: '面试完成', desc: '李四 完成了 产品经理 的二面', time: '30分钟前', tag: 'completed' },
            { type: 'offer', title: '录用审批通过', desc: '王五 的 前端开发工程师 Offer已通过', time: '1小时前', tag: 'completed' },
            { type: 'resume', title: '简历筛选通过', desc: '赵六 的简历已通过技术部筛选', time: '2小时前', tag: 'completed' },
            { type: 'interview', title: '面试安排', desc: '钱七 安排了 数据分析师 的面试', time: '3小时前', tag: 'pending' },
            { type: 'hired', title: '新员工入职', desc: '孙八 已办理 测试工程师 入职手续', time: '5小时前', tag: 'completed' },
            { type: 'rejected', title: '面试未通过', desc: '周九 的 运维工程师 面试未通过', time: '昨天', tag: 'completed' },
            { type: 'resume', title: '新简历投递', desc: '吴十 投递了 UI设计师 职位', time: '昨天', tag: 'new' }
        ];
        
        this.activities = activityData;
        this.currentActivityFilter = 'all';
        this.filterActivity('all');
    },
    
    // ========== 筛选近期动态 ==========
    filterActivity(filter) {
        this.currentActivityFilter = filter;
        
        // 更新筛选按钮状态
        document.querySelectorAll('.activity-filter__btn').forEach(btn => {
            btn.classList.remove('active');
            if (btn.dataset.filter === filter) {
                btn.classList.add('active');
            }
        });
        
        const container = document.getElementById('activity-timeline');
        if (!container || !this.activities) return;
        
        // 筛选活动
        let filteredActivities = this.activities;
        if (filter !== 'all') {
            filteredActivities = this.activities.filter(a => a.type === filter);
        }
        
        // 图标映射
        const iconMap = {
            resume: { icon: 'ri-file-list-line', class: 'resume' },
            interview: { icon: 'ri-calendar-check-line', class: 'interview' },
            offer: { icon: 'ri-award-line', class: 'offer' },
            hired: { icon: 'ri-user-follow-line', class: 'hired' },
            rejected: { icon: 'ri-close-circle-line', class: 'rejected' }
        };
        
        // 标签映射
        const tagMap = {
            new: '新',
            pending: '待处理',
            completed: '已完成'
        };
        
        container.innerHTML = filteredActivities.map(activity => {
            const iconInfo = iconMap[activity.type] || iconMap.resume;
            return `
                <div class="activity-item ${activity.type}">
                    <div class="activity-icon ${iconInfo.class}">
                        <i class="${iconInfo.icon}"></i>
                    </div>
                    <div class="activity-content">
                        <div class="activity-title">${activity.title}</div>
                        <div class="activity-desc">${activity.desc}</div>
                        <div class="activity-meta">
                            <span class="activity-time"><i class="ri-time-line"></i> ${activity.time}</span>
                            <span class="activity-tag ${activity.tag}">${tagMap[activity.tag] || activity.tag}</span>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    },
    
    // ========== 标签页切换 ==========
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
        
        // 显示对应内容
        document.querySelectorAll('.tab-content').forEach(content => {
            content.classList.remove('active');
        });
        document.getElementById(`tab-${tab}`)?.classList.add('active');
        
        // 加载对应数据
        switch(tab) {
            case 'jobs':
                this.loadJobs();
                break;
            case 'resumes':
                this.loadResumes();
                break;
            case 'interviews':
                this.loadInterviews();
                break;
            case 'offers':
                this.loadOffers();
                break;
            case 'dashboard':
                this.loadStatistics();
                break;
        }
    },
    
    // ========== 更新左侧菜单数量徽章 ==========
    updateMenuBadges() {
        // 更新职位数量
        const jobCount = this.jobs?.length || 0;
        const jobBadge = document.getElementById('menu-job-count');
        if (jobBadge) jobBadge.textContent = jobCount;
        
        // 更新简历数量
        const resumeCount = this.resumes?.length || 0;
        const resumeBadge = document.getElementById('menu-resume-count');
        if (resumeBadge) resumeBadge.textContent = resumeCount;
        
        // 更新面试数量
        const interviewCount = this.interviews?.length || 0;
        const interviewBadge = document.getElementById('menu-interview-count');
        if (interviewBadge) interviewBadge.textContent = interviewCount;
        
        // 更新录用数量
        const offerCount = this.offers?.length || 0;
        const offerBadge = document.getElementById('menu-offer-count');
        if (offerBadge) offerBadge.textContent = offerCount;
    },
    
    // ========== 导出报表 ==========
    exportReport() {
        alert('报表导出功能开发中...');
    },
    
    // ========== 简历解析 ==========
    openResumeParser() {
        alert('简历解析功能开发中...');
    },
    
    // ========== 工具函数 ==========
    promisify(obj, method, ...args) {
        return new Promise((resolve, reject) => {
            // NexusAPI 使用单参数回调格式 callback(result)
            // 成功时: result = { status: 'success', data: ... }
            // 错误时: result = { status: 'error', message: ... }
            obj[method](...args, (result) => {
                resolve(result || { status: 'success', data: null });
            });
        });
    },
    
    formatDate(timestamp) {
        if (!timestamp) return '-';
        const date = new Date(timestamp);
        return `${date.getMonth() + 1}/${date.getDate()}`;
    },
    
    formatDateTime(timestamp) {
        if (!timestamp) return '-';
        const date = new Date(timestamp);
        return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`;
    },
    
    formatDate(timestamp) {
        const date = new Date(timestamp);
        return `${date.getMonth() + 1}/${date.getDate()}`;
    },
    
    // ========== 主题切换功能 ==========
    initTheme() {
        // 从localStorage读取主题设置
        const savedTheme = localStorage.getItem('recruitment-theme');
        if (savedTheme === 'dark') {
            document.documentElement.setAttribute('data-theme', 'dark');
            this.updateThemeIcon(true);
        } else {
            // 默认亮色主题，移除dark属性
            document.documentElement.removeAttribute('data-theme');
            this.updateThemeIcon(false);
        }
    },
    
    toggleTheme() {
        const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
        if (isDark) {
            // 切换到亮色
            document.documentElement.removeAttribute('data-theme');
            localStorage.setItem('recruitment-theme', 'light');
            this.updateThemeIcon(false);
        } else {
            // 切换到深色
            document.documentElement.setAttribute('data-theme', 'dark');
            localStorage.setItem('recruitment-theme', 'dark');
            this.updateThemeIcon(true);
        }
    },
    
    updateThemeIcon(isDark) {
        const icon = document.getElementById('theme-icon');
        const text = document.getElementById('theme-text');
        if (icon && text) {
            if (isDark) {
                icon.className = 'ri-moon-line';
                text.textContent = '深色模式';
            } else {
                icon.className = 'ri-sun-line';
                text.textContent = '亮色模式';
            }
        }
    }
};

document.addEventListener('DOMContentLoaded', () => {
    RecruitmentManagement.init();
});

window.RecruitmentManagement = RecruitmentManagement;
