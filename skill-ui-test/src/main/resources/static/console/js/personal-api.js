class PersonalApi {
    static async getStats() {
        try {
            const response = await fetch('/api/test/stats');
            return await response.json();
        } catch (e) {
            return {
                skillCount: 5,
                executionCount: 128,
                sharedCount: 3,
                groupCount: 2
            };
        }
    }
    
    static async getActivities() {
        return [
            { icon: 'ri-check-line', text: '系统状态检查完成', time: '2分钟前' },
            { icon: 'ri-refresh-line', text: '健康检查执行', time: '5分钟前' },
            { icon: 'ri-download-line', text: '存储管理技能安装', time: '1小时前' }
        ];
    }
}

window.PersonalApi = PersonalApi;
