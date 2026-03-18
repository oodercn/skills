package net.ooder.mvp.skill.scene.controller;

import net.ooder.mvp.skill.scene.dto.menu.MenuItemDTO;
import net.ooder.mvp.skill.scene.model.ResultModel;
import net.ooder.mvp.skill.scene.service.RoleManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthMenuController {

    @Autowired
    private RoleManagementService roleManagementService;

    @GetMapping("/menu-config")
    public ResultModel<List<Map<String, Object>>> getMenuConfig(
            @RequestParam(required = false) String role,
            HttpServletRequest request) {
        
        String roleType = role;
        if (roleType == null) {
            roleType = "collaborator";
        }
        
        List<MenuItemDTO> menuItems = roleManagementService.getMenusByRole(roleType);
        
        List<Map<String, Object>> menus = new ArrayList<>();
        for (MenuItemDTO item : menuItems) {
            Map<String, Object> menu = new HashMap<>();
            menu.put("id", item.getId());
            menu.put("name", item.getName());
            menu.put("path", item.getUrl());
            menu.put("icon", item.getIcon());
            menu.put("sort", item.getSort());
            menu.put("order", item.getOrder());
            menu.put("visible", item.isVisible());
            menu.put("children", item.getChildren());
            menus.add(menu);
        }
        
        return ResultModel.success(menus);
    }
}
