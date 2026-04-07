class Toolbar {
    constructor(container, store, api) {
        this.container = container;
        this.store = store;
        this.api = api;
        this._bindEvents();
    }

    _bindEvents() {
        this.container.querySelector('#btnUndo')?.addEventListener('click', () => {
            this.store.undo();
        });

        this.container.querySelector('#btnRedo')?.addEventListener('click', () => {
            this.store.redo();
        });

        this.container.querySelector('#btnZoomIn')?.addEventListener('click', () => {
            if (window.app?.canvas) {
                window.app.canvas.zoomIn();
            }
        });

        this.container.querySelector('#btnZoomOut')?.addEventListener('click', () => {
            if (window.app?.canvas) {
                window.app.canvas.zoomOut();
            }
        });

        this.container.querySelector('#btnFit')?.addEventListener('click', () => {
            if (window.app?.canvas) {
                window.app.canvas.fitToScreen();
            }
        });

        this.container.querySelector('#btnSave')?.addEventListener('click', () => {
            this._save();
        });

        this.container.querySelector('#btnImport')?.addEventListener('click', () => {
            this._showImportModal();
        });

        this.container.querySelector('#btnExport')?.addEventListener('click', () => {
            this._showExportModal();
        });

        this.store.on('dirty:change', (dirty) => {
            const saveBtn = this.container.querySelector('#btnSave');
            if (saveBtn) {
                saveBtn.disabled = !dirty;
            }
        });

        this.store.on('process:change', (process) => {
            const nameEl = this.container.querySelector('#processName');
            if (nameEl && process) {
                nameEl.textContent = process.name;
            }
        });
    }

    async _save() {
        const process = this.store.getProcess();
        if (!process) {
            this._toast('没有流程可保存', 'warning');
            return;
        }

        try {
            await this.api.saveProcess(process.toJSON());
            this.store.setDirty(false);
            this._toast('保存成功', 'success');
        } catch (error) {
            console.error('[Toolbar] Save failed:', error);
            this._toast('保存失败: ' + error.message, 'error');
        }
    }

    _showImportModal() {
        const modal = document.getElementById('modalYaml');
        const textarea = document.getElementById('yamlContent');
        const confirmBtn = document.getElementById('btnYamlConfirm');
        const cancelBtn = document.getElementById('btnYamlCancel');
        const closeBtn = document.getElementById('btnModalClose');

        if (!modal || !textarea) return;

        textarea.value = '';
        modal.classList.add('show');

        const closeModal = () => {
            modal.classList.remove('show');
        };

        const handleConfirm = () => {
            const yaml = textarea.value.trim();
            if (yaml) {
                try {
                    const process = ProcessDef.fromYAML(yaml);
                    this.store.setProcess(process);
                    if (window.app?.canvas) {
                        window.app.canvas.loadProcess(process);
                    }
                    this._toast('导入成功', 'success');
                } catch (error) {
                    this._toast('导入失败: ' + error.message, 'error');
                }
            }
            closeModal();
        };

        confirmBtn?.addEventListener('click', handleConfirm, { once: true });
        cancelBtn?.addEventListener('click', closeModal, { once: true });
        closeBtn?.addEventListener('click', closeModal, { once: true });
    }

    _showExportModal() {
        const process = this.store.getProcess();
        if (!process) {
            this._toast('没有流程可导出', 'warning');
            return;
        }

        const modal = document.getElementById('modalYaml');
        const textarea = document.getElementById('yamlContent');
        const confirmBtn = document.getElementById('btnYamlConfirm');
        const cancelBtn = document.getElementById('btnYamlCancel');
        const closeBtn = document.getElementById('btnModalClose');

        if (!modal || !textarea) return;

        textarea.value = process.toYAML();
        modal.classList.add('show');

        confirmBtn.textContent = '复制';
        confirmBtn.onclick = () => {
            textarea.select();
            document.execCommand('copy');
            this._toast('已复制到剪贴板', 'success');
        };

        const closeModal = () => {
            modal.classList.remove('show');
            confirmBtn.textContent = '确认';
            confirmBtn.onclick = null;
        };

        cancelBtn?.addEventListener('click', closeModal, { once: true });
        closeBtn?.addEventListener('click', closeModal, { once: true });
    }

    _toast(message, type) {
        const toast = document.getElementById('toast');
        if (!toast) return;

        toast.textContent = message;
        toast.className = 'd-toast show ' + type;

        setTimeout(() => {
            toast.classList.remove('show');
        }, 3000);
    }
}

window.Toolbar = Toolbar;
