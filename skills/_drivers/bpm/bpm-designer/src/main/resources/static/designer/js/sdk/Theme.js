class ThemeManager {
    constructor() {
        this.theme = this._loadTheme();
        this._applyTheme(this.theme);
    }

    _loadTheme() {
        const saved = localStorage.getItem('bpm-theme');
        if (saved) {
            return saved;
        }
        return window.matchMedia('(prefers-color-scheme: light)').matches ? 'light' : 'dark';
    }

    _saveTheme(theme) {
        localStorage.setItem('bpm-theme', theme);
    }

    _applyTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        this._updateIcon(theme);
    }

    _updateIcon(theme) {
        const btn = document.getElementById('btnTheme');
        if (btn) {
            btn.innerHTML = IconManager.render(theme === 'dark' ? 'sun' : 'moon', 18, 'icon-theme');
        }
    }

    toggle() {
        this.theme = this.theme === 'dark' ? 'light' : 'dark';
        this._applyTheme(this.theme);
        this._saveTheme(this.theme);
    }

    getTheme() {
        return this.theme;
    }

    isDark() {
        return this.theme === 'dark';
    }
}

const ThemeFactory = {
    _manager: null,
    get: function() {
        if (!this._manager) {
            this._manager = new ThemeManager();
        }
        return this._manager;
    }
};

window.ThemeManager = ThemeManager;
window.ThemeFactory = ThemeFactory;
