/**
 * NexusAPI - API请求模块
 */

var NexusAPI = {
    baseUrl: '',
    
    init: function(baseUrl) {
        this.baseUrl = baseUrl || '';
    },
    
    request: function(method, url, data, callback) {
        var self = this;
        var options = {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            }
        };
        
        if (data && (method === 'POST' || method === 'PUT' || method === 'PATCH')) {
            options.body = JSON.stringify(data);
        }
        
        fetch(this.baseUrl + url, options)
            .then(function(response) {
                if (!response.ok) {
                    throw new Error('HTTP error! status: ' + response.status);
                }
                return response.json();
            })
            .then(function(result) {
                if (callback) {
                    callback(result);
                }
            })
            .catch(function(error) {
                console.error('API Error:', error);
                if (callback) {
                    callback({
                        status: 'error',
                        message: error.message,
                        data: null
                    });
                }
            });
    },
    
    get: function(url, callback) {
        this.request('GET', url, null, callback);
    },
    
    post: function(url, data, callback) {
        this.request('POST', url, data, callback);
    },
    
    put: function(url, data, callback) {
        this.request('PUT', url, data, callback);
    },
    
    delete: function(url, callback) {
        this.request('DELETE', url, null, callback);
    },
    
    patch: function(url, data, callback) {
        this.request('PATCH', url, data, callback);
    },
    
    upload: function(url, formData, callback) {
        var self = this;
        fetch(this.baseUrl + url, {
            method: 'POST',
            body: formData
        })
        .then(function(response) {
            if (!response.ok) {
                throw new Error('HTTP error! status: ' + response.status);
            }
            return response.json();
        })
        .then(function(result) {
            if (callback) {
                callback(result);
            }
        })
        .catch(function(error) {
            console.error('Upload Error:', error);
            if (callback) {
                callback({
                    status: 'error',
                    message: error.message,
                    data: null
                });
            }
        });
    }
};

if (typeof module !== 'undefined' && module.exports) {
    module.exports = NexusAPI;
}
