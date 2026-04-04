package net.ooder.skill.protocol.handler.group;

import net.ooder.skill.group.service.GroupService;
import net.ooder.skill.protocol.handler.AbstractCommandHandler;
import net.ooder.skill.protocol.handler.ErrorCodes;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GroupMemberAddCommandHandler extends AbstractCommandHandler {
    
    @Autowired
    private GroupService groupService;
    
    @Override
    public String getCommand() {
        return "group.member.add";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String groupId = getParamAsString(message, "group_id");
        String userId = getParamAsString(message, "user_id");
        String userName = getParamAsString(message, "user_name");
        
        if (groupId == null || groupId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: group_id");
        }
        
        if (userId == null || userId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: user_id");
        }
        
        if (userName == null || userName.isEmpty()) {
            userName = userId;
        }
        
        boolean success = groupService.addMember(groupId, userId, userName);
        
        if (!success) {
            return buildErrorResponse(message, ErrorCodes.INTERNAL_ERROR, 
                "Failed to add member to group: " + groupId);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("group_id", groupId);
        response.put("user_id", userId);
        response.put("status", "added");
        response.put("message", "Member added successfully");
        
        return buildSuccessResponse(message, response);
    }
}
