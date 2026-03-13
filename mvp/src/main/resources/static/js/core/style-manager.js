/**
 * NX Style Manager
 * 样式管理器 - 四分离之样式层
 */

class StyleManager {
  constructor(definitions, element) {
    this.definitions = definitions || {};
    this.element = element;
    this.values = {};
    this.classes = new Set();
    this._initDefaults();
  }

  _initDefaults() {
    for (const [key, def] of Object.entries(this.definitions)) {
      if (def.default !== undefined) {
        this.values[key] = def.default;
      }
      if (def.cssClass) {
        this.classes.add(def.cssClass);
      }
    }
    this._applyClasses();
  }

  get(name) {
    return this.values[name];
  }

  set(name, value) {
    const def = this.definitions[name];
    if (!def) {
      console.warn(`[StyleManager] Style "${name}" not defined`);
      return false;
    }

    this.values[name] = value;

    if (def.cssVar && this.element) {
      this.element.style.setProperty(def.cssVar, String(value));
    }

    if (def.cssClass && def.classMap) {
      const targetClass = def.classMap[value];
      if (targetClass && this.element) {
        Object.values(def.classMap).forEach(cls => {
          this.element.classList.remove(cls);
        });
        this.element.classList.add(targetClass);
      }
    }

    return true;
  }

  setMultiple(styles) {
    const results = {};
    for (const [key, value] of Object.entries(styles)) {
      results[key] = this.set(key, value);
    }
    return results;
  }

  getAll() {
    return { ...this.values };
  }

  addClass(className) {
    if (this.element) {
      this.element.classList.add(className);
      this.classes.add(className);
      return true;
    }
    return false;
  }

  removeClass(className) {
    if (this.element) {
      this.element.classList.remove(className);
      this.classes.delete(className);
      return true;
    }
    return false;
  }

  hasClass(className) {
    return this.element ? this.element.classList.contains(className) : false;
  }

  toggleClass(className) {
    if (this.element) {
      const result = this.element.classList.toggle(className);
      if (result) {
        this.classes.add(className);
      } else {
        this.classes.delete(className);
      }
      return result;
    }
    return false;
  }

  setTheme(theme) {
    if (this.element) {
      this.element.setAttribute('data-theme', theme);
      return true;
    }
    return false;
  }

  getTheme() {
    return this.element ? this.element.getAttribute('data-theme') : null;
  }

  _applyClasses() {
    if (this.element) {
      this.classes.forEach(cls => {
        this.element.classList.add(cls);
      });
    }
  }

  getSchema() {
    const schema = {
      type: 'object',
      properties: {},
      cssVariables: [],
      cssClasses: []
    };

    for (const [key, def] of Object.entries(this.definitions)) {
      schema.properties[key] = {
        type: def.type || 'string',
        default: def.default,
        description: def.description || ''
      };
      if (def.cssVar) {
        schema.cssVariables.push({ name: key, cssVar: def.cssVar });
      }
      if (def.cssClass) {
        schema.cssClasses.push({ name: key, cssClass: def.cssClass });
      }
    }

    return schema;
  }
}

export { StyleManager };
export default StyleManager;
