package net.ooder.skill.protocol.handler.group;

import net.ooder.skill.protocol.handler.AbstractCommandHandler;
import net.ooder.skill.protocol.handler.ErrorCodes;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GroupDataGetCommandHandler extends AbstractCommandHandler {
    
    @Override
    public String getCommand() {
        return "group.data.get";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String groupId = getParamAsString(message, "group_id");
        String dataKey = getParamAsString(message, "data_key");
        
        if (groupId == null || groupId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: group_id");
        }
        
        if (dataKey == null || dataKey.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: data_key");
        }
        
        return buildErrorResponse(message, ErrorCodes.INTERNAL_ERROR, 
            "Group data management not implemented yet. " +
            "This feature requires GroupService enhancement to support custom data storage. " +
            "Please refer to the collaboration document for details.");
    }
}
