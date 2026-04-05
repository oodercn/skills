
package net.ooder.bpm.engine;

import net.ooder.common.logging.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ooder.bpm.client.ActivityDefDevice;
import net.ooder.bpm.engine.database.device.*;
import net.ooder.bpm.engine.database.expression.DbParticipantSelect;
import net.ooder.bpm.engine.database.expression.DbParticipantSelectManager;
import net.ooder.bpm.engine.database.right.DbProcessDefVersionRightManager;
import net.ooder.bpm.engine.inter.*;
import net.ooder.bpm.engine.proxy.ActivityDefDeviceProxy;
import net.ooder.bpm.enums.activitydef.deivce.ActivityDefDevicePerformSequence;
import net.ooder.bpm.enums.activitydef.deivce.ActivityDefDevicePerformtype;
import net.ooder.bpm.enums.activityinst.ActivityInstRightAtt;
import net.ooder.bpm.enums.command.CommandExecType;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.bpm.enums.right.RightGroupEnums;
import net.ooder.bpm.enums.right.RightPerformStatus;
import net.ooder.command.Command;
import net.ooder.agent.client.command.SensorCommand;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.common.expression.ExpressionParser;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.annotation.AttributeName;
import net.ooder.agent.client.enums.CommandEnums;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class IOTDeviceEngine implements DeviceEngine, Serializable {
    Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, IOTDeviceEngine.class);

    static ScheduledExecutorService commandService = Executors.newScheduledThreadPool(1000);

    // process relevent manager
    public EIProcessDefManager processDefMgr = null;

    public EIProcessDefVersionManager processDefVerMgr = null;

    public EIActivityDefManager activityDefMgr = null;

    public EIRouteDefManager routeDefMgr = null;

    public EIProcessInstManager processInstMgr = null;

    public EIActivityInstManager activityInstMgr = null;

    public DbActivityInstDeviceManager actDeviceMgr = null;

    public DbActivityHistoryDeviceManager historyDeviceMgr = null;

    public EIActivityInstHistoryManager activityInstHistoryMgr = null;

    public EIRouteInstManager routeInstMgr = null;

    public DbProcessDefVersionRightManager processRightMgr = null;

    public DbActivityDefDeviceManager activityDeviceMgr = null;

    public DbParticipantSelectManager participantMgr = null;

    // Workflw Engine
    public WorkflowEngine workflowEngine = null;

    // Org Manager
    public OrgManager orgManager = null;

    private String systemCode;

    public IOTDeviceEngine(String systemCode) {
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
	activityDeviceMgr = DbActivityDefDeviceManager.getInstance();
	participantMgr = DbParticipantSelectManager.getInstance();
	actDeviceMgr = DbActivityInstDeviceManager.getInstance();
	workflowEngine = WorkflowEngineImpl.getEngine(systemCode);
	orgManager = OrgManagerFactory.getOrgManager(JDSServer.getClusterClient().getSystem(systemCode).getConfigname());

    }

    private static IOTDeviceEngine engine = null;

    public static IOTDeviceEngine getEngine(String systemCode) {
	if (engine == null) {
	    synchronized (IOTDeviceEngine.class) {
		if (engine == null) {
		    engine = new IOTDeviceEngine(systemCode);
		}
	    }
	}
	return engine;
    }

    class ExcelCommand implements Runnable {

	private List<Command> commands;
	private String endPointId;
	private String userId;
	private CountDownLatch mainlatch;
	private String activityInstId;
	private String nextActivityDefId;

	ExcelCommand(List<Command> commands, String activityInstId, String nextActivityDefId, String endPointId, String userId, CountDownLatch mainlatch) {
	    this.commands = commands;
	    this.endPointId = endPointId;
	    this.userId = userId;
	    this.nextActivityDefId = nextActivityDefId;
	    this.activityInstId = activityInstId;
	    this.mainlatch = mainlatch;

	}

	@Override
	public void run() {
	    final CountDownLatch latch = new CountDownLatch(commands.size());
	    final List<Msg> msgs = new ArrayList<Msg>();
	    try {

		final CommandExecType commandExecType = (CommandExecType) IOTDeviceEngine.this.getActivityDefDeviceAttribute(nextActivityDefId).getCommandExecType();
		workflowEngine.routeTo(activityInstId, nextActivityDefId);
		for (final Command command : commands) {
		    EIActivityInst actInst = activityInstMgr.loadByKey(activityInstId);
		
		    final DbActivityInstDevice performer = actDeviceMgr.createActivityInstDevice();
		    performer.setActivityInstEndPointId(UUID.randomUUID().toString());
		    performer.setProcessInstId(actInst.getProcessInstId());
		    performer.setActivityInstId(actInst.getActivityInstId());
		    performer.setDeviceActivityState(RightPerformStatus.WAITING);	
		    performer.setEndPointId(endPointId);
		    final Integer delayTime = (Integer) IOTDeviceEngine.this.getActivityDefDeviceAttribute(nextActivityDefId).getCommandDelayTime();


			try {
				Command msgcommand = IOTDeviceEngine.this.getIotClient().sendCommand( command, delayTime).get();
				if (commandExecType.equals(CommandExecType.MULTIPLENOWITE)) {
					commandService.execute(new Runnable() {
						@Override
						public void run() {
							try {

								command.setCommandId(msgcommand.getCommandId());
								performer.setCommandId(command.getCommandId());
								performer.setDeviceGrpCode(RightGroupEnums.PERFORMER);
								actDeviceMgr.save(performer);
								latch.countDown();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}

					});

				} else {
					try {
						command.setCommandId(msgcommand.getCommandId());
						performer.setCommandId(command.getCommandId());
						performer.setDeviceGrpCode(RightGroupEnums.PERFORMER);
						actDeviceMgr.save(performer);
						latch.countDown();
					} catch (SQLException  e) {
						e.printStackTrace();
					}
				}
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

			log.info("start commandTask delayTime=" + delayTime + " commandExecType=" + commandExecType.getName());



		}

		// 如果不等待，则直接并行处理
		if (!commandExecType.equals(CommandExecType.MULTIPLENOWITE)) {
		    latch.await();
		}

	    } catch ( InterruptedException | BPMException e2) {
		e2.printStackTrace();
	    }

	    mainlatch.countDown();
	}

    }

    @Override
    public ReturnType routeTo(String activityInstId, String activityDefId, final Map<RightCtx, Object> ctx) throws BPMException {

	List<String> performers = new ArrayList<String>();

	if (ctx == null || ctx.get(RightCtx.sensorieee) == null) {
	    List<DeviceEndPoint> endpints = (List<DeviceEndPoint>) this.getActivityDefDeviceAttribute(activityDefId).getEndpoints();
	    for (DeviceEndPoint ep : endpints) {
		performers.add(ep.getEndPointId());
	    }
	} else {
	    performers = (List<String>) ctx.get(RightCtx.sensorieee);
	}

	final CountDownLatch maindlatch = new CountDownLatch(performers.size());

	final List<Command> commands = (List<Command>) this.getActivityDefDeviceAttribute(activityDefId).getCommand();

	String historyId = workflowEngine.getActivityInstHistoryListByActvityInst(activityInstId).get(0).getActivityHistoryId();
	List<EIActivityInst> copyActivityInsts = workflowEngine.splitActivityInst(activityInstId, performers.size(), historyId);

	ActivityDefDevicePerformtype performtype = (ActivityDefDevicePerformtype) this.getActivityDefDeviceAttribute(activityDefId).getPerformType();

	log.info("start perform performtype=" + performtype.getName() + " maindlatch.size=" + maindlatch.getCount());
	int k = 0;

	for (final String endPointId : performers) {

	    activityInstId = copyActivityInsts.get(k).getActivityInstId();

	    DeviceEndPoint endPoint = null;
	    try {
		endPoint = getIotClient().getEndPointById(endPointId);
		ctx.put(RightCtx.sensorieee, endPoint.getIeeeaddress());

		for (final Command command : commands) {
		    command.setGatewayieee(endPoint.getDevice().getRootDevice().getSerialno());
		    if (!endPoint.getDevice().getSensortype().getType().equals(0)) {
			SensorCommand sensorCommand = (SensorCommand) command;
			sensorCommand.setSensorieee(endPoint.getIeeeaddress());
		    }
		}
		ExcelCommand commandTask = new ExcelCommand(commands, activityInstId, activityDefId, endPointId, ctx.get(RightCtx.USERID).toString(), maindlatch);

		// 并行处理
		if (performtype.equals(ActivityDefDevicePerformtype.MULTIPLE) || performtype.equals(ActivityDefDevicePerformtype.NOSELECT)) {

		    commandService.submit(commandTask);

		} else if (performtype.equals(ActivityDefDevicePerformtype.SINGLE)) {

		    commandService.submit(commandTask).get();
		    // latch.countDown();
		}
	    } catch (Exception e) {
		e.printStackTrace();
		maindlatch.countDown();
	    }
	    k = k + 1;
	}

	try {
	    if (!performtype.equals(ActivityDefDevicePerformtype.MULTIPLE)) {
		maindlatch.await();
	    }

	} catch (InterruptedException e) {
	    e.printStackTrace();
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

	DbActivityDefDevice actDefDevice = activityDeviceMgr.loadByKey(activityInst.getActivityDefId());
	ActivityDefDevicePerformSequence performSequence = actDefDevice.getPerformSequence();

	String sql = " where ACTIVITYINST_ID = '" + activityInstID + "' and DEVICE_GRP_CODE = '" + RightGroupEnums.PERFORMER + "' and ENDPOINT_ID = '" + userId + "' and DEVICE_ACTIVITY_STATE = '" + RightPerformStatus.WAITING + "'";
	DbActivityInstDevice[] enpoints;
	try {
	    enpoints = actDeviceMgr.loadByWhere(sql);
	    if (enpoints.length == 0) {
		return new ReturnType(ReturnType.MAINCODE_FAIL);
	    } else {
		enpoints[0].setDeviceActivityState(RightPerformStatus.CURRENT);
		actDeviceMgr.save(enpoints[0]);

		EIActivityInst actInst = activityInstMgr.loadByKey(activityInstID);
		List routeInsts = routeInstMgr.getRouteInsts(actInst);

		for (Iterator it = routeInsts.iterator(); it.hasNext();) {
		    EIRouteInst routeInst = (EIRouteInst) it.next();
		    DbActivityHistoryDevice historyDevice = historyDeviceMgr.createActivityHistoryDevice(enpoints[0]);
		    historyDevice.setActivityInstHistoryId(routeInst.getFromActivityId());
		    historyDevice.setDeviceGrpCode(RightGroupEnums.READER);
		    historyDeviceMgr.save(historyDevice);
		}
	    }
	} catch (SQLException e) {
	    throw new BPMException("load person from activityInst failed!", e);
	}

	if (performSequence.equals(ActivityDefDevicePerformSequence.FIRST)) {
	    String finishOtherSql = " where ACTIVITYINST_ID = '" + activityInstID + "'" + " and DEVICE_GRP_CODE = '" + RightGroupEnums.PERFORMER + "'" + " and DEVICE_ACTIVITY_STATE = '" + RightPerformStatus.WAITING + "'";
	    try {
		enpoints = actDeviceMgr.loadByWhere(finishOtherSql);
		for (int i = 0; i < enpoints.length; i++) {
		    enpoints[i].setDeviceGrpCode(RightGroupEnums.READER);
		}
		actDeviceMgr.save(enpoints);
	    } catch (SQLException e) {
		throw new BPMException("load person from activityInst failed!", e);
	    }
	}

	// 清除活动实例中的所有的LAST_RIGHT_GRP
	String restorePersonSql = " where ACTIVITYINST_ID = '" + activityInstID + "' AND LAST_DEVICE_GRP IS NOT NULL ";
	try {
	    DbActivityInstDevice[] restorEndPoints = actDeviceMgr.loadByWhere(restorePersonSql);
	    for (int i = 0; i < restorEndPoints.length; i++) {
		DbActivityInstDevice person = restorEndPoints[i];
		person.setLastDeviceGrp("");
	    }
	    actDeviceMgr.save(restorEndPoints);
	} catch (SQLException e) {
	    throw new BPMException("restore current performers and readers failed!", e);
	}

	return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    private synchronized Command executeCommand(EIAttributeDef formulaAtt, Map<RightCtx, Object> ctx) throws BPMException {
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

	String expression = selected.getFormula();

	JSONObject jsonobj = JSONObject.parseObject(expression);

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
		    jsonobj.put(parameterName, ctx.get(parameterName));

		} else {
		    jsonobj.put(parameterName, ctx.get(parameterValue));
		}
	    }
	}

	String commandStr = jsonobj.getString("command");
	Command command = null;

	if (CommandEnums.fromByName(commandStr) != null) {
	    command = JSON.toJavaObject(jsonobj, CommandEnums.fromByName(commandStr).getCommand());
	    // command = (Command) jsonobj.getObject("Command", CommandEnums.fromByName(commandStr).getCommand());
	}

	return command;

    }

    private List<Command> getCommand(EIAttributeDef participantAttribute, Map<RightCtx, Object> ctx) throws BPMException {

	if (participantAttribute == null) {
	    return new ArrayList<Command>();
	}

	String selectedId = participantAttribute.getValue();
	// 此属性不为空，说明是旧版本的公式系统，继续调用旧的公式系统执行
	if (selectedId != null && !selectedId.equals("")) {
	    return getParticipant(selectedId, ctx);
	}
	List<EIAttribute> child = participantAttribute.getChildren();
	if (child.size() == 0) {
	    return new ArrayList<Command>();
	}

	List<Command> result = new ArrayList<Command>();
	for (int i = 0; i < child.size(); i++) {
	    EIAttributeDef formulaAtt = (EIAttributeDef) child.get(i);
	    String formulaId = formulaAtt.getValue();
	    Command o = null;
	    if (formulaId.equalsIgnoreCase("CUSTOMFORMULA")) {
		// 高级公式
		EIAttributeDef customFormulaAtt = (EIAttributeDef) formulaAtt.getChild("CUSTOMFORMULA");
		String expression = customFormulaAtt.getValue();

		JSONObject jsonobj = JSONObject.parseObject(expression);

		String commandStr = jsonobj.getString("command");
		if (CommandEnums.fromByName(commandStr) != null) {
		    o = JSON.toJavaObject(jsonobj, CommandEnums.fromByName(commandStr).getCommand());
		}

	    } else {
		// 执行新的公式摸板
		o = executeCommand(formulaAtt, ctx);
	    }

	    if (o == null) {
		continue;
	    }
	    result.add(o);

	}
	result = combineParticipant(result);
	return result;
    }


    public ActivityDefDevice getActivityDefDeviceAttribute(String activityDefId) throws BPMException {

	DbActivityDefDevice devicewf = activityDeviceMgr.loadByKey(activityDefId);

	return new ActivityDefDeviceProxy(devicewf,this.systemCode);

    }


    @Override
		public List<DeviceEndPoint> getActivityInstDevices(String activityInstId, ActivityInstRightAtt attName) throws BPMException {
		switch (attName) {
			case SPONSOR:
				String sql = " where ACTIVITYINST_ID = '" + activityInstId + "' " + " and DEVICE_GRP_CODE in ('" + RightGroupEnums.SPONSOR + "','" + RightGroupEnums.SPONSOR + "|" + RightGroupEnums.HISSPONSOR + "')";
				return getEndPointsFromActivityDevice(sql);

			case PERFORMER:
				sql = " where ACTIVITYINST_ID = '" + activityInstId + "' " + " and DEVICE_GRP_CODE in ('" + RightGroupEnums.SPONSOR + "','" + RightGroupEnums.SPONSOR + "|" + RightGroupEnums.HISSPONSOR + "')";
				return getEndPointsFromActivityDevice(sql);

			case READER:
				sql = " where ACTIVITYINST_ID = '" + activityInstId + "' " + " and DEVICE_GRP_CODE in ('" + RightGroupEnums.READER + "','" + RightGroupEnums.READER + "|" + RightGroupEnums.NORIGHT + "')";
				return getEndPointsFromActivityDevice(sql);
			case ALL:
				sql = " where ACTIVITYINST_ID = '" + activityInstId + "' ";
	    return getEndPointsFromActivityDevice(sql);
	}

	return null;

    }

    private List<DeviceEndPoint> getEndPointsFromActivityDevice(String activityInstId ) throws BPMException {
	DbActivityInstDevice[] devices = null;
		String sql = " where ACTIVITYINST_ID = '" + activityInstId + "' ";
	try {
	    devices = actDeviceMgr.loadByWhere(sql);
	} catch (SQLException e) {
	    throw new BPMException("load persons from activity failed!", e);
	}
	List<DeviceEndPoint> endpointList = new ArrayList<DeviceEndPoint>();
	for (int i = 0; i < devices.length; i++) {
	    String epId = devices[i].getEndPointId();
	    DeviceEndPoint endPoint;
	    try {
		endPoint = CtIotFactory.getCtIotService().getEndPointById(epId);
		// endPoint = appEngine.getEndPoint(epId);
		endpointList.add(endPoint);
	    } catch (JDSException | HomeException e) {
		log.warn("can't load endPoint : " + epId, e);

	    }
	}
	return endpointList;
    }

	@Override
    public List<Command> getCommandFromActivity(String sql) throws BPMException {
		DbActivityInstDevice[] devices = null;
		try {
			devices = actDeviceMgr.loadByWhere(sql);
		} catch (SQLException e) {
			throw new BPMException("load persons from activity failed!", e);
		}
		List<Command> commandList = new ArrayList<Command>();

		for (DbActivityInstDevice deviceInst:devices) {
			String commandId = deviceInst.getCommandId();
			Command command = 	CtIotFactory.getCtIotService().getCommandById(commandId);
			if (command != null) {
				commandList.add(command);
			}
		}

	return commandList;
    }

    @Override
    public ReturnType createProcessInst(String processInstId, Map rightCtx) throws BPMException {
	// TODO Auto-generated method stub
	return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ReturnType createProcessInst(String processInstId, String initType, Map rightCtx) {
	return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /**
     * 支持新的公式摸板，可以有多个公式组合在一起，每个公式可以设置参数
     * 
     * @param participantAttribute
     *            公式定义的扩展属性
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

	    if ((o instanceof DeviceEndPoint) || o instanceof Device || o instanceof Command) {
		result.add(o);
	    } else if (o instanceof DeviceEndPoint[]) {
		DeviceEndPoint[] endpoints = (DeviceEndPoint[]) o;
		for (int j = 0; j < endpoints.length; j++) {
		    result.add(endpoints[j]);
		}
	    } else if (o instanceof Device[]) {
		Device[] devices = (Device[]) o;
		for (int j = 0; j < devices.length; j++) {
		    result.add(devices[j]);
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
	    Device[] devices = (Device[]) o;
	    for (int i = 0; i < devices.length; i++) {
		list.add(devices[i]);
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

	DbActivityDefDevice deviceDef = activityDeviceMgr.loadByKey(activityDefId);
	// 支持新的公式摸板
	EIAttributeDef attDef = deviceDef.getEndpointSelectedAtt();
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

	Iterator<RightCtx> keyIt = ctx.keySet().iterator();
	while (keyIt.hasNext()) {
	    RightCtx key = keyIt.next();
	    parser.addVariableAsObject(key.getType(), ctx.get(key));
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
    public ReturnType hasDeviceToStartProcess(String processInstId, Map<RightCtx, Object> ctx) throws BPMException {
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
    public Object getActivityInstHistoryDeviceAttribute(String activityInstHistoryId, AttributeName attName, Map<RightCtx, Object> ctx) throws BPMException {
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
