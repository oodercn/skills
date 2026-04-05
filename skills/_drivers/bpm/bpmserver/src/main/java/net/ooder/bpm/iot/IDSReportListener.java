package net.ooder.bpm.iot;

import net.ooder.common.logging.Log;


import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.agent.client.home.event.DataEvent;
import net.ooder.agent.client.home.event.DataListener;
import net.ooder.agent.client.iot.HomeException;

/**
 * @author
 * @describe 设备上报数据监听（开门等）
 * @date 2
 */
//@Component
@EsbBeanAnnotation(id = "IDSReportListener", name = "流程监听器", expressionArr = "IDSReportListener()", desc = "流程监听器")
public class IDSReportListener implements DataListener {
    @Override
    public void onData(DataEvent event) {
    }

    @Override
    public void dataReport(DataEvent event) throws HomeException {

    }

    @Override
    public void alarmReport(DataEvent event) throws HomeException {

    }

    @Override
    public void attributeReport(DataEvent event) throws HomeException {

    }

    @Override
    public String getSystemCode() {
        return null;
    }
//
//    private final Logger logger = LogFactory.getLog(com.ds.listener.DataReportListener.class);
//
//
//    public void sensnorEvent2(){
//        Cache<String, List<ProcessDefVersion>> cache = CacheManagerFactory.createCache("bpm", "SceneActivityInst");
//        //  Condition condition = new Condition(BPMConditionKey., Operator.NOT_EQUAL, ProcessDefVersionStatus.VERSION_UNDER_TEST.getType());
//
//        //  getClient().getActivityInstList()
//
//    }
//
//
//    public  void sensnorEvent(DataIndex dataIndex){
//
//        try {
//            Cache<String, List<ProcessDefVersion>> cache = CacheManagerFactory.createCache("bpm", "SceneProcess");
//            Map<String, String> contextMap = new HashMap<String, String>();
//            contextMap.put(dataIndex.getValuetype(),dataIndex.getValue());
//            DeviceEndPoint currDeviceEndPoint=    CtIotFactory.getCtIotService().getEndPointByIeee(dataIndex.getSn());
//            String epIeee=dataIndex.getSn();
//            String eventtype=dataIndex.getValuetype();
//            Map<RightCtx, Object> ctx = new HashMap<RightCtx, Object>();
//            ctx.put(RightCtx.CURRENT_DEVICEIEEE, epIeee);
//            ctx.put(RightCtx.CURRENT_EVENT_TYPE, eventtype);
//            ctx.put(RightCtx.CONTEXT_CTX, contextMap);
//            JDSActionContext.getActionContext().getContext().putAll(ctx);
//            JDSActionContext.getActionContext().getContext().putAll(contextMap);
//
//            List<ProcessDefVersion> processDefVersionList = cache.get(epIeee + eventtype);
//
//            if (processDefVersionList == null) {
//                BPMCondition condition = new BPMCondition(BPMConditionKey.PROCESSDEF_VERSION_PUBLICATIONSTATUS, Operator.NOT_EQUAL, ProcessDefVersionStatus.UNDER_TEST.getType());
//                List<ProcessDefVersion> processDefVersions = getClient().getProcessDefVersionList(condition, null, ctx);
//                processDefVersionList= new ArrayList<ProcessDefVersion>();
//                for (ProcessDefVersion defVersion : processDefVersions) {
//                    String activityDefId=  this.getClient().getFirstActivityDefInProcess(defVersion.getProcessDefVersionId()).getActivityDefId();
//                    ActivityDefEvent defEvent=this.getClient().getActivityDefEventAttribute(activityDefId);
//                    List<DeviceEndPoint>  endPoints=defEvent.getEndpoints();
//                    DeviceDataTypeKey attributeName=defEvent.getAttributeName();
//                    if (endPoints.contains(currDeviceEndPoint) && attributeName.getType().equals(dataIndex.getValuetype())){
//                        processDefVersionList.add(defVersion);
//                    }
//                }
//                cache.put(epIeee + eventtype, processDefVersionList);
//            }
//
//
//
//            if (processDefVersionList != null) {
//
//                for (ProcessDefVersion defVersion : processDefVersionList) {
//                    ProcessInst processInst =this.getClient().newProcess(defVersion.getProcessDefId(), null, null, ctx);
//                    ActivityInst activityInst = processInst.getActivityInstList().get(0);
//                    List<RouteDef> rds = activityInst.getNextRoutes();
//                    List<String> nextActivityDefList = new ArrayList<String>();
//                    List<Map<RightCtx, Object>> nextActivityDefCtxList = new ArrayList<Map<RightCtx, Object>>();
//                    for (RouteDef rd : rds) {
//                        nextActivityDefCtxList.add(ctx);
//                        nextActivityDefList.add(rd.getToActivityDefId());
//                    }
//                    if (nextActivityDefList.size() > 0) {
//                        this.getClient().routeTo(activityInst.getActivityInstId(), nextActivityDefList, nextActivityDefCtxList);
//
//                    }
//                }
//            }
//        } catch (JDSException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//    public IDSReportListener() {
//
//
//
//
//    }
//
//    @Override
//    public String getSystemCode() {
//        return null;
//    }
//
//
//
//    /**
//     * 数据上报
//     *
//     * @param event
//     * @throws HomeException
//     */
//    @Override
//    public void dataReport(final DataEvent event) throws HomeException {
//        DataIndex dataIndex = (DataIndex) event.getSource();
//        sensnorEvent(dataIndex);
//    }
//
//    /**
//     * 报警
//     *
//     * @param event
//     * @throws HomeException
//     */
//    @Override
//    public void alarmReport(final DataEvent event) throws HomeException {
//        DataIndex dataIndex = (DataIndex) event.getSource();
//        sensnorEvent(dataIndex);
//    }
//
//    /**
//     * 属性上报
//     *
//     * @param event
//     * @throws HomeException
//     */
//    @Override
//    public void attributeReport(final DataEvent event) throws HomeException {
//        DataIndex dataIndex = (DataIndex) event.getSource();
//        sensnorEvent(dataIndex);
//
//    }
//
//
//    @JSONField(serialize = false)
//    public WorkflowClientService getClient() {
//
//        WorkflowClientService client = ((WorkflowClientService) EsbUtil.parExpression("$BPMC"));
//
//        return client;
//    }
}