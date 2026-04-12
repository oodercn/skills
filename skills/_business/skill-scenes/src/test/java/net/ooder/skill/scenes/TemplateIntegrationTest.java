package net.ooder.skill.scenes;

import net.ooder.scene.group.template.SqlSceneGroupTemplateManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TemplateIntegrationTest {

    @Autowired
    private SqlSceneGroupTemplateManager templateManager;

    @Test
    public void testTemplateManagerInjection() {
        assertNotNull(templateManager, "SqlSceneGroupTemplateManager should be injected");
    }

    @Test
    public void testCreateTemplate() {
        String name = "测试模板";
        String description = "这是一个测试模板";
        String category = "test";
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("participants", new Object[]{});
        templateData.put("capabilities", new Object[]{});
        
        String templateId = templateManager.createTemplate(name, description, category, templateData);
        
        assertNotNull(templateId, "Template ID should not be null");
    }

    @Test
    public void testGetTemplate() {
        String name = "查询测试模板";
        String description = "测试查询功能";
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("testKey", "testValue");
        
        String templateId = templateManager.createTemplate(name, description, "test", templateData);
        
        Map<String, Object> retrieved = templateManager.getTemplate(templateId);
        
        assertNotNull(retrieved, "Template should be retrieved");
        assertEquals(name, retrieved.get("name"), "Template name should match");
    }

    @Test
    public void testUpdateTemplate() {
        String name = "更新测试模板";
        String description = "测试更新功能";
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("initial", "data");
        
        String templateId = templateManager.createTemplate(name, description, "test", templateData);
        
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("name", "更新后的模板名称");
        updateData.put("description", "更新后的描述");
        
        boolean updated = templateManager.updateTemplate(templateId, updateData);
        assertTrue(updated, "Template should be updated");
    }

    @Test
    public void testDeleteTemplate() {
        String name = "删除测试模板";
        String description = "测试删除功能";
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("test", "data");
        
        String templateId = templateManager.createTemplate(name, description, "test", templateData);
        
        boolean deleted = templateManager.deleteTemplate(templateId);
        assertTrue(deleted, "Template should be deleted");
        
        Map<String, Object> retrieved = templateManager.getTemplate(templateId);
        assertNull(retrieved, "Deleted template should not be found");
    }

    @Test
    public void testListTemplates() {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("test", "data");
        
        templateManager.createTemplate("列表测试模板1", "测试1", "list-test", templateData);
        templateManager.createTemplate("列表测试模板2", "测试2", "list-test", templateData);
        
        List<Map<String, Object>> templates = templateManager.listTemplates("list-test");
        
        assertNotNull(templates, "Templates should not be null");
        assertTrue(templates.size() >= 2, "Should have at least 2 templates");
    }

    @Test
    public void testCloneTemplate() {
        String name = "克隆测试模板";
        String description = "测试克隆功能";
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("clone", "test");
        
        String templateId = templateManager.createTemplate(name, description, "test", templateData);
        
        String clonedId = templateManager.cloneTemplate(templateId, "克隆的模板", "克隆的描述");
        
        assertNotNull(clonedId, "Cloned template ID should not be null");
        assertNotEquals(templateId, clonedId, "Cloned ID should be different");
    }
}
