/**
 * NX Behavior Manager
 * 行为管理器 - 四分离之行为层
 */

class BehaviorManager {
  constructor(definitions, component) {
    this.definitions = definitions || {};
    this.component = component;
    this.history = [];
    this.maxHistory = 100;
  }

  async execute(name, params = {}) {
    const def = this.definitions[name];
    if (!def) {
      console.warn(`[BehaviorManager] Behavior "${name}" not defined`);
      return { success: false, error: 'Behavior not found' };
    }

    const validationResult = this._validateParams(name, params, def.parameters);
    if (!validationResult.valid) {
      return { success: false, error: validationResult.errors };
    }

    const historyEntry = {
      name,
      params,
      timestamp: Date.now(),
      status: 'pending'
    };

    try {
      const result = await def.execute.call(this.component, params);

      historyEntry.status = 'success';
      historyEntry.result = result;

      return { success: true, result };
    } catch (error) {
      historyEntry.status = 'error';
      historyEntry.error = error.message;

      return { success: false, error: error.message };
    } finally {
      this._addToHistory(historyEntry);
    }
  }

  _validateParams(name, params, schema) {
    if (!schema) return { valid: true };

    const errors = [];

    if (schema.required) {
      for (const required of schema.required) {
        if (params[required] === undefined) {
          errors.push(`Missing required parameter: ${required}`);
        }
      }
    }

    if (schema.properties) {
      for (const [key, value] of Object.entries(params)) {
        const propSchema = schema.properties[key];
        if (propSchema) {
          const typeError = this._checkType(key, value, propSchema);
          if (typeError) {
            errors.push(typeError);
          }
        }
      }
    }

    return { valid: errors.length === 0, errors };
  }

  _checkType(key, value, schema) {
    if (!schema.type) return null;
    
    const actualType = Array.isArray(value) ? 'array' : typeof value;
    const expectedType = schema.type;

    if (expectedType && actualType !== expectedType) {
      return `Parameter "${key}" should be ${expectedType}, got ${actualType}`;
    }

    return null;
  }

  _addToHistory(entry) {
    this.history.push(entry);
    if (this.history.length > this.maxHistory) {
      this.history.shift();
    }
  }

  getHistory(limit = 10) {
    return this.history.slice(-limit);
  }

  getAvailable() {
    return Object.keys(this.definitions);
  }

  hasBehavior(name) {
    return !!this.definitions[name];
  }

  getSchema() {
    const schema = {
      type: 'object',
      behaviors: []
    };

    for (const [name, def] of Object.entries(this.definitions)) {
      schema.behaviors.push({
        name,
        description: def.description || '',
        parameters: def.parameters || { type: 'object', properties: {} }
      });
    }

    return schema;
  }

  clearHistory() {
    this.history = [];
  }
}

export { BehaviorManager };
export default BehaviorManager;
