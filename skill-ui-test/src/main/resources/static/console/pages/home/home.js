const HomePage = {
    init() {
        console.log('[Home] Initializing home page...');
        this.bindEvents();
        this.loadDevices();
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
                    console.log('[Home] Navigate to:', href);
                }
            });
        });
        
        document.querySelectorAll('.switch input').forEach(toggle => {
            toggle.addEventListener('change', (e) => {
                const deviceCard = e.target.closest('.device-card');
                const deviceName = deviceCard.querySelector('.device-info h4').textContent;
                console.log(`[Home] Device ${deviceName} toggled: ${e.target.checked ? 'ON' : 'OFF'}`);
            });
        });
    },
    
    async loadDevices() {
        console.log('[Home] Loading devices...');
    },
    
    async refreshData() {
        console.log('[Home] Refreshing data...');
        location.reload();
    }
};

document.addEventListener('DOMContentLoaded', () => {
    HomePage.init();
});

window.HomePage = HomePage;
