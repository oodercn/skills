package net.ooder.skill.tenant.context;

public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_USER_ID = new ThreadLocal<>();

    public static String getTenantId() {
        return CURRENT_TENANT_ID.get();
    }

    public static void setTenantId(String tenantId) {
        CURRENT_TENANT_ID.set(tenantId);
    }

    public static void clear() {
        CURRENT_TENANT_ID.remove();
        CURRENT_USER_ID.remove();
    }

    public static String getUserId() {
        return CURRENT_USER_ID.get();
    }

    public static void setUserId(String userId) {
        CURRENT_USER_ID.set(userId);
    }

    public static boolean hasTenant() {
        return CURRENT_TENANT_ID.get() != null;
    }
}
