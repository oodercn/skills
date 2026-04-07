class Panel {
    constructor(container, store) {
        this.container = container;
        this.store = store;
        this.panels = {};
        this.currentTab = 'basic';
        this._initPanels();
        this._bindEvents();
    }

    _initPanels() {
        this.panels = {
            basic: new BasicPanel(this.container.querySelector('#pageBasic')),
            timing: new TimingPanel(this.container.querySelector('#pageTiming')),
            route: new RoutePanel(this.container.querySelector('#pageRoute')),
            agent: new AgentPanel(this.container.querySelector('#pageAgent')),
            scene: new ScenePanel(this.container.querySelector('#pageScene'))
        };
    }

    _bindEvents() {
        const tabs = this.container.querySelectorAll('.d-tab');
        tabs.forEach(tab => {
            tab.addEventListener('click', () => {
                this._switchTab(tab.dataset.tab);
            });
        });

        this.store.on('activity:select', (activity) => {
            this._renderCurrentPanel(activity);
        });
    }

    _switchTab(tabName) {
        this.currentTab = tabName;

        const tabs = this.container.querySelectorAll('.d-tab');
        tabs.forEach(tab => {
            tab.classList.toggle('active', tab.dataset.tab === tabName);
        });

        const pages = this.container.querySelectorAll('.d-panel-page');
        pages.forEach(page => {
            page.classList.toggle('active', page.id === 'page' + tabName.charAt(0).toUpperCase() + tabName.slice(1));
        });

        const activity = this.store.currentActivity;
        if (activity) {
            this._renderCurrentPanel(activity);
        }
    }

    _renderCurrentPanel(activity) {
        const panel = this.panels[this.currentTab];
        if (panel) {
            panel.render(activity);
        }
    }
}

window.Panel = Panel;
