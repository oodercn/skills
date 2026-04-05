
package net.ooder.bpm.engine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ooder.annotation.AttributeName;
import net.ooder.bpm.engine.database.expression.DbParticipantSelect;
import net.ooder.bpm.engine.database.expression.DbParticipantSelectManager;
import net.ooder.bpm.engine.database.right.DbProcessDefVersionRightManager;
import net.ooder.bpm.engine.database.service.*;
import net.ooder.bpm.engine.inter.*;
import net.ooder.bpm.enums.activitydef.service.ActivityDefServiceAtt;
import net.ooder.bpm.enums.activityinst.ActivityInstRightAtt;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.bpm.enums.right.RightGroupEnums;
import net.ooder.bpm.enums.right.RightPerformStatus;
import net.ooder.common.ReturnType;
import net.ooder.common.expression.ExpressionParser;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.context.JDSActionContext;
import net.ooder.esb.config.manager.JDSExpressionParserManager;
import net.ooder.jds.core.esb.util.ActionContext;
import net.ooder.msg.Msg;
import net.ooder.org.OrgManager;
import net.ooder.server.JDSServer;
import net.ooder.server.OrgManagerFactory;
import net.ooder.web.RemoteConnectionManager;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Future;

public class IOTServiceEngine implements ServiceEngine, Serializable {
    Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, IOTServiceEngine.class);

    // process relservice manager
    public EIProcessDefManager processDefMgr = null;

    public EIProcessDefVersionManager processDefVerMgr = null;

    public EIActivityDefManager activityDefMgr = null;

    public EIRouteDefManager routeDefMgr = null;

    public EIProcessInstManager processInstMgr = null;

    public EIActivityInstManager activityInstMgr = null;

    public DbActivityInstServiceManager actServiceMgr = null;

    public DbActivityHistoryServiceManager historyServiceMgr = null;

    public EIActivityInstHistoryManager activityInstHistoryMgr = null;

    public EIRouteInstManager routeInstMgr = null;

    public DbProcessDefVersionRightManager processRightMgr = null;

    public DbActivityDefServiceManager activityServiceMgr = null;

    public DbParticipantSelectManager participantMgr = null;

    // Workflw Engine
    public WorkflowEngine workflowEngine = null;

    // Org Manager
    public OrgManager orgManager = null;


    private String systemCode;

    public IOTServiceEngine(String systemCode) {
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
        activityServiceMgr = DbActivityDefServiceManager.getInstance();
        participantMgr = DbParticipantSelectManager.getInstance();
        actServiceMgr = DbActivityInstServiceManager.getInstance();
        workflowEngine = WorkflowEngineImpl.getEngine(systemCode);
        orgManager = OrgManagerFactory.getOrgManager(JDSServer.getClusterClient().getSystem(systemCode).getConfigname());

    }

    private static IOTServiceEngine engine = null;

    public static IOTServiceEngine getEngine(String systemCode) {
        if (engine == null) {
            synchronized (IOTServiceEngine.class) {
                if (engine == null) {
                    engine = new IOTServiceEngine(systemCode);
                }
            }
        }
        return engine;
    }

    @Override
    public ReturnType routeTo(final String activityInstId, String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {
        List<String> performers = new ArrayList<String>();
        DbActivityDefService activityDefService = activityServiceMgr.loadByKey(activityDefId);

        this.getActivityDefServiceAttribute(activityDefId, ActivityDefServiceAtt.SERVICESELECTEDID, ctx);

        final String params = (String) this.getActivityDefServiceAttribute(activityDefId, ActivityDefServiceAtt.HTTP_SERVICEPARAMS, ctx);
        String requestType = (String) this.getActivityDefServiceAttribute(activityDefId, ActivityDefServiceAtt.HTTP_REQUESTTYPE, ctx);
        final String method = (String) this.getActivityDefServiceAttribute(activityDefId, ActivityDefServiceAtt.HTTP_REQUESTTYPE, ctx);
        String responseType = (String) this.getActivityDefServiceAttribute(activityDefId, ActivityDefServiceAtt.HTTP_RESPONSETYPE, ctx);
        String url = (String) this.getActivityDefServiceAttribute(activityDefId, ActivityDefServiceAtt.HTTP_URL, ctx);

        ctx.put(RightCtx.PERFORMSEQUENCE, params);
        ctx.put(RightCtx.Http_ResponseType, requestType);
        ctx.put(RightCtx.Http_Method, method);
        ctx.put(RightCtx.Http_RequestType, responseType);
        ctx.put(RightCtx.SERVICE_URL, url);

        EIActivityInst eiActivityInst = activityInstMgr.loadByKey(activityInstId);

        final String processInstId = eiActivityInst.getProcessInstId();

        try {

            List<Msg> msgs = (List<Msg>) this.getActivityDefServiceAttribute(activityDefId, ActivityDefServiceAtt.SERVICESELECTEDID, ctx);
            if (msgs != null) {
                for (Msg msg : msgs) {
                    DbActivityInstService performer = actServiceMgr.createActivityInstService();
                    performer.setActivityInstServiceId(UUID.randomUUID().toString());
                    performer.setProcessInstId(processInstId);
                    performer.setActivityInstId(activityInstId);
                    performer.setServiceActivityState(RightPerformStatus.FINISH);
                    performer.setUrl(method);
                    performer.setRequestParams(params);
                    performer.setServiceGrpCode(RightGroupEnums.PERFORMER);
                    performer.setServiceId(msg.getId());
                    actServiceMgr.save(performer);

                }
            } else {

                Request request = null;
                switch (activityDefService.getMethod()) {
                    case POST:
                        request = Request.Post(url);
                    case GET:
                        request = Request.Get(url);
                }

                switch (activityDefService.getRequestType()) {
                    case JSON:
                        request.bodyString(params, ContentType.APPLICATION_JSON);
                        try {
                            request.execute().returnContent().asString();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    case FORM:
                        JSONObject jsonParams = JSON.parseObject(params);
                        Form form = Form.form();

                        for (String paramName : jsonParams.keySet()) {
                            form.add(paramName, jsonParams.getString(paramName));
                        }

                        request = Request.Post(url).bodyForm(form.build(), Charset.forName("utf-8"));
                }


                Async async = Async.newInstance().use(RemoteConnectionManager.getConntctionService("IOTService"));
                Future<Content> future = async.execute(request, new FutureCallback<Content>() {
                    public void failed(final Exception ex) {
                        ex.printStackTrace();
                    }

                    public void completed(final Content content) {


                        String json = JSONObject.parseObject(content.asString()).toJSONString();
                        log.info("return " + json);
                        DbActivityInstService performer = actServiceMgr.createActivityInstService();
                        performer.setActivityInstServiceId(UUID.randomUUID().toString());
                        performer.setProcessInstId(processInstId);
                        performer.setActivityInstId(activityInstId);
                        performer.setServiceActivityState(RightPerformStatus.FINISH);
                        performer.setUrl(method);
                        performer.setRequestParams(params);
                        performer.setServiceGrpCode(RightGroupEnums.PERFORMER);
                        performer.setServiceId("");
                        try {
                            actServiceMgr.save(performer);
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }

                    public void cancelled() {
                    }
                });

                future.get();


            }

        } catch (Exception e) {

            DbActivityInstService performer = actServiceMgr.createActivityInstService();
            performer.setActivityInstServiceId(UUID.randomUUID().toString());
            performer.setProcessInstId(processInstId);
            performer.setActivityInstId(activityInstId);
            performer.setServiceActivityState(RightPerformStatus.FINISH);
            performer.setUrl(method);
            performer.setRequestParams(params);
            performer.setServiceGrpCode(RightGroupEnums.PERFORMER);
            performer.setServiceId("");
            try {
                actServiceMgr.save(performer);
            } catch (SQLException ee) {
                // TODO Auto-generated catch block
                ee.printStackTrace();
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

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    @Override
    public Object getActivityDefServiceAttribute(String activityDefId, ActivityDefServiceAtt attName, Map<RightCtx, Object> ctx) throws BPMException {

        DbActivityDefService servicewf = activityServiceMgr.loadByKey(activityDefId);
        if (servicewf == null) {
            return null;
        }

        switch (attName) {
            case HTTP_URL:
                return servicewf.getUrl();
            case HTTP_REQUESTTYPE:
                return servicewf.getRequestType().getType();
            case HTTP_METHOD:
                return getParticipant(servicewf.getServiceSelectedAtt(), ctx);
            case HTTP_RESPONSETYPE:
                return servicewf.getResponseType().getType();
            case HTTP_SERVICEPARAMS:
                String responseStr = servicewf.getServiceParams();


                Map<String, Object> context = new HashMap<String, Object>();
                Iterator<RightCtx> keyIt = ctx.keySet().iterator();
                while (keyIt.hasNext()) {
                    RightCtx objk = keyIt.next();

                    context.put(objk.getType(), ctx.get(objk).toString());
                }

                ActionContext.getContext().getValueStack().getRoot().push(context);
                JDSActionContext.getActionContext().getContext().putAll(context);

//		FreemarkerResult result = new FreemarkerResult();
//
//		File file = new File(JDSConfig.Config.tempPath(), activityDefId + "urlParams.ftl");
//		if (!file.exists()) {
//		    file.createNewFile();
//		    FileWriter fwriter = new FileWriter(file);
//		    fwriter.write(servicewf.getServiceParams());
//		    fwriter.close();
//
//		}
//
//		StringWriter stringWriter = (StringWriter) result.doExecute(file.getName());
//		file.deleteOnExit();
//		responseStr = stringWriter.toString();
//		try {
//	    } catch (TemplateException e) {
//		e.printStackTrace();
//	    } catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	    } finally {
//		ActionContext.getContext().getValueStack().getRoot().remove(ctx);
//	    }
                ActionContext.getContext().getValueStack().getRoot().remove(ctx);
                return responseStr;

            case SERVICESELECTEDID:
                return servicewf.getServiceSelectedID();

            default:
                break;
        }

        return null;

    }

    @Override
    public Object getActivityInstServiceAttribute(String activityInstId, ActivityInstRightAtt attName, Map<RightCtx, Object> ctx) throws BPMException {
//	switch (attName) {
//
//	case RIGHTGRP_COMMAND:
//	    String sql = "select SERVICE_ID FROM  RT_ACTIVITY_SERVICE where RT_ACTIVITY_SERVICE.ACTIVITYINST_ID = '" + activityInstId + "' ";
//	    return getMsgFromActivityService(sql);
//
//	}

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
    private List getParticipant(EIAttributeDef participantAttribute, Map<RightCtx, Object> ctx) throws BPMException {
        if (participantAttribute == null) {
            return new ArrayList();
        }

        String selectedId = participantAttribute.getValue();
        // 此属性不为空，说明是旧版本的公式系统，继续调用旧的公式系统执行
        if (selectedId != null && !selectedId.equals("")) {
            return getParticipant(selectedId, ctx);
        }
        List child = participantAttribute.getChildren();
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

            if (o instanceof Msg) {
                result.add(o);
            } else if (o instanceof Msg[]) {
                Msg[] commands = (Msg[]) o;
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
        List result;
        Object o = executeExpression(expression, ctx);
        if (o == null) {
            return new ArrayList();
        }
        result = new ArrayList();

        if (o instanceof Msg) {
            result.add(o);
        } else if (o instanceof Msg[]) {
            Msg[] commands = (Msg[]) o;
            for (int j = 0; j < commands.length; j++) {
                result.add(commands[j]);
            }
        }

        return result;
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

        DbActivityDefService serviceDef = activityServiceMgr.loadByKey(activityDefId);
        // 支持新的公式摸板
        EIAttributeDef attDef = serviceDef.getServiceSelectedAtt();
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
    public ReturnType hasServiceToStartProcess(String processInstId, Map<RightCtx, Object> ctx) throws BPMException {
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
    public Object getProcessDefVersionServiceAttribute(String processDefVersionId, AttributeName attName, Map<RightCtx, Object> ctx) throws BPMException {
        return null;
    }

    @Override
    public Object getRouteDefServiceAttribute(String routeDefId, AttributeName attName, Map<RightCtx, Object> ctx) throws BPMException {
        return null;
    }

    @Override
    public Object getActivityInstHistoryServiceAttribute(String activityInstHistoryId, AttributeName attName, Map<RightCtx, Object> ctx) throws BPMException {
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
}
