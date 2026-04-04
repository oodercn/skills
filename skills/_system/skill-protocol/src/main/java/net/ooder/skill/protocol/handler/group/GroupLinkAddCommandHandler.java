package net.ooder.skill.protocol.handler.group;

import net.ooder.skill.protocol.handler.AbstractCommandHandler;
import net.ooder.skill.protocol.handler.ErrorCodes;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GroupLinkAddCommandHandler extends AbstractCommandHandler {
    
    @Override
    public String getCommand() {
        return "group.link.add";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String groupId = getParamAsString(message, "group_id");
        String linkId = getParamAsString(message, "link_id");
        String linkType = getParamAsString(message, "link_type");
        
        if (groupId == null || groupId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: group_id");
        }
        
        if (linkId == null || linkId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: link_id");
        }
        
        return buildErrorResponse(message, ErrorCodes.INTERNAL_ERROR, 
            "Group link management not implemented yet. " +
            "This feature requires GroupService enhancement to support link management. " +
            "Please refer to the collaboration document for details.");
    }
}
