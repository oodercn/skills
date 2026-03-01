class StorageManagement {
    constructor() {
        this.currentPath = '/';
        this.init();
    }
    
    async init() {
        await this.loadStorageInfo();
        await this.loadFiles();
    }
    
    async loadStorageInfo() {
        const info = await StorageApi.getStorageInfo();
        const usedEl = document.getElementById('storage-used');
        const freeEl = document.getElementById('storage-free');
        const percentEl = document.getElementById('storage-percent');
        
        if (usedEl) usedEl.textContent = info.used;
        if (freeEl) freeEl.textContent = info.free;
        if (percentEl) percentEl.textContent = `${info.usedPercent}%`;
    }
    
    async loadFiles() {
        const files = await StorageApi.getFiles(this.currentPath);
        this.renderFiles(files);
    }
    
    renderFiles(files) {
        const tbody = document.getElementById('file-list');
        if (!tbody) return;
        
        tbody.innerHTML = files.map(file => `
            <tr>
                <td>
                    <i class="${file.type === 'folder' ? 'ri-folder-fill' : 'ri-file-line'}"></i>
                    ${file.name}
                </td>
                <td>${file.size}</td>
                <td>${file.modified}</td>
                <td>
                    <button class="nx-btn nx-btn--ghost nx-btn--sm" onclick="openItem('${file.name}', '${file.type}')">
                        ${file.type === 'folder' ? '打开' : '查看'}
                    </button>
                </td>
            </tr>
        `).join('');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new StorageManagement();
});
