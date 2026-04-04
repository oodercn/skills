package net.ooder.skill.protocol.handler.vfs;

import net.ooder.skill.protocol.handler.AbstractCommandHandler;
import net.ooder.skill.protocol.handler.ErrorCodes;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CapVfsSyncStatusCommandHandler extends AbstractCommandHandler {
    
    @Override
    public String getCommand() {
        return "cap.vfs.sync.status";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String capId = getParamAsString(message, "cap_id");
        
        if (capId == null || capId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: cap_id");
        }
        
        return buildErrorResponse(message, ErrorCodes.INTERNAL_ERROR, 
            "VFS synchronization status not implemented yet. " +
            "This feature requires VFS service integration. " +
            "Please refer to the collaboration document for details.");
    }
}
