class ModalManager {
    static open(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('nx-modal--open');
        }
    }
    
    static close(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('nx-modal--open');
        }
    }
    
    static toggle(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.toggle('nx-modal--open');
        }
    }
}

window.ModalManager = ModalManager;
window.openModal = (id) => ModalManager.open(id);
window.closeModal = (id) => ModalManager.close(id);
