/**
 * NX LLM Descriptor
 * LLM 描述生成器 - 为组件生成 LLM 友好的描述信息
 */

class LLMDescriptor {
  
  static describe(component, options = {}) {
    const { 
      includeValues = true,
      includeSchema = false,
      compact = true
    } = options;

    const descriptor = {
      id: component.id,
      type: component.constructor.name,
      description: component.description || `${component.constructor.name} component`,
      summary: {
        properties: this._summarizeProperties(component.properties, includeValues),
        styles: this._summarizeStyles(component.styles, includeValues),
        events: this._summarizeEvents(component.events),
        behaviors: this._summarizeBehaviors(component.behaviors)
      }
    };

    if (includeSchema) {
      descriptor.schema = {
        properties: component.properties?.getSchema?.() || null,
        styles: component.styles?.getSchema?.() || null,
        events: component.events?.getSchema?.() || null,
        behaviors: component.behaviors?.getSchema?.() || null
      };
    }

    return compact ? this._compact(descriptor) : descriptor;
  }

  static _summarizeProperties(properties, includeValues) {
    if (!properties) return null;
    
    const defs = properties.definitions;
    const values = properties.values;
    const result = {};

    for (const [name, def] of Object.entries(defs)) {
      result[name] = {
        type: def.type,
        desc: def.description || name
      };
      if (includeValues && values[name] !== undefined) {
        result[name].value = values[name];
      }
    }

    return result;
  }

  static _summarizeStyles(styles, includeValues) {
    if (!styles) return null;
    
    const defs = styles.definitions;
    const values = styles.values;
    const result = {};

    for (const [name, def] of Object.entries(defs)) {
      result[name] = {
        type: def.type || 'string',
        desc: def.description || name
      };
      if (includeValues && values[name] !== undefined) {
        result[name].value = values[name];
      }
    }

    return result;
  }

  static _summarizeEvents(events) {
    if (!events) return null;
    
    const defs = events.definitions;
    const result = {};

    for (const [name, def] of Object.entries(defs)) {
      result[name] = {
        type: def.type,
        desc: def.description || name
      };
    }

    return result;
  }

  static _summarizeBehaviors(behaviors) {
    if (!behaviors) return null;
    
    const defs = behaviors.definitions;
    const result = {};

    for (const [name, def] of Object.entries(defs)) {
      result[name] = {
        desc: def.description || name,
        params: def.parameters?.properties 
          ? Object.keys(def.parameters.properties) 
          : []
      };
    }

    return result;
  }

  static _compact(descriptor) {
    return JSON.parse(JSON.stringify(descriptor, (key, value) => {
      if (value === null || value === undefined || value === '') {
        return undefined;
      }
      return value;
    }));
  }

  static toNaturalLanguage(component) {
    const desc = this.describe(component, { includeValues: true });
    
    let text = `【${desc.type}】ID: ${desc.id}\n`;
    text += `${desc.description}\n\n`;

    if (desc.summary.properties) {
      text += `📋 属性:\n`;
      for (const [name, info] of Object.entries(desc.summary.properties)) {
        text += `  • ${name}(${info.type}): ${info.desc}`;
        if (info.value !== undefined) {
          text += ` [当前值: ${JSON.stringify(info.value)}]`;
        }
        text += '\n';
      }
    }

    if (desc.summary.styles) {
      text += `\n🎨 样式:\n`;
      for (const [name, info] of Object.entries(desc.summary.styles)) {
        text += `  • ${name}: ${info.desc}`;
        if (info.value !== undefined) {
          text += ` [${info.value}]`;
        }
        text += '\n';
      }
    }

    if (desc.summary.events) {
      text += `\n⚡ 事件:\n`;
      for (const [name, info] of Object.entries(desc.summary.events)) {
        text += `  • ${name}(${info.type}): ${info.desc}\n`;
      }
    }

    if (desc.summary.behaviors) {
      text += `\n🔧 可执行操作:\n`;
      for (const [name, info] of Object.entries(desc.summary.behaviors)) {
        text += `  • ${name}(${info.params.join(', ')}): ${info.desc}\n`;
      }
    }

    return text;
  }

  static toOpenAIFunctions(component) {
    const functions = [];
    const prefix = component.id;

    if (component.properties) {
      functions.push({
        name: `${prefix}.getProperty`,
        description: `获取 ${component.constructor.name} 的属性值`,
        parameters: {
          type: 'object',
          properties: {
            name: { 
              type: 'string', 
              enum: Object.keys(component.properties.definitions),
              description: '属性名'
            }
          },
          required: ['name']
        }
      });
      functions.push({
        name: `${prefix}.setProperty`,
        description: `设置 ${component.constructor.name} 的属性值`,
        parameters: {
          type: 'object',
          properties: {
            name: { 
              type: 'string', 
              enum: Object.keys(component.properties.definitions),
              description: '属性名'
            },
            value: { description: '属性值' }
          },
          required: ['name', 'value']
        }
      });
    }

    if (component.styles) {
      functions.push({
        name: `${prefix}.setStyle`,
        description: `设置 ${component.constructor.name} 的样式`,
        parameters: {
          type: 'object',
          properties: {
            name: { 
              type: 'string', 
              enum: Object.keys(component.styles.definitions),
              description: '样式名'
            },
            value: { description: '样式值' }
          },
          required: ['name', 'value']
        }
      });
    }

    if (component.events) {
      functions.push({
        name: `${prefix}.triggerEvent`,
        description: `触发 ${component.constructor.name} 的事件`,
        parameters: {
          type: 'object',
          properties: {
            name: { 
              type: 'string', 
              enum: Object.keys(component.events.definitions),
              description: '事件名'
            },
            detail: { type: 'object', description: '事件数据' }
          },
          required: ['name']
        }
      });
    }

    if (component.behaviors) {
      for (const [name, def] of Object.entries(component.behaviors.definitions)) {
        functions.push({
          name: `${prefix}.${name}`,
          description: def.description || `执行 ${name} 操作`,
          parameters: def.parameters || { type: 'object', properties: {} }
        });
      }
    }

    return functions;
  }
}

export { LLMDescriptor };
export default LLMDescriptor;
