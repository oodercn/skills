package net.ooder.skill.scene.dto.menu;

import lombok.Data;
import java.util.List;

@Data
public class MenuItemDTO {
    private String id;
    private String parentId;
    private String parentRoleId;
    private String name;
    private String icon;
    private String url;
    private int order;
    private int sort;
    private boolean visible;
    private boolean active;
    private int level;
    private List<MenuItemDTO> children;
}
