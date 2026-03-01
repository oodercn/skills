class StorageApi {
    static async getStorageInfo() {
        try {
            const response = await fetch('/api/test/storage/info');
            return await response.json();
        } catch (e) {
            return {
                total: '500GB',
                used: '125GB',
                free: '375GB',
                usedPercent: 25
            };
        }
    }
    
    static async getFiles(path = '/') {
        try {
            const response = await fetch(`/api/test/storage/files?path=${path}`);
            return await response.json();
        } catch (e) {
            return [
                { name: 'documents', type: 'folder', size: '-', modified: '2024-01-15' },
                { name: 'images', type: 'folder', size: '-', modified: '2024-01-14' },
                { name: 'readme.txt', type: 'file', size: '2KB', modified: '2024-01-13' }
            ];
        }
    }
}

window.StorageApi = StorageApi;
