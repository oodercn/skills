package net.ooder.skill.protocol.handler.vfs;

import net.ooder.skill.protocol.handler.AbstractCommandHandler;
import net.ooder.skill.protocol.handler.ErrorCodes;
import net.ooder.skill.protocol.model.AiBridgeMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CapVfsSyncCommandHandler extends AbstractCommandHandler {
    
    @Override
    public String getCommand() {
        return "cap.vfs.sync";
    }
    
    @Override
    protected AiBridgeMessage doHandle(AiBridgeMessage message) throws Exception {
        String capId = getParamAsString(message, "cap_id");
        String vfsPath = getParamAsString(message, "vfs_path");
        Boolean force = getParamAsBoolean(message, "force");
        
        if (capId == null || capId.isEmpty()) {
            return buildErrorResponse(message, ErrorCodes.INVALID_PARAMS, 
                "Missing required parameter: cap_id");
        }
        
        return buildErrorResponse(message, ErrorCodes.INTERNAL_ERROR, 
            "Capability-VFS synchronization not implemented yet. " +
            "This feature requires integration between CapabilityService and VfsManager. " +
            "Please refer to the collaboration document: E:\\github\\ooder-skills\\docs\\v3.0.1\\CAP_VFS_INTEGRATION_COLLABORATION.md");
    }
}
