package net.ooder.bpm.engine;

import net.ooder.bpm.client.*;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.route.RouteCondition;
import net.ooder.bpm.enums.route.RouteDirction;
import net.ooder.annotation.Attributetype;

import java.util.ArrayList;
import java.util.List;

public class MockRouteDef implements RouteDef {

    private String routeDefId;
    private String processDefId;
    private String processDefVersionId;
    private String name;
    private String description;
    private String fromActivityDefId;
    private String toActivityDefId;
    private int routeOrder;
    private RouteDirction routeDirection;
    private String routeCondition;
    private RouteCondition routeConditionType;

    @Override
    public String getRouteDefId() {
        return routeDefId;
    }

    public void setRouteDefId(String routeDefId) {
        this.routeDefId = routeDefId;
    }

    @Override
    public String getProcessDefId() {
        return processDefId;
    }

    public void setProcessDefId(String processDefId) {
        this.processDefId = processDefId;
    }

    @Override
    public String getProcessDefVersionId() {
        return processDefVersionId;
    }

    public void setProcessDefVersionId(String processDefVersionId) {
        this.processDefVersionId = processDefVersionId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getFromActivityDefId() {
        return fromActivityDefId;
    }

    public void setFromActivityDefId(String fromActivityDefId) {
        this.fromActivityDefId = fromActivityDefId;
    }

    @Override
    public String getToActivityDefId() {
        return toActivityDefId;
    }

    public void setToActivityDefId(String toActivityDefId) {
        this.toActivityDefId = toActivityDefId;
    }

    @Override
    public int getRouteOrder() {
        return routeOrder;
    }

    public void setRouteOrder(int routeOrder) {
        this.routeOrder = routeOrder;
    }

    @Override
    public RouteDirction getRouteDirection() {
        return routeDirection != null ? routeDirection : RouteDirction.FORWARD;
    }

    public void setRouteDirection(RouteDirction routeDirection) {
        this.routeDirection = routeDirection;
    }

    @Override
    public String getRouteCondition() {
        return routeCondition;
    }

    public void setRouteCondition(String routeCondition) {
        this.routeCondition = routeCondition;
    }

    @Override
    public RouteCondition getRouteConditionType() {
        return routeConditionType != null ? routeConditionType : RouteCondition.CONDITION;
    }

    public void setRouteConditionType(RouteCondition routeConditionType) {
        this.routeConditionType = routeConditionType;
    }

    @Override
    public Object getWorkflowAttribute(String name) {
        return null;
    }

    @Override
    public Object getRightAttribute(String name) {
        return null;
    }

    @Override
    public String getAttribute(String name) {
        return null;
    }

    @Override
    public Object getAttribute(Attributetype attributetype, String name) {
        return null;
    }

    @Override
    public List<AttributeDef> getAllAttribute() {
        return new ArrayList<>();
    }

    @Override
    public Object getAppAttribute(String name) {
        return null;
    }

    @Override
    public List<Listener> getListeners() {
        return new ArrayList<>();
    }

    @Override
    public ActivityDef getFromActivityDef() throws BPMException {
        return null;
    }

    @Override
    public ActivityDef getToActivityDef() throws BPMException {
        return null;
    }

    @Override
    public ProcessDefVersion getProcessDefVersion() throws BPMException {
        return null;
    }

    @Override
    public ProcessDef getProcessDef() throws BPMException {
        return null;
    }

    @Override
    public boolean isToEnd() {
        return false;
    }
}
