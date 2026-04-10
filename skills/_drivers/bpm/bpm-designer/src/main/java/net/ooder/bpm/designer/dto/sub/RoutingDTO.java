package net.ooder.bpm.designer.dto.sub;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.Map;

/**
 * 路由配置DTO
 */
public class RoutingDTO {

    @JSONField(name = "join")
    private String join;

    @JSONField(name = "split")
    private String split;

    @JSONField(name = "canRouteBack")
    private String canRouteBack;

    @JSONField(name = "routeBackMethod")
    private String routeBackMethod;

    @JSONField(name = "canSpecialSend")
    private String canSpecialSend;

    @JSONField(name = "specialScope")
    private String specialScope;

    @JSONField(name = "defaultRoute")
    private String defaultRoute;

    @JSONField(name = "parallelMode")
    private String parallelMode;

    @JSONField(name = "mergeCondition")
    private String mergeCondition;

    @JSONField(name = "extendedAttributes")
    private Map<String, Object> extendedAttributes;

    public String getJoin() {
        return join;
    }

    public void setJoin(String join) {
        this.join = join;
    }

    public String getSplit() {
        return split;
    }

    public void setSplit(String split) {
        this.split = split;
    }

    public String getCanRouteBack() {
        return canRouteBack;
    }

    public void setCanRouteBack(String canRouteBack) {
        this.canRouteBack = canRouteBack;
    }

    public String getRouteBackMethod() {
        return routeBackMethod;
    }

    public void setRouteBackMethod(String routeBackMethod) {
        this.routeBackMethod = routeBackMethod;
    }

    public String getCanSpecialSend() {
        return canSpecialSend;
    }

    public void setCanSpecialSend(String canSpecialSend) {
        this.canSpecialSend = canSpecialSend;
    }

    public String getSpecialScope() {
        return specialScope;
    }

    public void setSpecialScope(String specialScope) {
        this.specialScope = specialScope;
    }

    public String getDefaultRoute() {
        return defaultRoute;
    }

    public void setDefaultRoute(String defaultRoute) {
        this.defaultRoute = defaultRoute;
    }

    public String getParallelMode() {
        return parallelMode;
    }

    public void setParallelMode(String parallelMode) {
        this.parallelMode = parallelMode;
    }

    public String getMergeCondition() {
        return mergeCondition;
    }

    public void setMergeCondition(String mergeCondition) {
        this.mergeCondition = mergeCondition;
    }

    public Map<String, Object> getExtendedAttributes() {
        return extendedAttributes;
    }

    public void setExtendedAttributes(Map<String, Object> extendedAttributes) {
        this.extendedAttributes = extendedAttributes;
    }
}
