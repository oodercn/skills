package net.ooder.skill.protocol.handler.scene;

import net.ooder.skill.protocol.handler.AbstractCommandHandler;
import net.ooder.skill.protocol.handler.ErrorCodes;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import net.ooder.skill.scenes.dto.SceneDTO;
import net.ooder.skill.scenes.service.SceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SceneQueryCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private SceneService sceneService;
    
    @Override
    public String getCommand() {
        return "scene.query";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String sceneId = getParamAsString(message, "scene_id");
        String status = getParamAsString(message, "status");
        Integer pageNum = getParamAsInteger(message, "page_num");
        Integer pageSize = getParamAsInteger(message, "page_size");
        
        if (sceneId != null && !sceneId.isEmpty()) {
            SceneDTO scene = sceneService.get(sceneId);
            if (scene == null) {
                return buildErrorResponse(message, ErrorCodes.SCENE_NOT_FOUND, 
                    "Scene not found: " + sceneId);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("scene_id", scene.getSceneId());
            response.put("name", scene.getName());
            response.put("description", scene.getDescription());
            response.put("status", scene.getStatus());
            response.put("type", scene.getType());
            response.put("owner_id", scene.getOwnerId());
            
            return buildSuccessResponse(message, response);
        } else {
            int page = pageNum != null ? pageNum : 1;
            int size = pageSize != null ? pageSize : 20;
            
            java.util.List<SceneDTO> scenes = sceneService.listScenes(status, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("scenes", scenes);
            response.put("total", scenes.size());
            response.put("page_num", page);
            response.put("page_size", size);
            
            return buildSuccessResponse(message, response);
        }
    }
}
