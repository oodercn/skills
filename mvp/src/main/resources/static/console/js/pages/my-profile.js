(function() {
    'use strict';

    var MyProfile = {
        currentUser: null,
        originalData: {},
        
        init: function() {
            this.loadUserProfile();
            this.loadUserStats();
            this.bindEvents();
        },
        
        bindEvents: function() {
            var self = this;
            document.getElementById('profileForm').addEventListener('submit', function(e) {
                e.preventDefault();
                self.saveProfile();
            });
        },
        
        loadUserProfile: function() {
            var self = this;
            fetch('/api/v1/org/users/current')
                .then(function(response) { return response.json(); })
                .then(function(result) {
                    if (result.status === 'success' && result.data) {
                        self.currentUser = result.data;
                        self.originalData = {
                            name: self.currentUser.name || '',
                            title: self.currentUser.title || '',
                            email: self.currentUser.email || '',
                            phone: self.currentUser.phone || ''
                        };
                        self.renderProfile(self.currentUser);
                    }
                })
                .catch(function(error) {
                    console.error('Failed to load user profile:', error);
                    self.showToast('加载用户信息失败', 'error');
                });
        },
        
        loadUserStats: function() {
            fetch('/api/v1/org/users/current/stats')
                .then(function(response) { return response.json(); })
                .then(function(result) {
                    if (result.status === 'success' && result.data) {
                        document.getElementById('statScenes').textContent = result.data.scenesCount || 0;
                        document.getElementById('statCapabilities').textContent = result.data.capabilitiesCount || 0;
                    }
                })
                .catch(function(error) {
                    console.error('Failed to load user stats:', error);
                });
        },
        
        renderProfile: function(user) {
            document.getElementById('profileAvatar').textContent = (user.name || 'U').charAt(0).toUpperCase();
            document.getElementById('profileName').textContent = user.name || '未知用户';
            
            var roleMap = {
                'admin': { name: '管理员', icon: 'ri-admin-line' },
                'manager': { name: '管理者', icon: 'ri-user-settings-line' },
                'leader': { name: '主导者', icon: 'ri-user-star-line' },
                'collaborator': { name: '协作者', icon: 'ri-team-line' },
                'installer': { name: '安装者', icon: 'ri-install-line' },
                'employee': { name: '员工', icon: 'ri-user-line' }
            };
            var roleInfo = roleMap[user.role] || { name: user.role || '用户', icon: 'ri-shield-user-line' };
            document.getElementById('profileRole').innerHTML = '<i class="' + roleInfo.icon + '"></i><span>' + roleInfo.name + '</span>';
            
            document.getElementById('infoUserId').textContent = user.userId || '-';
            document.getElementById('infoCreateTime').textContent = this.formatTime(user.createTime);
            document.getElementById('infoUpdateTime').textContent = this.formatTime(user.updateTime);
            
            document.getElementById('formName').value = user.name || '';
            document.getElementById('formTitle').value = user.title || '';
            document.getElementById('formEmail').value = user.email || '';
            document.getElementById('formPhone').value = user.phone || '';
            document.getElementById('formDepartment').value = user.departmentId || '-';
            document.getElementById('formRole').value = roleInfo.name;
        },
        
        formatTime: function(timestamp) {
            if (!timestamp) return '-';
            var date = new Date(timestamp);
            return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
        },
        
        saveProfile: function() {
            var self = this;
            if (!this.currentUser) return;
            
            var updatedData = {
                name: document.getElementById('formName').value,
                title: document.getElementById('formTitle').value,
                email: document.getElementById('formEmail').value,
                phone: document.getElementById('formPhone').value
            };
            
            fetch('/api/v1/org/users/' + this.currentUser.userId, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(updatedData)
            })
                .then(function(response) { return response.json(); })
                .then(function(result) {
                    if (result.status === 'success') {
                        self.currentUser = result.data;
                        self.originalData = {
                            name: self.currentUser.name || '',
                            title: self.currentUser.title || '',
                            email: self.currentUser.email || '',
                            phone: self.currentUser.phone || ''
                        };
                        self.renderProfile(self.currentUser);
                        self.showToast('保存成功', 'success');
                    } else {
                        self.showToast(result.message || '保存失败', 'error');
                    }
                })
                .catch(function(error) {
                    console.error('Failed to save profile:', error);
                    self.showToast('保存失败', 'error');
                });
        },
        
        resetForm: function() {
            if (!this.currentUser) return;
            document.getElementById('formName').value = this.originalData.name;
            document.getElementById('formTitle').value = this.originalData.title;
            document.getElementById('formEmail').value = this.originalData.email;
            document.getElementById('formPhone').value = this.originalData.phone;
        },
        
        showPasswordModal: function() {
            this.showToast('密码修改功能开发中', 'error');
        },
        
        showDevicesModal: function() {
            this.showToast('设备管理功能开发中', 'error');
        },
        
        showToast: function(message, type) {
            var toast = document.getElementById('toast');
            toast.textContent = message;
            toast.className = 'profile-toast ' + type;
            toast.classList.add('show');
            
            setTimeout(function() {
                toast.classList.remove('show');
            }, 3000);
        }
    };

    window.MyProfile = MyProfile;
    window.resetForm = function() { MyProfile.resetForm(); };
    window.showPasswordModal = function() { MyProfile.showPasswordModal(); };
    window.showDevicesModal = function() { MyProfile.showDevicesModal(); };
    
    document.addEventListener('DOMContentLoaded', function() {
        MyProfile.init();
    });
})();
