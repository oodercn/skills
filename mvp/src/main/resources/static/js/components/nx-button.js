/**
 * NX Button Component
 * 示例组件 - 展示四分离架构
 */

import { PropertyManager, PropertyTypes } from '../core/property-manager.js';
import { StyleManager } from '../core/style-manager.js';
import { EventManager } from '../core/event-manager.js';
import { BehaviorManager } from '../core/behavior-manager.js';
import { componentRegistry } from '../core/component-registry.js';

class NxButton {
  constructor(element, options = {}) {
    this.element = element;
    this.id = element.id || `btn_${Date.now()}`;
    this.type = 'NxButton';
    this.description = '按钮组件';
    
    this._initProperties(options);
    this._initStyles(options);
    this._initEvents(options);
    this._initBehaviors();
    
    componentRegistry.register(this);
    
    this._applyInitialOptions(options);
    this._render();
  }

  _initProperties(options) {
    this.properties = new PropertyManager({
      label: {
        type: PropertyTypes.STRING,
        default: 'Button',
        description: '按钮文本',
        observable: true
      },
      disabled: {
        type: PropertyTypes.BOOLEAN,
        default: false,
        description: '是否禁用',
        observable: true
      },
      loading: {
        type: PropertyTypes.BOOLEAN,
        default: false,
        description: '加载状态',
        observable: true
      },
      icon: {
        type: PropertyTypes.STRING,
        default: '',
        description: '图标类名'
      }
    });
    
    this.properties.observe('disabled', (newVal) => {
      this._updateDisabledState(newVal);
    });
    
    this.properties.observe('loading', (newVal) => {
      this._updateLoadingState(newVal);
    });
  }

  _initStyles(options) {
    this.styles = new StyleManager({
      variant: {
        type: 'string',
        default: 'primary',
        cssClass: 'nx-btn',
        classMap: {
          primary: 'nx-btn--primary',
          secondary: 'nx-btn--secondary',
          danger: 'nx-btn--danger',
          ghost: 'nx-btn--ghost',
          link: 'nx-btn--link'
        },
        description: '按钮变体'
      },
      size: {
        type: 'string',
        default: 'md',
        classMap: {
          sm: 'nx-btn--sm',
          md: 'nx-btn--md',
          lg: 'nx-btn--lg'
        },
        description: '按钮尺寸'
      }
    }, this.element);
  }

  _initEvents(options) {
    this.events = new EventManager({
      click: {
        type: 'click',
        description: '点击事件',
        handler: (e) => {
          if (this.properties.get('disabled') || this.properties.get('loading')) {
            e.preventDefault();
            e.stopPropagation();
          }
        }
      }
    }, this.element);
    
    if (options.onClick) {
      this.events.on('click', options.onClick);
    }
  }

  _initBehaviors() {
    this.behaviors = new BehaviorManager({
      click: {
        description: '触发点击',
        parameters: { type: 'object', properties: {} },
        execute: async () => {
          this.events.trigger('click');
          return { clicked: true };
        }
      },
      setLoading: {
        description: '设置加载状态',
        parameters: {
          type: 'object',
          properties: {
            loading: { type: 'boolean', description: '加载状态' }
          },
          required: ['loading']
        },
        execute: async ({ loading }) => {
          this.properties.set('loading', loading);
          return { loading };
        }
      },
      disable: {
        description: '禁用按钮',
        parameters: { type: 'object', properties: {} },
        execute: async () => {
          this.properties.set('disabled', true);
          return { disabled: true };
        }
      },
      enable: {
        description: '启用按钮',
        parameters: { type: 'object', properties: {} },
        execute: async () => {
          this.properties.set('disabled', false);
          return { disabled: false };
        }
      },
      setLabel: {
        description: '设置按钮文本',
        parameters: {
          type: 'object',
          properties: {
            label: { type: 'string', description: '按钮文本' }
          },
          required: ['label']
        },
        execute: async ({ label }) => {
          this.properties.set('label', label);
          this._render();
          return { label };
        }
      }
    }, this);
  }

  _applyInitialOptions(options) {
    if (options.label) this.properties.set('label', options.label);
    if (options.variant) this.styles.set('variant', options.variant);
    if (options.size) this.styles.set('size', options.size);
    if (options.icon) this.properties.set('icon', options.icon);
    if (options.disabled) this.properties.set('disabled', options.disabled);
  }

  _render() {
    const label = this.properties.get('label');
    const icon = this.properties.get('icon');
    
    let html = '';
    if (this.properties.get('loading')) {
      html = '<i class="ri-loader-4-line ri-spin"></i>';
    } else if (icon) {
      html = `<i class="${icon}"></i>`;
    }
    html += `<span class="nx-btn__text">${label}</span>`;
    
    this.element.innerHTML = html;
  }

  _updateDisabledState(disabled) {
    if (disabled) {
      this.element.setAttribute('disabled', 'disabled');
      this.styles.addClass('nx-btn--disabled');
    } else {
      this.element.removeAttribute('disabled');
      this.styles.removeClass('nx-btn--disabled');
    }
  }

  _updateLoadingState(loading) {
    if (loading) {
      this.styles.addClass('nx-btn--loading');
    } else {
      this.styles.removeClass('nx-btn--loading');
    }
    this._render();
  }

  destroy() {
    this.events.destroy();
    componentRegistry.unregister(this.id);
  }
}

export default NxButton;

document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('[data-nx-button]').forEach(el => {
    const options = {
      label: el.dataset.label,
      variant: el.dataset.variant,
      size: el.dataset.size,
      icon: el.dataset.icon,
      onClick: el.dataset.onClick ? window[el.dataset.onClick] : null
    };
    new NxButton(el, options);
  });
});
