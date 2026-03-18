/**
 * 录音质检系统 JavaScript
 */
const RecordingQA = {
    // 评分标准
    scoringStandard: [],
    
    // 当前录音列表
    recordings: [],
    
    // 分页信息
    pagination: {
        page: 1,
        size: 20,
        total: 0
    },
    
    // 音频播放器
    audioPlayer: null,
    
    // 当前审核的录音ID
    currentRecordingId: null,
    
    // ========== 初始化 ==========
    
    init() {
        this.loadScoringStandard();
    },
    
    initUpload() {
        this.init();
        this.initUploadEvents();
        this.loadRecordings();
    },
    
    initReview() {
        this.init();
        this.loadRecordings();
    },
    
    initStatistics() {
        this.loadStatistics();
    },
    
    // ========== 事件绑定 ==========
    
    initUploadEvents() {
        const uploadArea = document.getElementById('upload-area');
        const fileInput = document.getElementById('file-input');
        const uploadForm = document.getElementById('upload-form');
        
        if (uploadArea && fileInput) {
            // 点击上传区域选择文件
            uploadArea.addEventListener('click', () => fileInput.click());
            
            // 文件选择变化
            fileInput.addEventListener('change', (e) => {
                if (e.target.files.length > 0) {
                    this.handleFileSelect(e.target.files[0]);
                }
            });
            
            // 拖拽事件
            uploadArea.addEventListener('dragover', (e) => {
                e.preventDefault();
                uploadArea.classList.add('dragover');
            });
            
            uploadArea.addEventListener('dragleave', () => {
                uploadArea.classList.remove('dragover');
            });
            
            uploadArea.addEventListener('drop', (e) => {
                e.preventDefault();
                uploadArea.classList.remove('dragover');
                const files = e.dataTransfer.files;
                if (files.length > 0) {
                    fileInput.files = files;
                    this.handleFileSelect(files[0]);
                }
            });
        }
        
        // 表单提交
        if (uploadForm) {
            uploadForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.uploadRecording();
            });
        }
    },
    
    // ========== API 调用 ==========
    
    async loadScoringStandard() {
        try {
            const result = await this.promisify(NexusAPI, 'get', '/api/recording-qa/scoring-standard');
            if (result.status === 'success') {
                this.scoringStandard = result.data;
            }
        } catch (error) {
            console.error('Failed to load scoring standard:', error);
            // 使用默认评分标准
            this.scoringStandard = [
                { id: 'greeting', name: '开场白', weight: 10, maxScore: 10 },
                { id: 'communication', name: '沟通技巧', weight: 30, maxScore: 30 },
                { id: 'professionalism', name: '专业度', weight: 25, maxScore: 25 },
                { id: 'problemSolving', name: '问题解决', weight: 25, maxScore: 25 },
                { id: 'closing', name: '结束语', weight: 10, maxScore: 10 }
            ];
        }
    },
    
    async loadRecordings() {
        try {
            const status = document.getElementById('filter-status')?.value || '';
            const department = document.getElementById('filter-department')?.value || '';
            const keyword = document.getElementById('filter-keyword')?.value || '';
            
            const params = new URLSearchParams();
            if (status) params.append('status', status);
            if (department) params.append('department', department);
            if (keyword) params.append('keyword', keyword);
            params.append('page', this.pagination.page);
            params.append('size', this.pagination.size);
            
            const result = await this.promisify(NexusAPI, 'get', `/api/recording-qa/recordings?${params}`);
            
            if (result.status === 'success') {
                this.recordings = result.data.list;
                this.pagination.total = result.data.total;
                this.renderRecordingList();
                this.renderPagination();
            }
        } catch (error) {
            console.error('Failed to load recordings:', error);
            this.showToast('加载录音列表失败', 'error');
        }
    },
    
    async loadStatistics() {
        try {
            const result = await this.promisify(NexusAPI, 'get', '/api/recording-qa/statistics');
            
            if (result.status === 'success') {
                const data = result.data;
                
                // 更新统计卡片
                document.getElementById('stat-total').textContent = data.total;
                document.getElementById('stat-pending').textContent = data.pending;
                document.getElementById('stat-completed').textContent = data.completed;
                document.getElementById('stat-avg-score').textContent = data.averageScore;
                
                // 渲染图表
                this.renderCharts(data);
                
                // 渲染详情表格
                this.renderStatisticsTable(data);
            }
        } catch (error) {
            console.error('Failed to load statistics:', error);
        }
    },
    
    handleFileSelect(file) {
        // 获取音频时长
        const audio = new Audio();
        audio.src = URL.createObjectURL(file);
        audio.onloadedmetadata = () => {
            const duration = Math.round(audio.duration);
            const minutes = Math.floor(duration / 60);
            const seconds = duration % 60;
            document.getElementById('duration').value = `${minutes}分${seconds}秒`;
            URL.revokeObjectURL(audio.src);
        };
    },
    
    async uploadRecording() {
        const fileInput = document.getElementById('file-input');
        const file = fileInput?.files[0];
        
        if (!file) {
            this.showToast('请选择录音文件', 'error');
            return;
        }
        
        const agentName = document.getElementById('agent-name').value;
        const agentId = document.getElementById('agent-id').value;
        const department = document.getElementById('department').value;
        const callType = document.getElementById('call-type').value;
        const customerPhone = document.getElementById('customer-phone').value;
        
        if (!agentName || !agentId || !department || !callType) {
            this.showToast('请填写完整信息', 'error');
            return;
        }
        
        const formData = new FormData();
        formData.append('file', file);
        formData.append('agentId', agentId);
        formData.append('agentName', agentName);
        formData.append('department', department);
        formData.append('callType', callType);
        formData.append('customerPhone', customerPhone);
        
        try {
            const submitBtn = document.getElementById('submit-btn');
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="ri-loader-4-line ri-spin"></i> 上传中...';
            
            const result = await this.promisify(NexusAPI, 'post', '/api/recording-qa/upload', formData);
            
            if (result.status === 'success') {
                this.showToast('上传成功', 'success');
                document.getElementById('upload-form').reset();
                fileInput.value = '';
                this.loadRecordings();
            } else {
                this.showToast(result.message || '上传失败', 'error');
            }
        } catch (error) {
            console.error('Upload error:', error);
            this.showToast('上传失败', 'error');
        } finally {
            const submitBtn = document.getElementById('submit-btn');
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i class="ri-upload-line"></i><span>开始上传</span>';
        }
    },
    
    // ========== 审核功能 ==========
    
    openReviewModal(recordingId) {
        const recording = this.recordings.find(r => r.id === recordingId);
        if (!recording) return;
        
        this.currentRecordingId = recordingId;
        
        // 填充录音信息
        document.getElementById('review-agent-name').textContent = recording.agentName;
        document.getElementById('review-agent-id').textContent = recording.agentId;
        document.getElementById('review-department').textContent = recording.department;
        document.getElementById('review-call-type').textContent = recording.callType;
        document.getElementById('review-customer-phone').textContent = recording.customerPhone || '-';
        
        const minutes = Math.floor((recording.duration || 0) / 60);
        const seconds = (recording.duration || 0) % 60;
        document.getElementById('review-duration').textContent = `${minutes}分${seconds}秒`;
        
        // 设置音频源
        this.audioPlayer = document.getElementById('audio-player');
        // 这里应该设置真实的音频URL
        // this.audioPlayer.src = `/api/recording-qa/recordings/${recordingId}/audio`;
        
        // 渲染评分项
        this.renderScoringItems();
        
        // 显示弹窗
        document.getElementById('review-modal').classList.add('active');
        
        // 绑定表单提交
        document.getElementById('review-form').onsubmit = (e) => {
            e.preventDefault();
            this.submitReview();
        };
    },
    
    closeReviewModal() {
        document.getElementById('review-modal').classList.remove('active');
        if (this.audioPlayer) {
            this.audioPlayer.pause();
            this.audioPlayer.src = '';
        }
        this.currentRecordingId = null;
    },
    
    renderScoringItems() {
        const container = document.getElementById('scoring-items');
        container.innerHTML = this.scoringStandard.map(item => `
            <div class="scoring-item" data-id="${item.id}">
                <label>${item.name}</label>
                <input type="number" class="score-input" 
                       min="0" max="${item.maxScore}" value="${item.maxScore}"
                       onchange="RecordingQA.calculateTotal()">
                <span class="score-max">/${item.maxScore}</span>
                <input type="text" class="comment-input" placeholder="评语（可选）">
            </div>
        `).join('');
        
        this.calculateTotal();
    },
    
    calculateTotal() {
        const items = document.querySelectorAll('.scoring-item');
        let total = 0;
        
        items.forEach(item => {
            const input = item.querySelector('.score-input');
            total += parseInt(input.value) || 0;
        });
        
        document.getElementById('total-score').textContent = total;
    },
    
    async submitReview() {
        if (!this.currentRecordingId) return;
        
        const items = document.querySelectorAll('.scoring-item');
        const scores = [];
        
        items.forEach(item => {
            const id = item.dataset.id;
            const standard = this.scoringStandard.find(s => s.id === id);
            scores.push({
                id: id,
                name: standard.name,
                actualScore: parseInt(item.querySelector('.score-input').value) || 0,
                comment: item.querySelector('.comment-input').value
            });
        });
        
        const result = document.querySelector('input[name="review-result"]:checked').value;
        const comment = document.getElementById('review-comment').value;
        
        try {
            const response = await this.promisify(NexusAPI, 'post', 
                `/api/recording-qa/recordings/${this.currentRecordingId}/review`,
                { scores, result, comment }
            );
            
            if (response.status === 'success') {
                this.showToast('审核完成', 'success');
                this.closeReviewModal();
                this.loadRecordings();
            } else {
                this.showToast(response.message || '审核失败', 'error');
            }
        } catch (error) {
            console.error('Review error:', error);
            this.showToast('审核失败', 'error');
        }
    },
    
    // ========== 音频控制 ==========
    
    togglePlay() {
        if (!this.audioPlayer) return;
        
        const icon = document.getElementById('play-icon');
        const text = document.getElementById('play-text');
        
        if (this.audioPlayer.paused) {
            this.audioPlayer.play();
            icon.className = 'ri-pause-line';
            text.textContent = '暂停';
        } else {
            this.audioPlayer.pause();
            icon.className = 'ri-play-line';
            text.textContent = '播放';
        }
    },
    
    skipAudio(seconds) {
        if (!this.audioPlayer) return;
        this.audioPlayer.currentTime += seconds;
    },
    
    // ========== 渲染功能 ==========
    
    renderRecordingList() {
        const tbody = document.querySelector('#recording-list tbody') || document.querySelector('#upload-list tbody');
        if (!tbody) return;
        
        tbody.innerHTML = this.recordings.map(r => {
            const statusClass = {
                'pending': 'status-pending',
                'reviewing': 'status-reviewing',
                'completed': 'status-completed'
            }[r.status] || 'status-pending';
            
            const statusText = {
                'pending': '待审核',
                'reviewing': '审核中',
                'completed': '已完成'
            }[r.status] || '待审核';
            
            const minutes = Math.floor((r.duration || 0) / 60);
            const seconds = (r.duration || 0) % 60;
            const durationStr = `${minutes}:${seconds.toString().padStart(2, '0')}`;
            
            const sizeStr = this.formatFileSize(r.fileSize);
            
            return `
                <tr>
                    <td>${r.id}</td>
                    <td>${r.originalFileName}</td>
                    <td>${r.agentName}</td>
                    <td>${r.department}</td>
                    <td>${r.callType}</td>
                    <td>${durationStr}</td>
                    <td><span class="status-badge ${statusClass}">${statusText}</span></td>
                    <td>${r.totalScore || '-'}</td>
                    <td>${this.formatDate(r.uploadedAt)}</td>
                    <td>
                        <div class="action-btns">
                            ${r.status === 'pending' ? 
                                `<button class="action-btn review" onclick="RecordingQA.openReviewModal('${r.id}')">审核</button>` :
                                `<button class="action-btn view" onclick="RecordingQA.openReviewModal('${r.id}')">查看</button>`
                            }
                            <button class="action-btn delete" onclick="RecordingQA.deleteRecording('${r.id}')">删除</button>
                        </div>
                    </td>
                </tr>
            `;
        }).join('');
    },
    
    renderPagination() {
        const container = document.getElementById('pagination');
        if (!container) return;
        
        const totalPages = Math.ceil(this.pagination.total / this.pagination.size);
        if (totalPages <= 1) {
            container.innerHTML = '';
            return;
        }
        
        let html = '';
        
        // 上一页
        html += `<button class="page-btn" onclick="RecordingQA.goToPage(${this.pagination.page - 1})" ${this.pagination.page === 1 ? 'disabled' : ''}>上一页</button>`;
        
        // 页码
        for (let i = 1; i <= totalPages; i++) {
            if (i === 1 || i === totalPages || (i >= this.pagination.page - 2 && i <= this.pagination.page + 2)) {
                html += `<button class="page-btn ${i === this.pagination.page ? 'active' : ''}" onclick="RecordingQA.goToPage(${i})">${i}</button>`;
            } else if (i === this.pagination.page - 3 || i === this.pagination.page + 3) {
                html += `<span>...</span>`;
            }
        }
        
        // 下一页
        html += `<button class="page-btn" onclick="RecordingQA.goToPage(${this.pagination.page + 1})" ${this.pagination.page === totalPages ? 'disabled' : ''}>下一页</button>`;
        
        container.innerHTML = html;
    },
    
    renderCharts(data) {
        // 审核结果分布图
        const resultCtx = document.getElementById('result-chart');
        if (resultCtx) {
            new Chart(resultCtx, {
                type: 'doughnut',
                data: {
                    labels: ['通过', '不通过'],
                    datasets: [{
                        data: [data.pass, data.fail],
                        backgroundColor: ['#22c55e', '#ef4444']
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false
                }
            });
        }
        
        // 部门统计图
        const deptCtx = document.getElementById('dept-chart');
        if (deptCtx && data.departmentStats) {
            const labels = Object.keys(data.departmentStats);
            const values = Object.values(data.departmentStats);
            
            new Chart(deptCtx, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: '录音数量',
                        data: values,
                        backgroundColor: '#3b82f6'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false
                }
            });
        }
    },
    
    renderStatisticsTable(data) {
        const tbody = document.querySelector('#statistics-table tbody');
        if (!tbody || !data.departmentStats) return;
        
        tbody.innerHTML = Object.entries(data.departmentStats).map(([dept, count]) => `
            <tr>
                <td>${dept}</td>
                <td>${count}</td>
                <td>-</td>
                <td>-</td>
                <td>-</td>
                <td>
                    <button class="action-btn view" onclick="RecordingQA.viewDepartmentDetail('${dept}')">详情</button>
                </td>
            </tr>
        `).join('');
    },
    
    // ========== 工具方法 ==========
    
    goToPage(page) {
        if (page < 1 || page > Math.ceil(this.pagination.total / this.pagination.size)) return;
        this.pagination.page = page;
        this.loadRecordings();
    },
    
    async deleteRecording(id) {
        if (!confirm('确定要删除这条录音吗？')) return;
        
        try {
            const result = await this.promisify(NexusAPI, 'delete', `/api/recording-qa/recordings/${id}`);
            if (result.status === 'success') {
                this.showToast('删除成功', 'success');
                this.loadRecordings();
            } else {
                this.showToast(result.message || '删除失败', 'error');
            }
        } catch (error) {
            console.error('Delete error:', error);
            this.showToast('删除失败', 'error');
        }
    },
    
    viewDepartmentDetail(department) {
        // 跳转到审核页面并筛选
        window.location.href = `review.html?department=${encodeURIComponent(department)}`;
    },
    
    formatFileSize(bytes) {
        if (!bytes) return '0 B';
        const k = 1024;
        const sizes = ['B', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    },
    
    formatDate(dateStr) {
        if (!dateStr) return '-';
        const date = new Date(dateStr);
        return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`;
    },
    
    showToast(message, type = 'info') {
        // 简单的提示实现
        alert(message);
    },
    
    promisify(obj, method, ...args) {
        return new Promise((resolve, reject) => {
            obj[method](...args, (result) => {
                resolve(result || { status: 'success', data: null });
            });
        });
    }
};

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    RecordingQA.init();
});
