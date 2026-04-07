package net.ooder.skill.workflow.core;

import net.ooder.bpm.client.ActivityDef;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformtype;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.skill.tenant.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BpmRightContext {

    private static final Logger log = LoggerFactory.getLogger(BpmRightContext.class);

    public static Map<RightCtx, Object> build() {
        Map<RightCtx, Object> ctx = new HashMap<>();
        injectTenantId(ctx);
        injectCurrentUser(ctx);
        return ctx;
    }

    public static Map<RightCtx, Object> buildWithPerformers(List<String> performerIds, List<String> readerIds) {
        Map<RightCtx, Object> ctx = build();
        if (performerIds != null && !performerIds.isEmpty()) {
            ctx.put(RightCtx.PERFORMERS, performerIds);
        }
        if (readerIds != null && !readerIds.isEmpty()) {
            ctx.put(RightCtx.READERS, readerIds);
        }
        return ctx;
    }

    public static Map<RightCtx, Object> buildForRoute(WorkflowClientService bpmClient,
                                                       String activityInstId,
                                                       String nextActivityDefId,
                                                       List<String> performerIds,
                                                       List<String> readerIds) throws BPMException {
        if (bpmClient == null) return buildWithPerformers(performerIds, readerIds);

        Map<RightCtx, Object> ctx = build();

        if (nextActivityDefId != null) {
            try {
                ActivityDef nextActivityDef = bpmClient.getActivityDef(nextActivityDefId);
                if (nextActivityDef != null) {
                    Object rightAttr = invokeGetRightAttribute(nextActivityDef);
                    if (rightAttr != null) {
                        ActivityDefPerformtype performType = extractPerformType(rightAttr);
                        log.debug("[buildForRoute] nextActivityDefId={}, performType={}", nextActivityDefId, performType);

                        switch (performType != null ? performType : ActivityDefPerformtype.SINGLE) {
                            case NOSELECT:
                                ctx.put(RightCtx.PERFORMERS, Collections.emptyList());
                                break;
                            case NEEDNOTSELECT:
                                ctx.put(RightCtx.PERFORMERS, Collections.emptyList());
                                break;
                            case MULTIPLE:
                                if (performerIds != null && !performerIds.isEmpty()) {
                                    ctx.put(RightCtx.PERFORMERS, performerIds);
                                } else {
                                    log.warn("[buildForRoute] MULTIPLE mode but no performerIds provided for {}", nextActivityDefId);
                                }
                                break;
                            case JOINTSIGN:
                                if (performerIds != null && !performerIds.isEmpty()) {
                                    ctx.put(RightCtx.PERFORMERS, performerIds);
                                } else {
                                    log.warn("[buildForRoute] JOINTSIGN mode but no performerIds provided for {}", nextActivityDefId);
                                }
                                break;
                            case SINGLE:
                            default:
                                if (performerIds != null && !performerIds.isEmpty()) {
                                    ctx.put(RightCtx.PERFORMERS, performerIds);
                                }
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("[buildForRoute] Failed to resolve performType for activityDefId={}, using defaults: {}",
                    nextActivityDefId, e.getMessage());
            }
        }

        if (performerIds != null && !performerIds.isEmpty() && !ctx.containsKey(RightCtx.PERFORMERS)) {
            ctx.put(RightCtx.PERFORMERS, performerIds);
        }
        if (readerIds != null && !readerIds.isEmpty()) {
            ctx.put(RightCtx.READERS, readerIds);
        }

        return ctx;
    }

    private static void injectTenantId(Map<RightCtx, Object> ctx) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId != null && !tenantId.isEmpty()) {
            try {
                ctx.put(RightCtx.valueOf("TENANT_ID"), tenantId);
            } catch (IllegalArgumentException e) {
                log.debug("[BpmRightContext] TENANT_ID enum not available, skipping");
            }
        }
    }

    private static void injectCurrentUser(Map<RightCtx, Object> ctx) {
        String userId = TenantContext.getUserId();
        if (userId != null && !userId.isEmpty()) {
            ctx.put(RightCtx.USERID, userId);
        }
    }

    @SuppressWarnings("unchecked")
    private static Object invokeGetRightAttribute(ActivityDef activityDef) throws Exception {
        try {
            java.lang.reflect.Method m = activityDef.getClass().getMethod("getRightAttribute");
            return m.invoke(activityDef);
        } catch (NoSuchMethodException e1) {
            try {
                java.lang.reflect.Method m = activityDef.getClass().getMethod("getRight");
                return m.invoke(activityDef);
            } catch (NoSuchMethodException e2) {
                return null;
            }
        }
    }

    private static ActivityDefPerformtype extractPerformType(Object rightAttr) {
        if (rightAttr == null) return null;
        try {
            java.lang.reflect.Method m = rightAttr.getClass().getMethod("getPerformType");
            Object result = m.invoke(rightAttr);
            if (result instanceof ActivityDefPerformtype) return (ActivityDefPerformtype) result;
            if (result instanceof Enum) {
                String name = ((Enum<?>) result).name();
                try { return ActivityDefPerformtype.valueOf(name); }
                catch (IllegalArgumentException ignored) {}
            }
        } catch (Exception e) {
            log.trace("[extractPerformType] reflection error: {}", e.getMessage());
        }
        return null;
    }

    public static boolean hasTenant() {
        String id = TenantContext.getTenantId();
        return id != null && !id.isEmpty();
    }

    public static String getCurrentUserId() {
        return TenantContext.getUserId();
    }

    public static String resolveCurrentUserId(WorkflowClientService bpmClient) {
        String userId = TenantContext.getUserId();
        if (userId != null && !userId.isEmpty()) return userId;
        if (bpmClient != null) {
            try {
                Object sysUser = bpmClient.getClass().getMethod("getSystemUser").invoke(bpmClient);
                if (sysUser != null) return sysUser.toString();
            } catch (Exception ignored) {}
        }
        return "system";
    }
}
