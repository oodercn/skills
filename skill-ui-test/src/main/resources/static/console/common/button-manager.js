class ButtonManager {
    static setLoading(button, loading) {
        if (typeof button === 'string') {
            button = document.getElementById(button);
        }
        if (!button) return;
        
        if (loading) {
            button.disabled = true;
            button.dataset.originalText = button.innerHTML;
            button.innerHTML = '<i class="ri-loader-4-line ri-spin"></i> 加载中...';
        } else {
            button.disabled = false;
            if (button.dataset.originalText) {
                button.innerHTML = button.dataset.originalText;
            }
        }
    }
}

window.ButtonManager = ButtonManager;
