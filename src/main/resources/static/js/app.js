/**
 * Biblioteca Igreja - JavaScript Customizado
 * Funcionalidades interativas para a aplicação
 */

// Configurações globais
const APP_CONFIG = {
    apiBaseUrl: '/api',
    defaultPageSize: 10,
    autoHideAlerts: 5000,
    debounceDelay: 300
};

// Utilitários
const Utils = {
    // Debounce para otimizar buscas
    debounce: function(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    },

    // Formatar data
    formatDate: function(date) {
        if (!date) return '-';
        return new Date(date).toLocaleDateString('pt-BR');
    },

    // Formatar moeda
    formatCurrency: function(value) {
        if (!value) return 'R$ 0,00';
        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(value);
    },

    // Mostrar loading
    showLoading: function(element) {
        if (element) {
            element.innerHTML = '<div class="spinner"></div>';
        }
    },

    // Esconder loading
    hideLoading: function(element, content) {
        if (element && content) {
            element.innerHTML = content;
        }
    }
};

// Gerenciador de alertas
const AlertManager = {
    show: function(message, type = 'info', duration = APP_CONFIG.autoHideAlerts) {
        const alertContainer = document.getElementById('alert-container') || this.createContainer();
        
        const alertId = 'alert-' + Date.now();
        const alertHtml = `
            <div id="${alertId}" class="alert alert-${type} alert-dismissible fade show" role="alert">
                <i class="bi bi-${this.getIcon(type)}"></i>
                <span>${message}</span>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;
        
        alertContainer.insertAdjacentHTML('beforeend', alertHtml);
        
        if (duration > 0) {
            setTimeout(() => {
                this.hide(alertId);
            }, duration);
        }
        
        return alertId;
    },

    hide: function(alertId) {
        const alert = document.getElementById(alertId);
        if (alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }
    },

    createContainer: function() {
        const container = document.createElement('div');
        container.id = 'alert-container';
        container.className = 'position-fixed top-0 end-0 p-3';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
        return container;
    },

    getIcon: function(type) {
        const icons = {
            'success': 'check-circle',
            'danger': 'exclamation-triangle',
            'warning': 'exclamation-triangle',
            'info': 'info-circle'
        };
        return icons[type] || 'info-circle';
    }
};

// Gerenciador de formulários
const FormManager = {
    // Validação em tempo real
    validateField: function(field) {
        const value = field.value.trim();
        const type = field.type;
        const required = field.hasAttribute('required');
        
        let isValid = true;
        let message = '';
        
        if (required && !value) {
            isValid = false;
            message = 'Este campo é obrigatório';
        } else if (type === 'email' && value) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(value)) {
                isValid = false;
                message = 'Email inválido';
            }
        } else if (type === 'tel' && value) {
            const phoneRegex = /^\(\d{2}\)\s\d{4,5}-\d{4}$/;
            if (!phoneRegex.test(value)) {
                isValid = false;
                message = 'Telefone inválido';
            }
        }
        
        this.showFieldValidation(field, isValid, message);
        return isValid;
    },

    showFieldValidation: function(field, isValid, message) {
        const feedback = field.parentNode.querySelector('.invalid-feedback') || 
                        field.parentNode.querySelector('.valid-feedback');
        
        if (feedback) {
            feedback.textContent = message;
            feedback.className = isValid ? 'valid-feedback' : 'invalid-feedback';
        }
        
        field.classList.toggle('is-valid', isValid);
        field.classList.toggle('is-invalid', !isValid);
    },

    // Máscara para telefone
    applyPhoneMask: function(input) {
        input.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length >= 11) {
                value = value.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
            } else if (value.length >= 7) {
                value = value.replace(/(\d{2})(\d{4})(\d{0,4})/, '($1) $2-$3');
            } else if (value.length >= 3) {
                value = value.replace(/(\d{2})(\d{0,5})/, '($1) $2');
            }
            e.target.value = value;
        });
    },

    // Validação completa do formulário
    validateForm: function(form) {
        const fields = form.querySelectorAll('input[required], select[required], textarea[required]');
        let isValid = true;
        
        fields.forEach(field => {
            if (!this.validateField(field)) {
                isValid = false;
            }
        });
        
        return isValid;
    }
};

// Gerenciador de tabelas
const TableManager = {
    // Busca em tempo real
    setupSearch: function(inputSelector, tableSelector) {
        const searchInput = document.querySelector(inputSelector);
        const table = document.querySelector(tableSelector);
        
        if (!searchInput || !table) return;
        
        const debouncedSearch = Utils.debounce((searchTerm) => {
            this.filterTable(table, searchTerm);
        }, APP_CONFIG.debounceDelay);
        
        searchInput.addEventListener('input', function(e) {
            debouncedSearch(e.target.value.toLowerCase());
        });
    },

    filterTable: function(table, searchTerm) {
        const rows = table.querySelectorAll('tbody tr');
        
        rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            const shouldShow = text.includes(searchTerm);
            row.style.display = shouldShow ? '' : 'none';
        });
    },

    // Ordenação de colunas
    setupSorting: function(tableSelector) {
        const table = document.querySelector(tableSelector);
        if (!table) return;
        
        const headers = table.querySelectorAll('th[data-sort]');
        
        headers.forEach(header => {
            header.style.cursor = 'pointer';
            header.addEventListener('click', function() {
                const column = this.dataset.sort;
                const currentOrder = this.dataset.order || 'asc';
                const newOrder = currentOrder === 'asc' ? 'desc' : 'asc';
                
                // Atualizar indicadores visuais
                headers.forEach(h => h.classList.remove('sort-asc', 'sort-desc'));
                this.classList.add(`sort-${newOrder}`);
                this.dataset.order = newOrder;
                
                // Implementar ordenação (seria necessário integrar com backend)
                console.log(`Ordenar por ${column} em ordem ${newOrder}`);
            });
        });
    }
};

// Gerenciador de modais
const ModalManager = {
    show: function(modalId, data = {}) {
        const modal = document.getElementById(modalId);
        if (!modal) return;
        
        // Preencher dados se fornecidos
        Object.keys(data).forEach(key => {
            const element = modal.querySelector(`[data-field="${key}"]`);
            if (element) {
                element.textContent = data[key];
            }
        });
        
        const bsModal = new bootstrap.Modal(modal);
        bsModal.show();
    },

    hide: function(modalId) {
        const modal = document.getElementById(modalId);
        if (!modal) return;
        
        const bsModal = bootstrap.Modal.getInstance(modal);
        if (bsModal) {
            bsModal.hide();
        }
    }
};

// Gerenciador de confirmações
const ConfirmManager = {
    show: function(message, callback, title = 'Confirmação') {
        const confirmModal = document.getElementById('confirmModal') || this.createModal();
        
        const modalTitle = confirmModal.querySelector('.modal-title');
        const modalBody = confirmModal.querySelector('.modal-body');
        const confirmBtn = confirmModal.querySelector('.btn-confirm');
        
        modalTitle.textContent = title;
        modalBody.textContent = message;
        
        // Remover listeners anteriores
        const newConfirmBtn = confirmBtn.cloneNode(true);
        confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);
        
        newConfirmBtn.addEventListener('click', function() {
            callback();
            ModalManager.hide('confirmModal');
        });
        
        ModalManager.show('confirmModal');
    },

    createModal: function() {
        const modalHtml = `
            <div class="modal fade" id="confirmModal" tabindex="-1">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Confirmação</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            Tem certeza que deseja continuar?
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                            <button type="button" class="btn btn-danger btn-confirm">Confirmar</button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        document.body.insertAdjacentHTML('beforeend', modalHtml);
        return document.getElementById('confirmModal');
    }
};

