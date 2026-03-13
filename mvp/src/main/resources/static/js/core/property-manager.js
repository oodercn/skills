/**
 * NX Property Manager
 * 属性管理器 - 四分离之属性层
 */

const PropertyTypes = {
  STRING: 'string',
  NUMBER: 'number',
  BOOLEAN: 'boolean',
  ARRAY: 'array',
  OBJECT: 'object',
  FUNCTION: 'function',
  ANY: 'any'
};

class PropertyManager {
  constructor(definitions) {
    this.definitions = definitions || {};
    this.values = {};
    this.observers = new Map();
    this._initDefaults();
  }

  _initDefaults() {
    for (const [key, def] of Object.entries(this.definitions)) {
      if (def.default !== undefined) {
        this.values[key] = def.default;
      }
    }
  }

  get(name) {
    return this.values[name];
  }

  set(name, value) {
    const def = this.definitions[name];
    if (!def) {
      console.warn(`[PropertyManager] Property "${name}" not defined`);
      return false;
    }

    if (def.validator && !def.validator(value)) {
      console.error(`[PropertyManager] Property "${name}" validation failed`);
      return false;
    }

    const transformedValue = def.transformer ? def.transformer(value) : value;
    const oldValue = this.values[name];
    this.values[name] = transformedValue;

    if (def.observable && oldValue !== transformedValue) {
      this._notifyObservers(name, oldValue, transformedValue);
    }

    return true;
  }

  setMultiple(props) {
    const results = {};
    for (const [key, value] of Object.entries(props)) {
      results[key] = this.set(key, value);
    }
    return results;
  }

  getAll() {
    return { ...this.values };
  }

  observe(name, callback) {
    if (!this.observers.has(name)) {
      this.observers.set(name, new Set());
    }
    this.observers.get(name).add(callback);
    return () => this.observers.get(name).delete(callback);
  }

  _notifyObservers(name, oldValue, newValue) {
    const callbacks = this.observers.get(name);
    if (callbacks) {
      callbacks.forEach(cb => cb(newValue, oldValue, name));
    }
  }

  reset(name) {
    const def = this.definitions[name];
    if (def && def.default !== undefined) {
      this.set(name, def.default);
      return true;
    }
    return false;
  }

  resetAll() {
    for (const name of Object.keys(this.definitions)) {
      this.reset(name);
    }
  }

  getSchema() {
    const schema = {
      type: 'object',
      properties: {},
      required: []
    };

    for (const [key, def] of Object.entries(this.definitions)) {
      schema.properties[key] = {
        type: def.type || PropertyTypes.ANY,
        default: def.default,
        description: def.description || ''
      };
      if (def.required) {
        schema.required.push(key);
      }
    }

    return schema;
  }
}

export { PropertyManager, PropertyTypes };
export default PropertyManager;
