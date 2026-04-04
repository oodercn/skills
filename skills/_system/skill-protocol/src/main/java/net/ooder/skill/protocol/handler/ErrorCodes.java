package net.ooder.skill.protocol.handler;

public class ErrorCodes {
    
    public static final int SUCCESS = 0;
    
    public static final int INTERNAL_ERROR = 500;
    
    public static final int INVALID_COMMAND = 400;
    
    public static final int NOT_FOUND = 404;
    
    public static final int UNAUTHORIZED = 401;
    
    public static final int FORBIDDEN = 403;
    
    public static final int BAD_REQUEST = 400;
    
    public static final int TIMEOUT = 408;
    
    public static final int CONFLICT = 409;
    
    public static final int INVALID_PARAMS = 1001;
    
    public static final int SKILL_NOT_FOUND = 2001;
    
    public static final int SKILL_INVOKE_ERROR = 2002;
    
    public static final int SCENE_NOT_FOUND = 3001;
    
    public static final int SCENE_JOIN_ERROR = 3002;
    
    public static final int AGENT_NOT_FOUND = 4001;
    
    public static final int AGENT_REGISTER_ERROR = 4002;
    
    public static final int CAP_NOT_FOUND = 5001;
    
    public static final int CAP_DECLARE_ERROR = 5002;
    
    public static final int RESOURCE_NOT_FOUND = 6001;
    
    public static final int RESOURCE_ACCESS_ERROR = 6002;
}
