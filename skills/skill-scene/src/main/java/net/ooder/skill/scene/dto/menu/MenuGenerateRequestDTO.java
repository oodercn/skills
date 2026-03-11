package net.ooder.skill.scene.dto.menu;

import lombok.Data;
import java.util.List;

@Data
public class MenuGenerateRequestDTO {
    private String userId;
    private String sceneGroupId;
    private String sceneName;
    private String role;
    private String templateId;
    private List<MenuItemDTO> customItems;
}
