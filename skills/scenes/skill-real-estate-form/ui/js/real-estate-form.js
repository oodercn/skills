const RealEstateForm = {
    properties: [],
    districts: ['朝阳区', '海淀区', '东城区', '西城区', '丰台区', '石景山区', '通州区', '顺义区', '大兴区', '昌平区'],
    currentProperty: null,
    uploadedImages: [],
    
    init() {
        this.loadDistricts();
        this.loadProperties();
        this.bindEvents();
    },
    
    bindEvents() {
        document.getElementById('property-price')?.addEventListener('input', () => this.calculateUnitPrice());
        document.getElementById('property-area')?.addEventListener('input', () => this.calculateUnitPrice());
    },
    
    loadDistricts() {
        const filterSelect = document.getElementById('filter-district');
        const formSelect = document.getElementById('property-district');
        
        if (filterSelect) {
            filterSelect.innerHTML = '<option value="">全部区域</option>';
            this.districts.forEach(d => {
                filterSelect.innerHTML += `<option value="${d}">${d}</option>`;
            });
        }
        
        if (formSelect) {
            formSelect.innerHTML = '<option value="">请选择区域</option>';
            this.districts.forEach(d => {
                formSelect.innerHTML += `<option value="${d}">${d}</option>`;
            });
        }
    },
    
    async loadProperties() {
        const tbody = document.getElementById('property-table-body');
        if (!tbody) return;
        
        tbody.innerHTML = '<tr><td colspan="8" class="nx-text-center nx-text-secondary"><i class="ri-loader-4-line ri-spin"></i> 加载中...</td></tr>';
        
        try {
            const response = await this.promisify(NexusAPI, 'get', '/api/real-estate/properties');
            
            if (response.status === 'success' && response.data) {
                this.properties = response.data.properties || response.data || [];
            } else {
                this.properties = this.getMockProperties();
            }
            
            this.renderProperties();
            this.updateCount();
        } catch (error) {
            console.error('Failed to load properties:', error);
            this.properties = this.getMockProperties();
            this.renderProperties();
            this.updateCount();
        }
    },
    
    getMockProperties() {
        return [
            {
                id: 'prop-001',
                title: '精装修三室两厅 南北通透 近地铁',
                community: '阳光花园',
                district: '朝阳区',
                roomType: '3室2厅',
                area: 125,
                price: 580,
                status: 'available',
                createdAt: Date.now() - 86400000 * 2
            },
            {
                id: 'prop-002',
                title: '学区房 两室一厅 精装修',
                community: '学府雅苑',
                district: '海淀区',
                roomType: '2室1厅',
                area: 89,
                price: 420,
                status: 'reserved',
                createdAt: Date.now() - 86400000 * 5
            },
            {
                id: 'prop-003',
                title: '豪华装修四室 南向采光好',
                community: '望京新城',
                district: '朝阳区',
                roomType: '4室2厅',
                area: 168,
                price: 980,
                status: 'available',
                createdAt: Date.now() - 86400000 * 1
            }
        ];
    },
    
    renderProperties() {
        const tbody = document.getElementById('property-table-body');
        if (!tbody) return;
        
        if (!this.properties.length) {
            tbody.innerHTML = '<tr><td colspan="8" class="nx-text-center nx-text-secondary"><i class="ri-inbox-line" style="font-size: 24px;"></i><p>暂无房源数据</p></td></tr>';
            return;
        }
        
        tbody.innerHTML = this.properties.map(p => `
            <tr>
                <td>
                    <div style="font-weight: 500;">${p.title}</div>
                    <div style="font-size: var(--nx-text-xs); color: var(--nx-text-secondary);">${p.address || p.community}</div>
                </td>
                <td>${p.community}</td>
                <td>${p.roomType}</td>
                <td>${p.area}㎡</td>
                <td><span style="color: var(--ns-danger); font-weight: 600;">${p.price}万</span></td>
                <td>
                    <span class="status-badge status-badge--${p.status}">
                        ${this.getStatusText(p.status)}
                    </span>
                </td>
                <td>${this.formatTime(p.createdAt)}</td>
                <td>
                    <div class="action-btns">
                        <button class="nx-btn nx-btn--ghost nx-btn--icon nx-btn--sm" onclick="RealEstateForm.editProperty('${p.id}')" title="编辑">
                            <i class="ri-edit-line"></i>
                        </button>
                        <button class="nx-btn nx-btn--ghost nx-btn--icon nx-btn--sm" onclick="RealEstateForm.deleteProperty('${p.id}')" title="删除">
                            <i class="ri-delete-bin-line"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    },
    
    updateCount() {
        const countEl = document.getElementById('property-count');
        if (countEl) {
            countEl.textContent = `${this.properties.length} 套`;
        }
    },
    
    getStatusText(status) {
        const statusMap = {
            'available': '在售',
            'reserved': '已预订',
            'sold': '已售'
        };
        return statusMap[status] || status;
    },
    
    formatTime(timestamp) {
        if (!timestamp) return '-';
        const date = new Date(timestamp);
        return `${date.getMonth() + 1}/${date.getDate()}`;
    },
    
    openCreateModal() {
        this.currentProperty = null;
        document.getElementById('modal-title').textContent = '新建房源';
        document.getElementById('property-form').reset();
        document.getElementById('property-id').value = '';
        this.uploadedImages = [];
        this.renderUploadedImages();
        document.getElementById('property-modal').classList.add('modal--open');
    },
    
    editProperty(id) {
        const property = this.properties.find(p => p.id === id);
        if (!property) return;
        
        this.currentProperty = property;
        document.getElementById('modal-title').textContent = '编辑房源';
        
        document.getElementById('property-id').value = property.id;
        document.getElementById('property-title').value = property.title || '';
        document.getElementById('property-district').value = property.district || '';
        document.getElementById('property-community').value = property.community || '';
        document.getElementById('property-room-type').value = property.roomType || '';
        document.getElementById('property-area').value = property.area || '';
        document.getElementById('property-price').value = property.price || '';
        
        this.calculateUnitPrice();
        
        document.getElementById('property-modal').classList.add('modal--open');
    },
    
    closeModal() {
        document.getElementById('property-modal').classList.remove('modal--open');
    },
    
    calculateUnitPrice() {
        const price = parseFloat(document.getElementById('property-price').value) || 0;
        const area = parseFloat(document.getElementById('property-area').value) || 0;
        
        if (price > 0 && area > 0) {
            const unitPrice = Math.round((price * 10000) / area);
            document.getElementById('property-unit-price').value = unitPrice.toLocaleString();
        } else {
            document.getElementById('property-unit-price').value = '';
        }
    },
    
    async getValuation() {
        const area = parseFloat(document.getElementById('property-area').value);
        const district = document.getElementById('property-district').value;
        const roomType = document.getElementById('property-room-type').value;
        
        if (!area || !district) {
            alert('请先填写面积和区域信息');
            return;
        }
        
        document.getElementById('property-valuation').value = '估价中...';
        
        try {
            const response = await this.promisify(NexusAPI, 'post', '/api/real-estate/valuation', {
                area, district, roomType
            });
            
            if (response.status === 'success' && response.data) {
                document.getElementById('property-valuation').value = response.data.valuation + ' 万';
            } else {
                const mockValuation = Math.round(area * (3 + Math.random() * 2));
                document.getElementById('property-valuation').value = mockValuation + ' 万';
            }
        } catch (error) {
            console.error('Valuation failed:', error);
            const mockValuation = Math.round(area * (3 + Math.random() * 2));
            document.getElementById('property-valuation').value = mockValuation + ' 万';
        }
    },
    
    handleImageUpload(files) {
        if (!files || files.length === 0) return;
        
        const maxImages = 20;
        const maxSize = 10 * 1024 * 1024;
        
        for (let i = 0; i < files.length && this.uploadedImages.length < maxImages; i++) {
            const file = files[i];
            
            if (file.size > maxSize) {
                alert(`图片 ${file.name} 超过10MB限制`);
                continue;
            }
            
            const reader = new FileReader();
            reader.onload = (e) => {
                this.uploadedImages.push({
                    name: file.name,
                    url: e.target.result
                });
                this.renderUploadedImages();
            };
            reader.readAsDataURL(file);
        }
    },
    
    renderUploadedImages() {
        const container = document.getElementById('image-upload-area');
        if (!container) return;
        
        let html = this.uploadedImages.map((img, index) => `
            <div class="image-preview">
                <img src="${img.url}" alt="${img.name}">
                <button type="button" class="image-remove" onclick="RealEstateForm.removeImage(${index})">
                    <i class="ri-close-line"></i>
                </button>
            </div>
        `).join('');
        
        if (this.uploadedImages.length < 20) {
            html += `
                <div class="image-upload-btn" onclick="document.getElementById('image-input').click()">
                    <i class="ri-add-line"></i>
                    <span>上传图片</span>
                </div>
            `;
        }
        
        container.innerHTML = html;
    },
    
    removeImage(index) {
        this.uploadedImages.splice(index, 1);
        this.renderUploadedImages();
    },
    
    async saveProperty() {
        const id = document.getElementById('property-id').value;
        
        const property = {
            title: document.getElementById('property-title').value,
            district: document.getElementById('property-district').value,
            community: document.getElementById('property-community').value,
            roomType: document.getElementById('property-room-type').value,
            area: parseFloat(document.getElementById('property-area').value) || 0,
            innerArea: parseFloat(document.getElementById('property-inner-area').value) || 0,
            floor: parseInt(document.getElementById('property-floor').value) || 0,
            totalFloor: parseInt(document.getElementById('property-total-floor').value) || 0,
            orientation: document.getElementById('property-orientation').value,
            decoration: document.getElementById('property-decoration').value,
            buildYear: parseInt(document.getElementById('property-build-year').value) || 0,
            propertyType: document.getElementById('property-property-type').value,
            price: parseFloat(document.getElementById('property-price').value) || 0,
            address: document.getElementById('property-address').value,
            description: document.getElementById('property-description').value,
            ownerName: document.getElementById('property-owner-name').value,
            ownerPhone: document.getElementById('property-owner-phone').value,
            status: document.querySelector('input[name="property-status"]:checked')?.value || 'available',
            images: this.uploadedImages
        };
        
        if (!property.title || !property.district || !property.community || !property.roomType || !property.area || !property.price) {
            alert('请填写必填字段');
            return;
        }
        
        try {
            let response;
            if (id) {
                response = await this.promisify(NexusAPI, 'put', `/api/real-estate/properties/${id}`, property);
            } else {
                response = await this.promisify(NexusAPI, 'post', '/api/real-estate/properties', property);
            }
            
            if (response.status === 'success') {
                this.closeModal();
                await this.loadProperties();
                alert('保存成功');
            } else {
                if (!id) {
                    property.id = 'prop-' + Date.now();
                    property.createdAt = Date.now();
                    this.properties.unshift(property);
                    this.renderProperties();
                    this.updateCount();
                    this.closeModal();
                    alert('保存成功（本地模拟）');
                } else {
                    alert('保存失败: ' + (response.message || '未知错误'));
                }
            }
        } catch (error) {
            console.error('Failed to save property:', error);
            if (!id) {
                property.id = 'prop-' + Date.now();
                property.createdAt = Date.now();
                this.properties.unshift(property);
                this.renderProperties();
                this.updateCount();
                this.closeModal();
                alert('保存成功（本地模拟）');
            } else {
                alert('保存失败: ' + error.message);
            }
        }
    },
    
    async deleteProperty(id) {
        if (!confirm('确定要删除此房源吗？')) return;
        
        try {
            const response = await this.promisify(NexusAPI, 'delete', `/api/real-estate/properties/${id}`);
            
            if (response.status === 'success') {
                this.properties = this.properties.filter(p => p.id !== id);
                this.renderProperties();
                this.updateCount();
            } else {
                this.properties = this.properties.filter(p => p.id !== id);
                this.renderProperties();
                this.updateCount();
            }
        } catch (error) {
            console.error('Failed to delete property:', error);
            this.properties = this.properties.filter(p => p.id !== id);
            this.renderProperties();
            this.updateCount();
        }
    },
    
    search() {
        const district = document.getElementById('filter-district').value;
        const type = document.getElementById('filter-type').value;
        const price = document.getElementById('filter-price').value;
        const status = document.getElementById('filter-status').value;
        
        let filtered = this.properties;
        
        if (district) {
            filtered = filtered.filter(p => p.district === district);
        }
        
        if (type) {
            filtered = filtered.filter(p => p.roomType && p.roomType.includes(type));
        }
        
        if (price) {
            const [min, max] = price.split('-').map(v => v === '+' ? Infinity : parseInt(v));
            filtered = filtered.filter(p => {
                const pPrice = p.price;
                if (max === undefined) return pPrice >= min;
                return pPrice >= min && pPrice < max;
            });
        }
        
        if (status) {
            filtered = filtered.filter(p => p.status === status);
        }
        
        this.properties = filtered;
        this.renderProperties();
    },
    
    promisify(obj, method, ...args) {
        return new Promise((resolve, reject) => {
            obj[method](...args, {
                success: (data) => resolve({ status: 'success', data }),
                failure: (error) => resolve({ status: 'error', message: error })
            });
        });
    }
};

document.addEventListener('DOMContentLoaded', () => {
    RealEstateForm.init();
});

window.RealEstateForm = RealEstateForm;
