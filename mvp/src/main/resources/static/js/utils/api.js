/**
 * MVP API 工具类
 */

const ApiClient = {
    baseUrl: '',
    
    async request(method, path, data = null) {
        const options = {
            method,
            headers: {
                'Content-Type': 'application/json'
            }
        };
        
        const token = localStorage.getItem('token');
        if (token) {
            options.headers['Authorization'] = `Bearer ${token}`;
        }
        
        if (data && (method === 'POST' || method === 'PUT')) {
            options.body = JSON.stringify(data);
        }
        
        try {
            const response = await fetch(this.baseUrl + path, options);
            const result = await response.json();
            return result;
        } catch (e) {
            console.error('API Error:', e);
            return { status: 'error', message: '请求失败: ' + e.message };
        }
    },
    
    get(path) {
        return this.request('GET', path);
    },
    
    post(path, data) {
        return this.request('POST', path, data);
    },
    
    put(path, data) {
        return this.request('PUT', path, data);
    },
    
    delete(path) {
        return this.request('DELETE', path);
    }
};

const AuthApi = {
    async login(username, password, role) {
        return ApiClient.post('/api/v1/auth/login', { username, password, role });
    },
    
    async logout() {
        return ApiClient.post('/api/v1/auth/logout');
    },
    
    async getCurrentUser() {
        return ApiClient.get('/api/v1/auth/current-user');
    },
    
    async getRoles() {
        return ApiClient.get('/api/v1/auth/roles');
    },
    
    async checkPermission(permission) {
        return ApiClient.get('/api/v1/auth/check-permission?permission=' + permission);
    }
};

const OrgApi = {
    async getUsers(departmentId, role) {
        let path = '/api/v1/org/users';
        const params = [];
        if (departmentId) params.push('departmentId=' + departmentId);
        if (role) params.push('role=' + role);
        if (params.length > 0) path += '?' + params.join('&');
        return ApiClient.get(path);
    },
    
    async getUser(userId) {
        return ApiClient.get('/api/v1/org/users/' + userId);
    },
    
    async createUser(user) {
        return ApiClient.post('/api/v1/org/users', user);
    },
    
    async updateUser(userId, user) {
        return ApiClient.put('/api/v1/org/users/' + userId, user);
    },
    
    async deleteUser(userId) {
        return ApiClient.delete('/api/v1/org/users/' + userId);
    },
    
    async getDepartments() {
        return ApiClient.get('/api/v1/org/departments');
    },
    
    async getOrgTree() {
        return ApiClient.get('/api/v1/org/tree');
    },
    
    async getRoles() {
        return ApiClient.get('/api/v1/org/roles');
    }
};

const SystemApi = {
    async getConfig() {
        return ApiClient.get('/api/v1/system/config');
    },
    
    async getHealth() {
        return ApiClient.get('/api/v1/mvp/health');
    },
    
    async getInfo() {
        return ApiClient.get('/api/v1/mvp/info');
    },
    
    async getProfile() {
        return ApiClient.get('/api/v1/mvp/profile');
    }
};
