const ThemeManager = {
    THEME_KEY: 'nexus-theme',
    
    init() {
        const savedTheme = localStorage.getItem(this.THEME_KEY) || 'dark';
        this.setTheme(savedTheme);
        this.bindEvents();
    },
    
    setTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem(this.THEME_KEY, theme);
        this.updateToggleButton(theme);
    },
    
    toggleTheme() {
        const currentTheme = document.documentElement.getAttribute('data-theme');
        const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
        this.setTheme(newTheme);
    },
    
    updateToggleButton(theme) {
        const btn = document.querySelector('.theme-toggle-btn');
        if (btn) {
            const icon = btn.querySelector('i');
            const text = btn.childNodes[btn.childNodes.length - 1];
            if (icon) {
                icon.className = theme === 'dark' ? 'ri-sun-line' : 'ri-moon-line';
            }
            if (text && text.nodeType === Node.TEXT_NODE) {
                text.textContent = theme === 'dark' ? ' 浅色模式' : ' 深色模式';
            }
        }
    },
    
    bindEvents() {
        const btn = document.querySelector('.theme-toggle-btn');
        if (btn) {
            btn.addEventListener('click', () => this.toggleTheme());
        }
    },
    
    getTheme() {
        return document.documentElement.getAttribute('data-theme') || 'dark';
    }
};

document.addEventListener('DOMContentLoaded', () => {
    ThemeManager.init();
});

window.ThemeManager = ThemeManager;
