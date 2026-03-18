package net.ooder.mvp.skill.scene.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.ooder.mvp.skill.scene.model.ResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthMenuController {

    @Autowired
    private ResourceLoader resourceLoader;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/menu-config")
    public ResultModel<List<Map<String, Object>>> getMenuConfig(
            @RequestParam(required = false) String role,
            HttpServletRequest request) {
        
        String roleType = role;
        if (roleType == null) {
            roleType = "collaborator";
        }
        
        try {
            List<Map<String, Object>> menuItems = loadAndFilterMenuConfig(roleType);
            return ResultModel.success(menuItems);
        } catch (IOException e) {
            return ResultModel.error("加载菜单配置失败: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> loadAndFilterMenuConfig(String roleType) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:static/console/menu-config.json");
        JsonNode rootNode = objectMapper.readTree(resource.getInputStream());
        JsonNode menuNode = rootNode.get("menu");
        
        List<Map<String, Object>> result = new ArrayList<>();
        if (menuNode != null && menuNode.isArray()) {
            for (JsonNode item : menuNode) {
                Map<String, Object> filteredItem = filterMenuItem(item, roleType);
                if (filteredItem != null) {
                    result.add(filteredItem);
                }
            }
        }
        
        return result;
    }

    private Map<String, Object> filterMenuItem(JsonNode item, String roleType) {
        JsonNode rolesNode = item.get("roles");
        if (rolesNode == null || !rolesNode.isArray()) {
            return null;
        }
        
        boolean hasRole = false;
        for (JsonNode r : rolesNode) {
            if (r.asText().equals(roleType)) {
                hasRole = true;
                break;
            }
        }
        
        if (!hasRole) {
            return null;
        }
        
        JsonNode statusNode = item.get("status");
        if (statusNode != null && !"implemented".equals(statusNode.asText())) {
            return null;
        }
        
        Map<String, Object> result = new LinkedHashMap<>();
        
        if (item.has("id")) result.put("id", item.get("id").asText());
        if (item.has("name")) result.put("name", item.get("name").asText());
        if (item.has("url")) result.put("url", item.get("url").asText());
        if (item.has("icon")) result.put("icon", item.get("icon").asText());
        if (item.has("sort")) result.put("sort", item.get("sort").asInt());
        if (item.has("order")) result.put("order", item.get("order").asInt());
        result.put("visible", true);
        result.put("active", false);
        
        JsonNode childrenNode = item.get("children");
        if (childrenNode != null && childrenNode.isArray()) {
            List<Map<String, Object>> children = new ArrayList<>();
            for (JsonNode child : childrenNode) {
                Map<String, Object> filteredChild = filterMenuItem(child, roleType);
                if (filteredChild != null) {
                    children.add(filteredChild);
                }
            }
            if (!children.isEmpty()) {
                result.put("children", children);
            } else {
                result.put("children", new ArrayList<>());
            }
        } else {
            result.put("children", new ArrayList<>());
        }
        
        return result;
    }
}
