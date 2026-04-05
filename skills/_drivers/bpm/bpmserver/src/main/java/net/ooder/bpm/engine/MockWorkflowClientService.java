package net.ooder.bpm.engine;

import net.ooder.bpm.client.*;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.query.BPMCondition;
import net.ooder.bpm.enums.activityinst.ActivityInstRightAtt;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.bpm.enums.right.RightConditionEnums;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.bpm.enums.right.RightPermission;
import net.ooder.command.Command;
import net.ooder.common.Filter;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.config.ListResultModel;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.org.OrgManager;
import net.ooder.org.Person;
import net.ooder.server.JDSClientService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MockWorkflowClientService implements WorkflowClientService {

    private final String systemCode;
    private ConnectInfo connectInfo;

    public MockWorkflowClientService(String systemCode) {
        this.systemCode = systemCode;
    }

    @Override
    public String getSystemCode() {
        return systemCode;
    }

    @Override
    public JDSSessionHandle getSessionHandle() {
        return null;
    }

    @Override
    public void connect(ConnectInfo connInfo) throws JDSException {
        this.connectInfo = connInfo;
    }

    @Override
    public ReturnType disconnect() throws JDSException {
        this.connectInfo = null;
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ConnectInfo getConnectInfo() {
        return connectInfo;
    }

    @Override
    public OrgManager getOrgManager() {
        return null;
    }

    @Override
    public ListResultModel<List<ProcessDefVersion>> getProcessDefVersionList(BPMCondition condition, Filter filter, Map<RightCtx, Object> ctx) throws BPMException {
        return new ListResultModel<>();
    }

    @Override
    public ListResultModel<List<ProcessDef>> getProcessDefList(BPMCondition condition, Filter filter, Map<RightCtx, Object> ctx) throws BPMException {
        return new ListResultModel<>();
    }

    @Override
    public ProcessDef getProcessDef(String processDefID) throws BPMException {
        MockProcessDef def = new MockProcessDef();
        def.setProcessDefId(processDefID);
        def.setName("Mock Process Definition");
        def.setSystemCode(systemCode);
        return def;
    }

    @Override
    public ProcessDefVersion getProcessDefVersion(String processDefVersionID) throws BPMException {
        return null;
    }

    @Override
    public ActivityDef getActivityDef(String activityDefID) throws BPMException {
        MockActivityDef def = new MockActivityDef();
        def.setActivityDefId(activityDefID);
        def.setName("Mock Activity Definition");
        return def;
    }

    @Override
    public RouteDef getRouteDef(String routeDefId) throws BPMException {
        MockRouteDef def = new MockRouteDef();
        def.setRouteDefId(routeDefId);
        def.setName("Mock Route Definition");
        return def;
    }

    @Override
    public ListResultModel<List<ProcessInst>> getProcessInstList(BPMCondition condition, RightConditionEnums conditionEnus, Filter filter, Map<RightCtx, Object> ctx) throws BPMException {
        return new ListResultModel<>();
    }

    @Override
    public ListResultModel<List<ActivityInst>> getActivityInstList(BPMCondition condition, RightConditionEnums conditionEnus, Filter filter, Map<RightCtx, Object> ctx) throws BPMException {
        return new ListResultModel<>();
    }

    @Override
    public List<ActivityInst> getActivityInstListByOutActvityInstHistory(String activityInstHistoryId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public List<ActivityInstHistory> getActivityInstHistoryListByActvityInst(String actvityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public ListResultModel<List<ActivityInstHistory>> getActivityInstHistoryList(BPMCondition condition, RightConditionEnums conditionEnus, Filter filter, Map<RightCtx, Object> ctx) throws BPMException {
        return new ListResultModel<>();
    }

    @Override
    public List<ActivityInstHistory> getLastActivityInstHistoryListByActvityInst(String actvityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public List<ActivityInstHistory> getLastActivityInstHistoryListByActvityInst(String actvityInstId, Map<RightCtx, Object> ctx, boolean noSplit) throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public List<ActivityInstHistory> getAllOutActivityInstHistoryByActvityInstHistory(String historyHisroryId, boolean noSplit) throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public ListResultModel<List<ActivityInstHistory>> getActivityInstHistoryListByProcessInst(String processInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ListResultModel<>();
    }

    @Override
    public ProcessInst getProcessInst(String processInstID) throws BPMException {
        MockProcessInst inst = new MockProcessInst();
        inst.setProcessInstId(processInstID);
        inst.setName("Mock Process Instance");
        return inst;
    }

    @Override
    public ReturnType updateProcessInstName(String processInstId, String name) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType updateProcessInstUrgency(String processInstId, String urgency) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ActivityInst getActivityInst(String activityInstID) throws BPMException {
        MockActivityInst inst = new MockActivityInst();
        inst.setActivityInstId(activityInstID);
        inst.setName("Mock Activity Instance");
        return inst;
    }

    @Override
    public RouteInst getRouteInst(String routeInstId) throws BPMException {
        return null;
    }

    @Override
    public ActivityInstHistory getActivityInstHistory(String activityInstHistoryID) throws BPMException {
        return null;
    }

    @Override
    public ProcessInst newProcess(String processDefId, String processInstName, String processUrgency, Map<RightCtx, Object> ctx) throws BPMException {
        MockProcessInst inst = new MockProcessInst();
        inst.setProcessInstId("mock-process-inst-" + System.currentTimeMillis());
        inst.setName(processInstName);
        inst.setProcessDefId(processDefId);
        return inst;
    }

    @Override
    public ProcessInst newProcess(String processDefId, String processInstName, String processUrgency, String initType, Map<RightCtx, Object> ctx) throws BPMException {
        MockProcessInst inst = new MockProcessInst();
        inst.setProcessInstId("mock-process-inst-" + System.currentTimeMillis());
        inst.setName(processInstName);
        inst.setProcessDefId(processDefId);
        return inst;
    }

    @Override
    public List<RouteDef> getNextRoutes(String startActivityInstID, BPMCondition condition, Filter routeFilter, Map<RightCtx, Object> ctx) throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public ReturnType routeTo(String startActivityInstID, List<String> nextActivityDefIDs, List<Map<RightCtx, Object>> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ActivityInst copyActivityInstByHistory(String activityHistoryInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return null;
    }

    @Override
    public ActivityInst copyActivityInstByHistory(String activityHistoryInstId, Map<RightCtx, Object> ctx, boolean isnew) throws BPMException {
        return null;
    }

    @Override
    public ActivityInst newActivityInstByActivityDefId(String processInstId, String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {
        return null;
    }

    @Override
    public ReturnType copyTo(String activityHistoryInstId, List readers) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public boolean canTakeBack(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return false;
    }

    @Override
    public ReturnType takeBack(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public boolean canEndRead(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return false;
    }

    @Override
    public ReturnType endRead(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType endTask(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType abortedTask(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType deleteHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType restoreHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType clearHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public boolean canRouteBack(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return false;
    }

    @Override
    public List<ActivityInstHistory> getRouteBackActivityHistoryInstList(String activityInstID, Filter routeFilter, Map<RightCtx, Object> ctx) throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public ReturnType routeBack(String fromActivityInstID, String toActivityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public boolean canPerform(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return true;
    }

    @Override
    public boolean canSignReceive(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return false;
    }

    @Override
    public ReturnType signReceive(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType suspendActivityInst(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType resumeActivityInst(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType suspendProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType resumeProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType abortProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType completeProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType deleteProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public void beginTransaction() throws BPMException {
    }

    @Override
    public void commitTransaction() throws BPMException {
    }

    @Override
    public void rollbackTransaction() throws BPMException {
    }

    @Override
    public ActivityDefRight getActivityDefRightAttribute(String activityDefId) throws BPMException {
        return null;
    }

    @Override
    public List<AttributeDef> getActivityDefAttributes(String activityDefId) throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public ActivityDefEvent getActivityDefEventAttribute(String activityDefId) throws BPMException {
        return null;
    }

    @Override
    public ActivityDefDevice getActivityDefDeviceAttribute(String activityDefId) throws BPMException {
        return null;
    }

    @Override
    public List<Person> getActivityInstPersons(String activityInstId, ActivityInstRightAtt attName) throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public List<DeviceEndPoint> getActivityInstDevices(String activityInstId, ActivityInstRightAtt attName) throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public List<Command> getActivityInstCommands(String activityInstId) throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public List<Person> getActivityInstHistoryPersons(String activityInstHistoryId, ActivityInstHistoryAtt attName) throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public boolean queryPermissionToActivityInst(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return true;
    }

    @Override
    public List<RightPermission> queryAllPermissionToActivityInst(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ArrayList<>();
    }

    @Override
    public DataEngine getMapDAODataEngine() {
        return null;
    }

    @Override
    public DataMap getActivityInstFormValues(String activityInstID) throws BPMException {
        return null;
    }

    @Override
    public void updateActivityHistoryFormValues(String activityHistoryID, DataMap dataMap) throws BPMException {
    }

    @Override
    public DataMap getActivityHistoryFormValues(String activityHistoryID) throws BPMException {
        return null;
    }

    @Override
    public void updateActivityInstFormValues(String activityHistoryID, DataMap dataMap) throws BPMException {
    }

    @Override
    public DataMap getProcessInstFormValues(String processInstId) throws BPMException {
        return null;
    }

    @Override
    public void updateProcessInstFormValues(String processInstId, DataMap dataMap) throws BPMException {
    }

    @Override
    public ReturnType display(String activityInstId) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType addPersonTagToHistory(String activityInstHistoryID, ActivityInstHistoryAtt tagName, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType deletePersonTagToHistory(String activityInstHistoryID, ActivityInstHistoryAtt tagName, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ActivityDef getFirstActivityDefInProcess(String processDefVersionId) throws BPMException {
        return null;
    }

    @Override
    public void setOrgManager(OrgManager orgManager) {
    }

    @Override
    public FileEngine getfileEngine() {
        return null;
    }

    @Override
    public JDSClientService getJdsClient() {
        return null;
    }
}
