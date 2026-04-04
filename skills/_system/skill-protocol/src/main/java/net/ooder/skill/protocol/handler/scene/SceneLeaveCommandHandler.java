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
public class SceneLeaveCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private SceneService sceneService;
    
    @Override
    public String getCommand() {
        return "scene.leave";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String sceneId = getParamAsString(message, "scene_id");
        String userId = getParamAsString(message, "user_id");
        
        if (sceneId == null || sceneId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: scene_id");
        }
        
        if (userId == null || userId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: user_id");
        }
        
        SceneDTO scene = sceneService.get(sceneId);
        if (scene == null) {
            return buildErrorResponse(message, ErrorCodes.SCENE_NOT_FOUND, 
                "Scene not found: " + sceneId);
        }
        
        boolean success = sceneService.removeCollaborativeUser(sceneId, userId);
        
        if (!success) {
            return buildErrorResponse(message, ErrorCodes.SCENE_JOIN_ERROR, 
                "Failed to leave scene: " + sceneId);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("scene_id", sceneId);
        response.put("user_id", userId);
        response.put("status", "left");
        response.put("message", "Successfully left scene");
        
        return buildSuccessResponse(message, response);
    }
}
