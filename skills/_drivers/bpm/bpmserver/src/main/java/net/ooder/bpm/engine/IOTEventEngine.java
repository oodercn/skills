
package net.ooder.bpm.engine;

import net.ooder.annotation.AttributeName;
import net.ooder.bpm.client.ActivityDefEvent;
import net.ooder.bpm.engine.database.event.*;
import net.ooder.bpm.engine.database.expression.DbParticipantSelect;
import net.ooder.bpm.engine.database.expression.DbParticipantSelectManager;
import net.ooder.bpm.engine.database.right.DbProcessDefVersionRightManager;
import net.ooder.bpm.engine.inter.*;
import net.ooder.bpm.engine.proxy.ActivityDefEventProxy;
import net.ooder.bpm.enums.activityinst.ActivityInstRightAtt;
import net.ooder.bpm.enums.event.DeviceAPIEventEnums;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.bpm.enums.right.RightGroupEnums;
import net.ooder.bpm.enums.right.RightPerformStatus;
import net.ooder.command.Command;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.common.expression.ExpressionParser;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.esb.config.manager.JDSExpressionParserManager;
import net.ooder.agent.client.iot.Device;
import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.ct.CtIotFactory;
import net.ooder.agent.client.iot.ct.CtIotService;
import net.ooder.msg.Msg;
import net.ooder.org.OrgManager;
import net.ooder.server.JDSServer;
import net.ooder.server.OrgManagerFactory;
import net.ooder.vfs.ct.CtVfsFactory;
import net.ooder.vfs.ct.CtVfsService;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;

