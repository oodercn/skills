/**
 * Personal API - 个人中心 API 封装
 */

(function() {
    'use strict';

    const PersonalAPI = {
        async getMySkills() {
            try {
                const response = await fetch('/api/installed-skills');
                if (!response.ok) throw new Error('获取技能列表失败');
                const result = await response.json();
                return result.data || [];
            } catch (error) {
                console.error('[PersonalAPI] getMySkills error:', error);
                return [];
            }
        },

        async getMyGroups() {
            try {
                const response = await fetch('/api/groups');
                if (!response.ok) throw new Error('获取群组列表失败');
                const result = await response.json();
                return result.data || [];
            } catch (error) {
                console.error('[PersonalAPI] getMyGroups error:', error);
                return [];
            }
        },

        async getExecutionHistory() {
            try {
                const response = await fetch('/api/executions');
                if (!response.ok) throw new Error('获取执行记录失败');
                const result = await response.json();
                return result.data || [];
            } catch (error) {
                console.error('[PersonalAPI] getExecutionHistory error:', error);
                return [];
            }
        },

        async getSharedSkills() {
            try {
                const response = await fetch('/api/shared-skills');
                if (!response.ok) throw new Error('获取分享技能失败');
                const result = await response.json();
                return result.data || [];
            } catch (error) {
                console.error('[PersonalAPI] getSharedSkills error:', error);
                return [];
            }
        },

        async getRecentActivities() {
            try {
                const response = await fetch('/api/activities');
                if (!response.ok) throw new Error('获取活动记录失败');
                const result = await response.json();
                return result.data || [];
            } catch (error) {
                console.error('[PersonalAPI] getRecentActivities error:', error);
                return [];
            }
        },

        async getStats() {
            try {
                const response = await fetch('/api/personal/stats');
                if (!response.ok) throw new Error('获取统计数据失败');
                const result = await response.json();
                return result.data || {
                    skillCount: 0,
                    executionCount: 0,
                    sharedCount: 0,
                    groupCount: 0
                };
            } catch (error) {
                console.error('[PersonalAPI] getStats error:', error);
                return {
                    skillCount: 0,
                    executionCount: 0,
                    sharedCount: 0,
                    groupCount: 0
                };
            }
        }
    };

    window.PersonalAPI = PersonalAPI;

})();
