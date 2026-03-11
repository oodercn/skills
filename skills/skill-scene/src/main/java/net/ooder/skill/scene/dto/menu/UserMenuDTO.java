package net.ooder.skill.scene.dto.menu;

import lombok.Data;
import java.util.List;

@Data
public class UserMenuDTO {
    private String userId;
    private String menuId;
    private String sceneGroupId;
    private String sceneName;
    private String role;
    private List<MenuItemDTO> items;
    private Long createTime;
    private Long updateTime;
}