public class IOTEventEngine implements EventEngine, Serializable {
    Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, IOTEventEngine.class);

    // process relevent manager
    public EIProcessDefManager processDefMgr = null;

    public EIProcessDefVersionManager processDefVerMgr = null;

    public EIActivityDefManager activityDefMgr = null;

    public EIRouteDefManager routeDefMgr = null;

    public EIProcessInstManager processInstMgr = null;

    public EIActivityInstManager activityInstMgr = null;

    public DbActivityInstEventManager actEventMgr = null;

    public DbActivityHistoryEventManager historyEventMgr = null;

    public EIActivityInstHistoryManager activityInstHistoryMgr = null;

    public EIRouteInstManager routeInstMgr = null;

    public DbProcessDefVersionRightManager processRightMgr = null;

    public DbActivityDefEventManager activityEventMgr = null;

    public DbParticipantSelectManager participantMgr = null;

    // Workflw Engine
    public WorkflowEngine workflowEngine = null;

    // Org Manager
    public OrgManager orgManager = null;

    private String systemCode;

    public IOTEventEngine(String systemCode) {
        this.systemCode = systemCode;
        processDefMgr = EIProcessDefManager.getInstance();
        processDefVerMgr = EIProcessDefVersionManager.getInstance();
        activityDefMgr = EIActivityDefManager.getInstance();
        routeDefMgr = EIRouteDefManager.getInstance();
        processInstMgr = EIProcessInstManager.getInstance();
        activityInstMgr = EIActivityInstManager.getInstance();
        activityInstHistoryMgr = EIActivityInstHistoryManager.getInstance();
        routeInstMgr = EIRouteInstManager.getInstance();
        processRightMgr = DbProcessDefVersionRightManager.getInstance();
        activityEventMgr = DbActivityDefEventManager.getInstance();
        participantMgr = DbParticipantSelectManager.getInstance();
        actEventMgr = DbActivityInstEventManager.getInstance();
        workflowEngine = WorkflowEngineImpl.getEngine(systemCode);
        orgManager = OrgManagerFactory.getOrgManager(JDSServer.getClusterClient().getSystem(systemCode).getConfigname());

    }

    private static IOTEventEngine engine = null;

    public static IOTEventEngine getEngine(String systemCode) {
        if (engine == null) {
            synchronized (IOTEventEngine.class) {
                if (engine == null) {
                    engine = new IOTEventEngine(systemCode);
                }
            }
        }
        return engine;
    }

    @Override
    public ReturnType routeTo(String activityInstId, String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {
        List<String> performers = new ArrayList<String>();
        if (ctx == null || ctx.get(RightCtx.PERFORMERS) == null) {
            List<DeviceEndPoint> endpints = (List<DeviceEndPoint>) this.getSelectDefEndPoint(activityDefId, ctx);
            for (DeviceEndPoint ep : endpints) {
                performers.add(ep.getEndPointId());
            }
        }

        List readers = null;
        if (ctx != null && ctx.get(RightCtx.READERS) != null) {
            readers = new ArrayList((List) ctx.get(RightCtx.READERS));
        }
        EIActivityInst eiActivityInst = activityInstMgr.loadByKey(activityInstId);
        DbActivityDefEvent activityDefRight = activityEventMgr.loadByKey(activityDefId);
        String processInstId = eiActivityInst.getProcessInstId();

        // 加入办理人
        try {
            for (String endPointId : performers) {

                DeviceEndPoint endPoint = this.getIotClient().getEndPointById(endPointId);

                ctx.put(RightCtx.sensorieee, endPoint.getIeeeaddress());

                DbActivityInstEvent performer = actEventMgr.createActivityInstEvent();
                performer.setActivityInstEndPointId(UUID.randomUUID().toString());
                performer.setProcessInstId(processInstId);
                performer.setActivityInstId(activityInstId);
                performer.setEventActivityState(RightPerformStatus.WAITING);
                performer.setEndPointId(endPointId);

                performer.setEventGrpCode(RightGroupEnums.PERFORMER);
                actEventMgr.save(performer);

            }
        } catch (SQLException e) {
            throw new BPMException("save performer failed when process routeTo!", e);
        } catch (HomeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (readers != null) {
            // 加入读者
            try {
                for (Iterator it = readers.iterator(); it.hasNext(); ) {
                    String readerId = (String) it.next();
                    DbActivityInstEvent reader = actEventMgr.createActivityInstEvent();
                    reader.setActivityInstEndPointId(UUID.randomUUID().toString());
                    reader.setProcessInstId(processInstId);
                    reader.setActivityInstId(activityInstId);
                    reader.setEventActivityState(RightPerformStatus.NULL);
                    reader.setEndPointId(readerId);
                    reader.setEventGrpCode(RightGroupEnums.READER);

                    actEventMgr.save(reader);
                }
            } catch (SQLException e) {
                throw new BPMException("save reader failed when process routeTo!", e);
            }
        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    @Override
    public ReturnType tackBack(String activityInstID, Map rightCtx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType endTask(String activityInstID, String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType abortedTask(String activityInstID, String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public boolean canSignReceive(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public ReturnType signReceive(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {

        if (ctx == null || ctx.get(RightCtx.sensorieee) == null) {
            return new ReturnType(ReturnType.MAINCODE_SUCCESS);

        }

        String userId = (String) ctx.get(RightCtx.sensorieee);
        EIActivityInst activityInst = activityInstMgr.loadByKey(activityInstID);

        String sql = " where ACTIVITYINST_ID = '" + activityInstID + "' and DEVICE_GRP_CODE = '" + RightGroupEnums.PERFORMER + "' and ENDPOINT_ID = '" + userId + "' and DEVICE_ACTIVITY_STATE = '" + RightPerformStatus.WAITING + "'";
        DbActivityInstEvent[] enpoints;
        try {
            enpoints = actEventMgr.loadByWhere(sql);
            if (enpoints.length == 0) {
                return new ReturnType(ReturnType.MAINCODE_FAIL);
            } else {
                enpoints[0].setEventActivityState(RightPerformStatus.CURRENT);
                actEventMgr.save(enpoints[0]);

                EIActivityInst actInst = activityInstMgr.loadByKey(activityInstID);
                List routeInsts = routeInstMgr.getRouteInsts(actInst);

                for (Iterator it = routeInsts.iterator(); it.hasNext(); ) {
                    EIRouteInst routeInst = (EIRouteInst) it.next();
                    DbActivityHistoryEvent historyEvent = historyEventMgr.createActivityHistoryEvent(enpoints[0]);
                    historyEvent.setActivityInstHistoryId(routeInst.getFromActivityId());
                    historyEvent.setEventGrpCode(RightGroupEnums.READER);
                    historyEventMgr.save(historyEvent);
                }
            }
        } catch (SQLException e) {
            throw new BPMException("load person from activityInst failed!", e);
        }

        String finishOtherSql = " where ACTIVITYINST_ID = '" + activityInstID + "'" + " and DEVICE_GRP_CODE = '" + RightGroupEnums.PERFORMER + "'" + " and DEVICE_ACTIVITY_STATE = '" + RightPerformStatus.WAITING + "'";
        try {
            enpoints = actEventMgr.loadByWhere(finishOtherSql);
            for (int i = 0; i < enpoints.length; i++) {
                enpoints[i].setEventGrpCode(RightGroupEnums.READER);
            }
            actEventMgr.save(enpoints);
        } catch (SQLException e) {
            throw new BPMException("load person from activityInst failed!", e);
        }

        // 清除活动实例中的所有的LAST_RIGHT_GRP
        String restorePersonSql = " where ACTIVITYINST_ID = '" + activityInstID + "' AND LAST_DEVICE_GRP IS NOT NULL ";
        try {
            DbActivityInstEvent[] restorEndPoints = actEventMgr.loadByWhere(restorePersonSql);
            for (int i = 0; i < restorEndPoints.length; i++) {
                DbActivityInstEvent person = restorEndPoints[i];
                person.setLastEventGrp(RightGroupEnums.NULL);
            }
            actEventMgr.save(restorEndPoints);
        } catch (SQLException e) {
            throw new BPMException("restore current performers and readers failed!", e);
        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    public List<DeviceEndPoint> getSelectDefEndPoint(String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {
        DbActivityDefEvent eventwf = activityEventMgr.loadByKey(activityDefId);
        return getParticipant(eventwf.getEndpointSelectedAtt(), ctx);
    }

    public DeviceAPIEventEnums getSelectDefEvent(String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {
        DbActivityDefEvent eventwf = activityEventMgr.loadByKey(activityDefId);
        return eventwf.getDeviceEvent();
    }

    @Override
    public ActivityDefEvent getActivityDefEventAttribute(String activityDefId) throws BPMException {

        DbActivityDefEvent eventwf = activityEventMgr.loadByKey(activityDefId);

        return new ActivityDefEventProxy(eventwf, this.systemCode);

    }

    @Override
    public Object getActivityInstEventAttribute(String activityInstId, ActivityInstRightAtt attName, Map<RightCtx, Object> ctx) throws BPMException {
        switch (attName) {
            case SPONSOR:
                String sql = " where ACTIVITYINST_ID = '" + activityInstId + "' " + " and DEVICE_GRP_CODE in ('" + RightGroupEnums.SPONSOR + "','" + RightGroupEnums.SPONSOR + "|" + RightGroupEnums.HISSPONSOR + "')";
                return getEndPointsFromActivityEvent(sql);

            case PERFORMER:
                sql = " where ACTIVITYINST_ID = '" + activityInstId + "' " + " and DEVICE_GRP_CODE in ('" + RightGroupEnums.SPONSOR + "','" + RightGroupEnums.SPONSOR + "|" + RightGroupEnums.HISSPONSOR + "')";
                return getEndPointsFromActivityEvent(sql);

            case READER:
                sql = " where ACTIVITYINST_ID = '" + activityInstId + "' " + " and DEVICE_GRP_CODE in ('" + RightGroupEnums.READER + "','" + RightGroupEnums.READER + "|" + RightGroupEnums.NORIGHT + "')";
                return getEndPointsFromActivityEvent(sql);
            case ALL:
                sql = " where ACTIVITYINST_ID = '" + activityInstId + "' ";
                return getEndPointsFromActivityEvent(sql);
            case COMMAND:
                sql = "select COMMAND_ID FROM  RT_ACTIVITY_DEVICE where RT_ACTIVITY_DEVICE.ACTIVITYINST_ID = '" + activityInstId + "' ";
                return getMsgFromActivityEvent(sql);

        }

        return null;

    }

    private List<DeviceEndPoint> getEndPointsFromActivityEvent(String sql) throws BPMException {
        DbActivityInstEvent[] events = null;
        try {
            events = actEventMgr.loadByWhere(sql);
        } catch (SQLException e) {
            throw new BPMException("load persons from activity failed!", e);
        }
        List<DeviceEndPoint> endpointList = new ArrayList<DeviceEndPoint>();
        for (int i = 0; i < events.length; i++) {
            String epId = events[i].getEndPointId();
            DeviceEndPoint endPoint;
            try {
                endPoint = this.getIotClient().getEndPointById(epId);
                endpointList.add(endPoint);
            } catch (HomeException e) {
                log.warn("can't load endPoint : " + epId, e);
            } catch (JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return endpointList;
    }

    private List<Msg> getMsgFromActivityEvent(String sql) throws BPMException {

        return null;
    }

    @Override
    public ReturnType createProcessInst(String processInstId, Map rightCtx) throws BPMException {
        // TODO Auto-generated method stub
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType createProcessInst(String processInstId, String initType, Map rightCtx) {
        // TODO Auto-generated method stub
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /**
     * 支持新的公式摸板，可以有多个公式组合在一起，每个公式可以设置参数
     *
     * @param participantAttribute 公式定义的扩展属性
     * @param ctx
     * @return
     * @throws BPMException
     */
    public List getParticipant(EIAttributeDef participantAttribute, Map<RightCtx, Object> ctx) throws BPMException {
        if (participantAttribute == null) {
            return new ArrayList();
        }

        String selectedId = participantAttribute.getValue();
        // 此属性不为空，说明是旧版本的公式系统，继续调用旧的公式系统执行
        if (selectedId != null && !selectedId.equals("")) {
            return getParticipant(selectedId, ctx);
        }
        List<EIAttribute> child = participantAttribute.getChildren();
        if (child.size() == 0) {
            return new ArrayList();
        }

        List result = new ArrayList();
        for (int i = 0; i < child.size(); i++) {
            EIAttributeDef formulaAtt = (EIAttributeDef) child.get(i);
            String formulaId = formulaAtt.getValue();
            Object o = null;
            if (formulaId.equalsIgnoreCase("CUSTOMFORMULA")) {
                // 高级公式
                EIAttributeDef customFormulaAtt = (EIAttributeDef) formulaAtt.getChild("CUSTOMFORMULA");
                String expression = customFormulaAtt.getValue();
                if (expression != null && !expression.equals("")) {
                    o = executeExpression(expression, ctx);
                }
            } else {
                // 执行新的公式摸板
                o = executeExpression(formulaAtt, ctx);
            }

            if (o == null) {
                continue;
            }

            if ((o instanceof DeviceEndPoint) || o instanceof Device || o instanceof Command) {
                result.add(o);
            } else if (o instanceof DeviceEndPoint[]) {
                DeviceEndPoint[] endpoints = (DeviceEndPoint[]) o;
                for (int j = 0; j < endpoints.length; j++) {
                    result.add(endpoints[j]);
                }
            } else if (o instanceof Device[]) {
                Device[] events = (Device[]) o;
                for (int j = 0; j < events.length; j++) {
                    result.add(events[j]);
                }
            } else if (o instanceof Command[]) {
                Command[] commands = (Command[]) o;
                for (int j = 0; j < commands.length; j++) {
                    result.add(commands[j]);
                }
            }

        }
        result = combineParticipant(result);
        return result;
    }

    /**
     * @param src
     * @return
     */
    private List combineParticipant(List src) {
        List result = new ArrayList();
        for (int i = 0; i < src.size(); i++) {
            if (!result.contains(src.get(i))) {
                result.add(src.get(i));
            }
        }
        return result;
    }

    private List getParticipant(String participantSelectedId, Map<RightCtx, Object> ctx) throws BPMException {
        String selectedId = participantSelectedId;
        DbParticipantSelect selected;
        String expression = null;
        if (selectedId == null) {
            return new ArrayList();
        }
        if (!isUUID(selectedId)) {
            expression = selectedId;
        } else {
            try {
                selected = participantMgr.loadByKey(selectedId);
            } catch (SQLException e) {
                throw new BPMException("load participant " + selectedId + " failed", e);
            }
            if (selected == null) {
                return new ArrayList();
            }
            expression = selected.getFormula();
        }
        if (expression == null) {
            return new ArrayList();
        }
        List list;
        Object o = executeExpression(expression, ctx);
        if (o == null) {
            return new ArrayList();
        }
        list = new ArrayList();
        if ((o instanceof DeviceEndPoint) || o instanceof Device) {
            list.add(o);
        } else if (o instanceof DeviceEndPoint[]) {
            DeviceEndPoint[] endpoints = (DeviceEndPoint[]) o;
            for (int i = 0; i < endpoints.length; i++) {
                list.add(endpoints[i]);
            }
        } else if (o instanceof Device[]) {
            Device[] events = (Device[]) o;
            for (int i = 0; i < events.length; i++) {
                list.add(events[i]);
            }
        }
        return list;
    }

    private boolean isUUID(String uuid) {
        if (uuid.length() == 36) {
            if (uuid.charAt(8) == '-' && uuid.charAt(13) == '-') {
                return true;
            }
        }
        return false;
    }

    private synchronized Object executeExpression(EIAttributeDef formulaAtt, Map<RightCtx, Object> ctx) throws BPMException {
        ExpressionParser parser = JDSExpressionParserManager.getExpressionParser(ctx);
        addCurrentActivityInst(parser, ctx);
        addCurrentProcessInst(parser, ctx);
        addCurrentEndPointIeee(parser, ctx);
        String selectedId = formulaAtt.getValue();
        // 取得公式
        DbParticipantSelect selected;
        try {
            selected = participantMgr.loadByKey(selectedId);
        } catch (SQLException e) {
            throw new BPMException("load participant " + selectedId + " failed", e);
        }
        if (selected == null) {
            return new ArrayList();
        }
        String expression = selected.getFormula();
        // 取得参数以及参数的值
        EIAttributeDef parameterAtt = (EIAttributeDef) formulaAtt.getChild(formulaAtt.getValue());
        if (parameterAtt != null && parameterAtt.getName() != null && !parameterAtt.getName().equals("") && parameterAtt.getValue() != null && !parameterAtt.getValue().equals("")) {
            StringTokenizer stParameter = new StringTokenizer(parameterAtt.getValue(), ";");
            while (stParameter.hasMoreTokens()) {
                String parameterString = stParameter.nextToken();
                int _index = parameterString.indexOf("=");
                if (_index == -1) {
                    continue;
                }
                String parameterName = parameterString.substring(0, _index);
                String parameterValue = parameterString.substring(_index + 1, parameterString.length());
                if (ctx.containsKey(parameterName)) {
                    parser.addVariableAsObject(parameterName, ctx.get(parameterName));
                } else {
                    parser.addVariableAsObject(parameterName, parameterValue);
                }
            }
        }
        // 执行公式
        boolean result = parser.parseExpression(expression);
        if (result == false) {
            log.warn("expression parse error: " + parser.getErrorInfo());
            return null;
        } else {
            Object o = parser.getValueAsObject();
            if (parser.hasError() == true) {
                log.error(parser.getErrorInfo());
            }
            return o;
        }
    }

    public List getParameter(String parameterName, String activityDefId) throws BPMException {

        DbActivityDefEvent eventDef = activityEventMgr.loadByKey(activityDefId);
        // 支持新的公式摸板
        EIAttributeDef attDef = eventDef.getEndpointSelectedAtt();
        List child = attDef.getChildren();
        List<String> valueList = new ArrayList<String>();
        for (int i = 0; i < child.size(); i++) {
            EIAttributeDef formulaAtt = (EIAttributeDef) child.get(i);
            String formulaId = formulaAtt.getValue();
            EIAttributeDef parameterAtt = (EIAttributeDef) formulaAtt.getChild(formulaAtt.getValue());
            if (parameterAtt != null && parameterAtt.getName() != null && !parameterAtt.getName().equals("") && parameterAtt.getValue() != null && !parameterAtt.getValue().equals("")) {
                StringTokenizer stParameter = new StringTokenizer(parameterAtt.getValue(), ";");
                while (stParameter.hasMoreTokens()) {
                    String parameterString = stParameter.nextToken();
                    int _index = parameterString.indexOf("=");
                    if (_index == -1) {
                        continue;
                    }
                    String name = parameterString.substring(0, _index);
                    String parameterValue = parameterString.substring(_index + 1, parameterString.length());
                    if (parameterName.equals(name)) {
                        StringTokenizer st = new StringTokenizer(parameterValue, ":");

                        while (st.hasMoreTokens()) {
                            valueList.add(st.nextToken());
                        }
                    }

                }
            }
        }

        return valueList;
    }

    private Object executeExpression(String expression, Map<RightCtx, Object> ctx) {
        ExpressionParser parser = JDSExpressionParserManager.getExpressionParser(ctx);
        addCurrentActivityInst(parser, ctx);
        addCurrentProcessInst(parser, ctx);
        addCurrentEndPointIeee(parser, ctx);
        boolean result = parser.parseExpression(expression);
        if (result == false) {
            log.warn("expression parse error: " + parser.getErrorInfo());
            return null;
        } else {
            Object o = parser.getValueAsObject();
            if (parser.hasError() == true) {
                log.error(parser.getErrorInfo());
            }
            return o;
        }
    }

    private void addCurrentEndPointIeee(ExpressionParser parser, Map<RightCtx, Object> ctx) {

        Iterator keyIt = ctx.keySet().iterator();
        while (keyIt.hasNext()) {
            String key = keyIt.next().toString();
            parser.addVariableAsObject(key, ctx.get(key));
        }

    }

    /**
     * 向公式解析器加入当前活动实例
     *
     * @param parser
     * @param ctx
     */
    private void addCurrentActivityInst(ExpressionParser parser, Map<RightCtx, Object> ctx) {
        String activityInstId = (String) ctx.get(RightCtx.ACTIVITYINST_ID);
        if (activityInstId == null) {
            parser.addVariableAsObject(RightCtx.CURRENT_ACTIVITYINST.getType(), null);
            return;
        }

        try {
            EIActivityInst activityInst = (EIActivityInst) activityInstMgr.loadByKey(activityInstId);
            parser.addVariableAsObject(RightCtx.CURRENT_ACTIVITYINST.getType(), activityInst);
        } catch (BPMException e) {
            log.warn("load activiytInst failed!", e);
        }
    }

    public boolean canStartProcess(String versionId, Map<RightCtx, Object> ctx) throws BPMException {
        String epIeee = (String) ctx.get(RightCtx.sensorieee);
        String eventtype = (String) ctx.get(RightCtx.sensorieee);

        EIActivityDef firstAct = this.workflowEngine.getFirstActivityDefInProcess(versionId);

        DeviceAPIEventEnums eventdef = this.getSelectDefEvent(firstAct.getActivityDefId(), ctx);
        // firstAct.getAttributeValue("DeviceEvent.DeviceEvent");
        if (eventtype != null && eventtype.equals(eventdef.getMethod())) {

            List<DeviceEndPoint> endpoints = this.getSelectDefEndPoint(firstAct.getActivityDefId(), ctx);

            if (endpoints == null || endpoints.size() == 0) {
                return false;
            }
            for (Iterator it = endpoints.iterator(); it.hasNext(); ) {
                DeviceEndPoint ep = (DeviceEndPoint) it.next();
                if (ep.getIeeeaddress().equalsIgnoreCase(epIeee)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 向公式解析器加入当前流程实例
     *
     * @param parser
     * @param ctx
     */
    private void addCurrentProcessInst(ExpressionParser parser, Map<RightCtx, Object> ctx) {
        String processInstId = (String) ctx.get(RightCtx.PROCESSINST_ID);
        if (processInstId == null) {
            parser.addVariableAsObject(RightCtx.CURRENT_PROCESSINST.getType(), null);
            return;
        }

        try {
            EIProcessInst processInst = (EIProcessInst) processInstMgr.loadByKey(processInstId);
            parser.addVariableAsObject(RightCtx.CURRENT_PROCESSINST.getType(), processInst);
        } catch (BPMException e) {
            log.warn("load processInst failed!", e);
        }
    }

    @Override
    public boolean canTakeBack(String activityInstID, Map rightCtx) {

        return true;
    }

    @Override
    public boolean canRouteBack(String activityInstID, Map rightCtx) {
        return true;
    }

    @Override
    public ReturnType routeBack(String fromActivityInstID, String toActivityInstHistoryID, Map rightCtx) {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType startProcessInst(String processInstId, String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public List getPerformerCandidate(String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReturnType startActivityInst(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType hasEventToStartProcess(String processInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType saveActivityHistoryInst(String activityInstId, String activityInstHistoryId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType splitActivityInst(String activityInstId, String[] subActivityInstIds, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType changePerformer(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType combineActivityInsts(String[] activityInstIds, Map<RightCtx, Object> ctx) throws BPMException {
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
    public Object getRouteDefEventAttribute(String routeDefId, AttributeName attName, Map<RightCtx, Object> ctx) throws BPMException {
        return null;
    }

    @Override
    public Object getActivityInstHistoryEventAttribute(String activityInstHistoryId, AttributeName attName, Map<RightCtx, Object> ctx) throws BPMException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReturnType endRead(String activityInstID, String activityHistoryId, Map<RightCtx, Object> ctxRight) {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public boolean queryPermissionToActivityInst(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public List queryAllPermissionToActivityInst(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        // TODO Auto-generated method stub
        return null;
    }

    public CtVfsService getVfsClient() {

        CtVfsService vfsClient = CtVfsFactory.getCtVfsService();
        return vfsClient;
    }

    public CtIotService getIotClient() {

        CtIotService iotClient = CtIotFactory.getCtIotService();
        return iotClient;
    }
}
