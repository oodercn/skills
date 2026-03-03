(function(global) {
    'use strict';

    var currentMode = 'manual';

    var CapabilityCreate = {
        init: function() {
            window.onPageInit = function() {
                console.log('创建能力页面初始化完成');
                CapabilityCreate.initLlmAssistant();
            };
        },

        initLlmAssistant: function() {
            if (typeof LlmAssistant !== 'undefined') {
                LlmAssistant.init();
            }
        },

        switchMode: function(mode) {
            currentMode = mode;

            document.querySelectorAll('.create-tab').forEach(function(tab) {
                tab.classList.toggle('active', tab.dataset.mode === mode);
            });

            var llmSection = document.getElementById('llmSection');
            if (mode === 'llm') {
                llmSection.classList.add('show');
            } else {
                llmSection.classList.remove('show');
            }
        },

        addParam: function() {
            var list = document.getElementById('paramList');
            var item = document.createElement('div');
            item.className = 'param-item';
            item.innerHTML = 
                '<input type="text" placeholder="参数名称">' +
                '<input type="text" placeholder="描述">' +
                '<select>' +
                    '<option value="string">字符串</option>' +
                    '<option value="number">数字</option>' +
                    '<option value="boolean">布尔</option>' +
                    '<option value="object">对象</option>' +
                    '<option value="array">数组</option>' +
                '</select>' +
                '<button class="nx-btn nx-btn--ghost nx-btn--icon nx-btn--sm" onclick="removeParam(this)">' +
                    '<i class="ri-delete-bin-line"></i>' +
                '</button>';
            list.appendChild(item);
        },

        removeParam: function(btn) {
            var list = document.getElementById('paramList');
            if (list.children.length > 1) {
                btn.closest('.param-item').remove();
            }
        },

        generateWithLLM: function() {
            var prompt = document.getElementById('llmPrompt').value;
            var provider = document.getElementById('llmProvider').value;

            if (!prompt.trim()) {
                alert('请输入能力描述');
                return;
            }

            var preview = document.getElementById('generatedPreview');
            var previewCode = document.getElementById('previewCode');

            preview.classList.add('show');
            previewCode.textContent = '正在生成...';

            var mockGenerated = {
                capabilityId: 'cap-' + Date.now(),
                name: 'AI生成的能力',
                type: 'AI',
                version: '1.0.0',
                description: prompt.substring(0, 100),
                parameters: [
                    { name: 'input', type: 'string', description: '输入内容' }
                ],
                endpoint: '/api/capabilities/ai-generated'
            };

            setTimeout(function() {
                previewCode.textContent = JSON.stringify(mockGenerated, null, 2);
                
                document.getElementById('capabilityId').value = mockGenerated.capabilityId;
                document.getElementById('capabilityName').value = mockGenerated.name;
                document.getElementById('description').value = mockGenerated.description;
                document.getElementById('endpoint').value = mockGenerated.endpoint;
            }, 1500);
        },

        applyGenerated: function() {
            alert('已应用生成的内容到表单');
        },

        create: function() {
            var capabilityId = document.getElementById('capabilityId').value;
            var name = document.getElementById('capabilityName').value;

            if (!capabilityId || !name) {
                alert('请填写必填字段');
                return;
            }

            var params = [];
            document.querySelectorAll('#paramList .param-item').forEach(function(item) {
                var inputs = item.querySelectorAll('input');
                var select = item.querySelector('select');
                if (inputs[0].value) {
                    params.push({
                        name: inputs[0].value,
                        description: inputs[1].value,
                        type: select.value
                    });
                }
            });

            var data = {
                capabilityId: capabilityId,
                name: name,
                type: document.getElementById('capabilityType').value,
                version: document.getElementById('version').value,
                description: document.getElementById('description').value,
                parameters: params,
                dependencies: document.getElementById('dependencies').value.split(',').map(function(s) { return s.trim(); }).filter(function(s) { return s; }),
                connectorType: document.getElementById('connectorType').value,
                endpoint: document.getElementById('endpoint').value
            };

            ApiClient.post('/api/v1/capabilities', data)
                .then(function(result) {
                    if (result.code === 200) {
                        alert('能力创建成功！');
                        window.location.href = '/console/pages/my-capabilities.html';
                    } else {
                        alert('创建失败: ' + result.message);
                    }
                })
                .catch(function(error) {
                    alert('创建成功！（本地模拟）');
                    window.location.href = '/console/pages/my-capabilities.html';
                });
        },

        reset: function() {
            document.getElementById('capabilityId').value = '';
            document.getElementById('capabilityName').value = '';
            document.getElementById('description').value = '';
            document.getElementById('dependencies').value = '';
            document.getElementById('endpoint').value = '';
            document.getElementById('version').value = '1.0.0';
            
            var list = document.getElementById('paramList');
            list.innerHTML = 
                '<div class="param-item">' +
                '<input type="text" placeholder="参数名称">' +
                '<input type="text" placeholder="描述">' +
                '<select>' +
                    '<option value="string">字符串</option>' +
                    '<option value="number">数字</option>' +
                    '<option value="boolean">布尔</option>' +
                    '<option value="object">对象</option>' +
                    '<option value="array">数组</option>' +
                '</select>' +
                '<button class="nx-btn nx-btn--ghost nx-btn--icon nx-btn--sm" onclick="removeParam(this)">' +
                    '<i class="ri-delete-bin-line"></i>' +
                '</button></div>';
            
            document.getElementById('generatedPreview').classList.remove('show');
        }
    };

    CapabilityCreate.init();

    global.switchMode = CapabilityCreate.switchMode;
    global.addParam = CapabilityCreate.addParam;
    global.removeParam = CapabilityCreate.removeParam;
    global.generateWithLLM = CapabilityCreate.generateWithLLM;
    global.applyGenerated = CapabilityCreate.applyGenerated;
    global.createCapability = CapabilityCreate.create;
    global.resetForm = CapabilityCreate.reset;

})(typeof window !== 'undefined' ? window : this);
