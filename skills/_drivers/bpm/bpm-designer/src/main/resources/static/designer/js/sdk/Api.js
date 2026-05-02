class Api {
    constructor(config) {
        this.baseUrl = config?.baseUrl || '/api/processdef';
        this.timeout = config?.timeout || 30000;
    }

    async request(method, endpoint, data) {
        const url = this.baseUrl + endpoint;
        const options = {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            }
        };

        if (data && (method === 'POST' || method === 'PUT')) {
            const body = JSON.stringify(data);
            console.log('[Api] Request body:', body.substring(0, 2000));
            options.body = body;
        }

        try {
            const response = await fetch(url, options);
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return await response.json();
        } catch (error) {
            console.error('[Api] Request failed:', error);
            throw error;
        }
    }

    async getProcess(processId, version) {
        const ver = version || 'latest';
        return this.request('GET', `/process/${processId}/version/${ver}`);
    }

    async saveProcess(processDef) {
        return this.request('POST', '/process', processDef);
    }

    async updateProcess(processId, processDef) {
        return this.request('PUT', `/process/${processId}`, processDef);
    }

    async deleteProcess(processId) {
        return this.request('DELETE', `/process/${processId}`);
    }

    async getProcessList(params) {
        const query = new URLSearchParams(params).toString();
        return this.request('GET', `/process?${query}`);
    }

    async exportYaml(processId) {
        return this.request('GET', `/process/${processId}/export/yaml`);
    }

    async importYaml(yamlContent) {
        return this.request('POST', '/process/import/yaml', { yaml: yamlContent });
    }

    async getActivity(processId, activityId) {
        return this.request('GET', `/process/${processId}/activity/${activityId}`);
    }

    async saveActivity(processId, activityDef) {
        return this.request('POST', `/process/${processId}/activity`, activityDef);
    }

    async updateActivity(processId, activityId, activityDef) {
        return this.request('PUT', `/process/${processId}/activity/${activityId}`, activityDef);
    }

    async deleteActivity(processId, activityId) {
        return this.request('DELETE', `/process/${processId}/activity/${activityId}`);
    }

    async activateVersion(processId, version) {
        return this.request('POST', `/process/${processId}/version/${version}/activate`);
    }

    async freezeVersion(processId, version) {
        return this.request('POST', `/process/${processId}/version/${version}/freeze`);
    }

    async deleteVersion(processId, version) {
        return this.request('DELETE', `/process/${processId}/version/${version}`);
    }

    async getAgent(agentId) {
        return this.request('GET', `/agent/${agentId}`);
    }

    async saveAgent(agentDef) {
        return this.request('POST', '/agent', agentDef);
    }

    async getCapabilities() {
        return this.request('GET', '/agent/capabilities');
    }

    async getScene(sceneId) {
        return this.request('GET', `/scene/${sceneId}`);
    }

    async saveScene(sceneDef) {
        return this.request('POST', '/scene', sceneDef);
    }
}

const ApiFactory = {
    _api: null,
    create: function(config) {
        if (!this._api) {
            this._api = new Api(config);
        }
        return this._api;
    },
    get: function() {
        return this._api || this.create();
    }
};

window.Api = Api;
window.ApiFactory = ApiFactory;
