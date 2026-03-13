/**
 * NX Event Manager
 * 事件管理器 - 四分离之事件层
 */

class EventManager {
  constructor(definitions, element) {
    this.definitions = definitions || {};
    this.element = element;
    this.bindings = new Map();
    this.callbacks = new Map();
    this._bindAll();
  }

  _bindAll() {
    for (const [eventName, def] of Object.entries(this.definitions)) {
      this._bind(eventName, def);
    }
  }

  _bind(eventName, def) {
    if (!this.element) return;

    const handler = (event) => {
      if (def.selector) {
        const target = event.target.closest(def.selector);
        if (!target) return;
        event.delegateTarget = target;
      }

      if (def.preventDefault) {
        event.preventDefault();
      }

      if (def.stopPropagation) {
        event.stopPropagation();
      }

      if (def.handler) {
        def.handler.call(this, event, this.element);
      }

      this._triggerCallbacks(eventName, event);
    };

    this.element.addEventListener(def.type, handler);
    this.bindings.set(eventName, { type: def.type, handler });
  }

  trigger(eventName, detail = {}) {
    if (!this.element) return false;

    const def = this.definitions[eventName];
    if (!def) {
      console.warn(`[EventManager] Event "${eventName}" not defined`);
      return false;
    }

    const event = new CustomEvent(def.type, {
      bubbles: true,
      cancelable: true,
      detail
    });

    this.element.dispatchEvent(event);
    return true;
  }

  on(eventName, callback) {
    if (!this.callbacks.has(eventName)) {
      this.callbacks.set(eventName, new Map());
    }

    const callbackId = `cb_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
    this.callbacks.get(eventName).set(callbackId, callback);

    return callbackId;
  }

  off(eventName, callbackId) {
    const callbacks = this.callbacks.get(eventName);
    if (callbacks && callbacks.has(callbackId)) {
      callbacks.delete(callbackId);
      return true;
    }
    return false;
  }

  once(eventName, callback) {
    const wrapper = (...args) => {
      callback(...args);
      this.off(eventName, callbackId);
    };
    const callbackId = this.on(eventName, wrapper);
    return callbackId;
  }

  _triggerCallbacks(eventName, event) {
    const callbacks = this.callbacks.get(eventName);
    if (callbacks) {
      callbacks.forEach(cb => cb(event, this.element));
    }
  }

  getSchema() {
    const schema = {
      type: 'object',
      events: []
    };

    for (const [name, def] of Object.entries(this.definitions)) {
      schema.events.push({
        name,
        type: def.type,
        description: def.description || '',
        selector: def.selector || null
      });
    }

    return schema;
  }

  destroy() {
    this.bindings.forEach(({ type, handler }) => {
      if (this.element) {
        this.element.removeEventListener(type, handler);
      }
    });
    this.bindings.clear();
    this.callbacks.clear();
  }
}

export { EventManager };
export default EventManager;