// Inicialização da aplicação
document.addEventListener('DOMContentLoaded', function() {
    console.log('Biblioteca Igreja - Sistema carregado');
    
    // Configurar máscaras de telefone
    const phoneInputs = document.querySelectorAll('input[type="tel"]');
    phoneInputs.forEach(input => {
        FormManager.applyPhoneMask(input);
    });
    
    // Configurar validação de formulários
    const forms = document.querySelectorAll('form[data-validate]');
    forms.forEach(form => {
        const inputs = form.querySelectorAll('input, select, textarea');
        inputs.forEach(input => {
            input.addEventListener('blur', function() {
                FormManager.validateField(this);
            });
        });
        
        form.addEventListener('submit', function(e) {
            if (!FormManager.validateForm(this)) {
                e.preventDefault();
                AlertManager.show('Por favor, corrija os erros no formulário', 'danger');
            }
        });
    });
    
    // Configurar confirmações de exclusão
    const deleteButtons = document.querySelectorAll('.btn-delete');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const form = this.closest('form');
            if (form) {
                ConfirmManager.show(
                    'Tem certeza que deseja excluir este item? Esta ação não pode ser desfeita.',
                    function() {
                        form.submit();
                    },
                    'Confirmar Exclusão'
                );
            }
        });
    });
    
    // Configurar busca em tabelas
    TableManager.setupSearch('.table-search', '.table');
    TableManager.setupSorting('.table');
    
    // Auto-hide alerts
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, APP_CONFIG.autoHideAlerts);
    });
    
    // Adicionar animações de entrada
    const cards = document.querySelectorAll('.card');
    cards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        
        setTimeout(() => {
            card.style.transition = 'all 0.5s ease';
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, index * 100);
    });
});

// Exportar para uso global
window.BibliotecaApp = {
    Utils,
    AlertManager,
    FormManager,
    TableManager,
    ModalManager,
    ConfirmManager,
    APP_CONFIG
};
