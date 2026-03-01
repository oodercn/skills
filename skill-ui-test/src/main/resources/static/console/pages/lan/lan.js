const LanPage = {
    init() {
        console.log('[LAN] Initializing LAN management page...');
        this.bindEvents();
        this.loadNetworkData();
    },
    
    bindEvents() {
        const sidebarToggle = document.getElementById('sidebarToggle');
        if (sidebarToggle) {
            sidebarToggle.addEventListener('click', () => {
                document.getElementById('sidebar').classList.toggle('collapsed');
            });
        }
        
        document.querySelectorAll('.menu-item a').forEach(link => {
            link.addEventListener('click', (e) => {
                const href = link.getAttribute('href');
                if (href && href.startsWith('#')) {
                    e.preventDefault();
                    document.querySelectorAll('.menu-item').forEach(item => {
                        item.classList.remove('active');
                    });
                    link.parentElement.classList.add('active');
                    console.log('[LAN] Navigate to:', href);
                }
            });
        });
    },
    
    async loadNetworkData() {
        console.log('[LAN] Loading network data...');
    },
    
    async refreshData() {
        console.log('[LAN] Refreshing network data...');
        location.reload();
    },
    
    async scanNetwork() {
        console.log('[LAN] Scanning network...');
    }
};

document.addEventListener('DOMContentLoaded', () => {
    LanPage.init();
});

window.LanPage = LanPage;
