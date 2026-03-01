const Api = {
    async request(url, options = {}) {
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json'
            }
        };
        
        const finalOptions = { ...defaultOptions, ...options };
        
        try {
            const response = await fetch(url, finalOptions);
            const data = await response.json();
            return data;
        } catch (error) {
            console.error('[Api] Request failed:', error);
            return {
                status: 'error',
                message: error.message,
                data: null
            };
        }
    },
    
    async get(url) {
        return this.request(url, { method: 'GET' });
    },
    
    async post(url, body) {
        return this.request(url, {
            method: 'POST',
            body: JSON.stringify(body)
        });
    },
    
    async put(url, body) {
        return this.request(url, {
            method: 'PUT',
            body: JSON.stringify(body)
        });
    },
    
    async delete(url) {
        return this.request(url, { method: 'DELETE' });
    }
};

window.Api = Api;
