/**
 * Scene Management Page Script
 * 场景管理页面脚本
 */

(function() {
    'use strict';

    const SceneManagement = {
        initialized: false,
        scenes: [],

        async init() {
            if (this.initialized) return;
            
            console.log('[SceneManagement] Initializing...');
            
            this.bindEvents();
            await this.loadScenes();
            
            this.initialized = true;
            console.log('[SceneManagement] Initialized');
        },

        bindEvents() {
            const refreshBtn = document.querySelector('[onclick="refreshScenes()"]');
            if (refreshBtn) {
                refreshBtn.removeAttribute('onclick');
                refreshBtn.addEventListener('click', () => this.loadScenes());
            }
        },

        async loadScenes() {
            try {
                const response = await fetch('/api/v1/scenes');
                if (!response.ok) {
                    console.warn('[SceneManagement] Scenes API not available');
                    this.renderEmptyState();
                    return;
                }
                const result = await response.json();
                
                if (result.status === 'success') {
                    this.scenes = result.data || [];
                    this.renderScenes(this.scenes);
                    this.updateStats(this.scenes);
                } else {
                    this.renderEmptyState();
                }
            } catch (e) {
                console.warn('[SceneManagement] Failed to load scenes:', e);
                this.renderEmptyState();
            }
        },

        updateStats(scenes) {
            const totalEl = document.getElementById('totalScenes');
            const activeEl = document.getElementById('activeScenes');
            const typesEl = document.getElementById('sceneTypes');
            
            if (totalEl) totalEl.textContent = scenes.length;
            
            const activeCount = scenes.filter(s => s.status === 'ACTIVE').length;
            if (activeEl) activeEl.textContent = activeCount;
            
            const types = new Set(scenes.map(s => s.type).filter(Boolean));
            if (typesEl) typesEl.textContent = types.size;
        },

        renderScenes(scenes) {
            const tbody = document.getElementById('sceneTableBody');
            if (!tbody) return;

            if (scenes.length === 0) {
                this.renderEmptyState();
                return;
            }

            tbody.innerHTML = scenes.map(scene => `
                <tr>
                    <td>
                        <div style="display: flex; align-items: center; gap: 8px;">
                            <i class="ri-artboard-line" style="color: var(--nx-primary);"></i>
                            <span>${scene.name || '未命名'}</span>
                        </div>
                    </td>
                    <td>${this.getTypeLabel(scene.type)}</td>
                    <td><span class="nx-badge nx-badge--${this.getStatusBadge(scene.status)}">${this.getStatusLabel(scene.status)}</span></td>
                    <td>${(scene.capabilities || []).length}</td>
                    <td>${this.formatDate(scene.createdAt)}</td>
                    <td>
                        <div class="nx-flex nx-gap-2">
                            <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="startScene('${scene.sceneId}')" title="启动">
                                <i class="ri-play-line"></i>
                            </button>
                            <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="editScene('${scene.sceneId}')" title="编辑">
                                <i class="ri-edit-line"></i>
                            </button>
                            <button class="nx-btn nx-btn--ghost nx-btn--sm nx-btn--danger" onclick="deleteScene('${scene.sceneId}')" title="删除">
                                <i class="ri-delete-bin-line"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `).join('');
        },

        renderEmptyState() {
            const tbody = document.getElementById('sceneTableBody');
            if (!tbody) return;

            tbody.innerHTML = `
                <tr>
                    <td colspan="6" style="text-align: center; padding: 40px;">
                        <div style="color: var(--nx-text-tertiary);">
                            <i class="ri-folder-open-line" style="font-size: 48px; margin-bottom: 16px; display: block;"></i>
                            <p>暂无场景数据</p>
                            <p style="font-size: 12px;">点击"创建场景"按钮添加新场景</p>
                        </div>
                    </td>
                </tr>
            `;
        },

        getTypeLabel(type) {
            const types = {
                'automation': '自动化',
                'collaboration': '协作',
                'knowledge': '知识库',
                'enterprise': '企业',
                'personal': '个人',
                'test': '测试',
                'development': '开发'
            };
            return types[type] || type || '未分类';
        },

        getStatusLabel(status) {
            const labels = {
                'DRAFT': '草稿',
                'ACTIVE': '活跃',
                'PAUSED': '暂停',
                'COMPLETED': '已完成',
                'ARCHIVED': '已归档'
            };
            return labels[status] || status || '未知';
        },

        getStatusBadge(status) {
            const badges = {
                'DRAFT': 'default',
                'ACTIVE': 'success',
                'PAUSED': 'warning',
                'COMPLETED': 'info',
                'ARCHIVED': 'default'
            };
            return badges[status] || 'default';
        },

        formatDate(dateStr) {
            if (!dateStr) return '-';
            const date = new Date(dateStr);
            return date.toLocaleDateString('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            });
        }
    };

    window.SceneManagement = SceneManagement;

    window.refreshScenes = function() {
        SceneManagement.loadScenes();
    };

    window.createScene = function() {
        const modal = document.getElementById('sceneModal');
        const title = document.getElementById('modalTitle');
        if (modal && title) {
            title.textContent = '创建场景';
            modal.style.display = 'flex';
        }
    };

    window.closeModal = function() {
        const modal = document.getElementById('sceneModal');
        if (modal) {
            modal.style.display = 'none';
        }
    };

    window.saveScene = async function() {
        const name = document.getElementById('sceneName').value;
        const type = document.getElementById('sceneType').value;
        const description = document.getElementById('sceneDescription').value;

        if (!name) {
            alert('请输入场景名称');
            return;
        }

        try {
            const response = await fetch('/api/v1/scenes', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, type, description })
            });

            if (response.ok) {
                closeModal();
                SceneManagement.loadScenes();
            } else {
                alert('创建失败');
            }
        } catch (e) {
            alert('创建失败: ' + e.message);
        }
    };

    window.startScene = async function(sceneId) {
        try {
            const response = await fetch(`/api/v1/scenes/${sceneId}/start`, { method: 'POST' });
            if (response.ok) {
                SceneManagement.loadScenes();
            }
        } catch (e) {
            alert('启动失败');
        }
    };

    window.editScene = function(sceneId) {
        alert('编辑场景: ' + sceneId + ' (功能开发中)');
    };

    window.deleteScene = async function(sceneId) {
        if (!confirm('确定要删除此场景吗？')) return;
        
        try {
            const response = await fetch(`/api/v1/scenes/${sceneId}`, { method: 'DELETE' });
            if (response.ok) {
                SceneManagement.loadScenes();
            }
        } catch (e) {
            alert('删除失败');
        }
    };

    document.addEventListener('DOMContentLoaded', () => {
        SceneManagement.init();
    });

})();
