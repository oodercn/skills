/**
 * NX Component Registry
 * 组件注册中心 - 提供统一的组件管理和 LLM 查询接口
 */

import LLMDescriptor from './llm-descriptor.js';

class ComponentRegistry {
  constructor() {
    this.components = new Map();
    this.indexes = {
      byType: new Map(),
      byRole: new Map(),
      byPage: new Map()
    };
  }

  register(component) {
    const { id, type, role, page } = component;
    
    this.components.set(id, component);
    
    if (type) {
      if (!this.indexes.byType.has(type)) {
        this.indexes.byType.set(type, new Set());
      }
      this.indexes.byType.get(type).add(id);
    }
    
    if (role) {
      if (!this.indexes.byRole.has(role)) {
        this.indexes.byRole.set(role, new Set());
      }
      this.indexes.byRole.get(role).add(id);
    }
    
    if (page) {
      if (!this.indexes.byPage.has(page)) {
        this.indexes.byPage.set(page, new Set());
      }
      this.indexes.byPage.get(page).add(id);
    }
  }

  unregister(id) {
    const component = this.components.get(id);
    if (!component) return;

    if (component.type && this.indexes.byType.has(component.type)) {
      this.indexes.byType.get(component.type).delete(id);
    }
    if (component.role && this.indexes.byRole.has(component.role)) {
      this.indexes.byRole.get(component.role).delete(id);
    }
    if (component.page && this.indexes.byPage.has(component.page)) {
      this.indexes.byPage.get(component.page).delete(id);
    }

    this.components.delete(id);
  }

  get(id) {
    return this.components.get(id);
  }

  getByType(type) {
    const ids = this.indexes.byType.get(type);
    if (!ids) return [];
    return Array.from(ids).map(id => this.components.get(id));
  }

  getByRole(role) {
    const ids = this.indexes.byRole.get(role);
    if (!ids) return [];
    return Array.from(ids).map(id => this.components.get(id));
  }

  getByPage(page) {
    const ids = this.indexes.byPage.get(page);
    if (!ids) return [];
    return Array.from(ids).map(id => this.components.get(id));
  }

  llm_list(options = {}) {
    const { type, role, page } = options;
    
    let components = Array.from(this.components.values());
    
    if (type) {
      components = components.filter(c => c.type === type);
    }
    if (role) {
      components = components.filter(c => c.role === role);
    }
    if (page) {
      components = components.filter(c => c.page === page);
    }

    return components.map(c => ({
      id: c.id,
      type: c.type,
      description: c.description
    }));
  }

  llm_describe(id, options = {}) {
    const component = this.components.get(id);
    if (!component) {
      return { error: `Component "${id}" not found` };
    }
    return LLMDescriptor.describe(component, options);
  }

  llm_explain(id) {
    const component = this.components.get(id);
    if (!component) {
      return `组件 "${id}" 不存在`;
    }
    return LLMDescriptor.toNaturalLanguage(component);
  }

  llm_getActions(id = null) {
    if (id) {
      const component = this.components.get(id);
      if (!component) return { error: `Component "${id}" not found` };
      
      return {
        componentId: id,
        actions: LLMDescriptor.toOpenAIFunctions(component)
      };
    }

    const allActions = [];
    this.components.forEach(component => {
      allActions.push(...LLMDescriptor.toOpenAIFunctions(component));
    });
    return { actions: allActions };
  }

  llm_search(query) {
    const keywords = query.toLowerCase().split(/\s+/);
    const results = [];

    this.components.forEach(component => {
      const text = [
        component.id,
        component.type,
        component.description,
        ...Object.keys(component.properties?.definitions || {}),
        ...Object.keys(component.behaviors?.definitions || {})
      ].join(' ').toLowerCase();

      const score = keywords.reduce((acc, kw) => {
        return acc + (text.includes(kw) ? 1 : 0);
      }, 0);

      if (score > 0) {
        results.push({
          id: component.id,
          type: component.type,
          description: component.description,
          score
        });
      }
    });

    return results
      .sort((a, b) => b.score - a.score)
      .slice(0, 10);
  }

  llm_getPageContext(pageId) {
    const ids = this.indexes.byPage.get(pageId);
    if (!ids || ids.size === 0) {
      return { error: `Page "${pageId}" not found or has no components` };
    }

    const components = Array.from(ids).map(id => {
      const c = this.components.get(id);
      return LLMDescriptor.describe(c, { includeValues: true, compact: true });
    });

    return {
      pageId,
      componentCount: components.length,
      components
    };
  }

  llm_generateSystemPrompt() {
    const componentCount = this.components.size;
    const types = Array.from(this.indexes.byType.keys());
    
    let prompt = `你是一个 UI 操作助手。当前页面有 ${componentCount} 个可操作组件。\n\n`;
    prompt += `组件类型: ${types.join(', ')}\n\n`;
    prompt += `你可以通过以下方式操作组件:\n`;
    prompt += `1. llm_list() - 列出所有组件\n`;
    prompt += `2. llm_describe(id) - 获取组件详情\n`;
    prompt += `3. llm_explain(id) - 获取自然语言描述\n`;
    prompt += `4. llm_getActions(id) - 获取可执行操作\n`;
    prompt += `5. llm_search(query) - 搜索组件\n`;
    prompt += `6. execute(componentId, action, params) - 执行操作\n`;

    return prompt;
  }
}

export const componentRegistry = new ComponentRegistry();

export const LLMApi = {
  listComponents: (options) => componentRegistry.llm_list(options),
  describeComponent: (id, options) => componentRegistry.llm_describe(id, options),
  explainComponent: (id) => componentRegistry.llm_explain(id),
  getAvailableActions: (id) => componentRegistry.llm_getActions(id),
  searchComponents: (query) => componentRegistry.llm_search(query),
  getPageContext: (pageId) => componentRegistry.llm_getPageContext(pageId),
  getSystemPrompt: () => componentRegistry.llm_generateSystemPrompt()
};

export { ComponentRegistry };
export default ComponentRegistry;
