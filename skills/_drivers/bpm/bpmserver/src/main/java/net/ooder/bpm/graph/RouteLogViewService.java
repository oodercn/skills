/**
 * $RCSfile: RouteLogViewAction.java,v $
 * $Revision: 1.2 $
 * $Date: 2014/12/09 00:56:56 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.graph;

import net.ooder.bpm.client.*;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.engine.inter.*;
import net.ooder.bpm.enums.activityinst.ActivityInstRightAtt;
import net.ooder.common.JDSException;
import net.ooder.common.util.DateUtility;
import net.ooder.common.util.StringUtility;
import net.ooder.esd.bpm.view.Graph;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.Person;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RouteLogViewService {

    private String activityInstId;


    private static final String SUBVIEWID = "SUBVIEWID";

    private String xml;


    EIProcessDefVersionManager eiProcessDefVersionManager = EIProcessDefVersionManager.getInstance();

    EIActivityDefManager activityDefManager = EIActivityDefManager.getInstance();

    EIRouteInstManager routeInstMgr = EIRouteInstManager.getInstance();

    EIActivityInstManager activityInstMgr = EIActivityInstManager.getInstance();


    /**
     * 生成流程定义图
     *
     * @param g
     * @param processDefVersion
     * @return
     * @throws BPMException
     */
    private XMLGraph genProcessDefView(XMLGraph g, ProcessDefVersion processDefVersion)
            throws BPMException {
        List<ActivityDef> activityDefList = processDefVersion
                .getAllActivityDefs();
        ;
        List<RouteDef> routeDefList = processDefVersion.getAllRouteDefs();
        this.genActivityDefVertex(g, activityDefList);
        this.genRouteDefEdge(g, routeDefList);
        this.genStartVertex(g, processDefVersion.getProcessDefVersionId());
        this.genEndVertex(g, processDefVersion.getProcessDefVersionId());

        return g;

    }

    /**
     * 获取开始节点
     *
     * @param g
     * @return
     * @throws BPMException
     */
    private XMLGraph genStartVertex(XMLGraph g, String processDefVersionId)
            throws BPMException {
        EIProcessDefVersion eiProcessDefVersion = eiProcessDefVersionManager
                .loadByKey(processDefVersionId);
        String[] startStrings = StringUtility.split(
                eiProcessDefVersion.getAttributeInterpretedValue(
                        "BPD.StartOfWorkflow").toString(), ";");
        String startId = (UUID.randomUUID().toString()).toString();
        String startActivityDefId = startStrings[1];
        int satrtX = Integer.valueOf(startStrings[2]);
        int satrtY = Integer.valueOf(startStrings[3]);

        Edge e = new Edge((UUID.randomUUID().toString()).toString());
        e.setSourceId(startId);
        e.setTargetId(startActivityDefId);
        e.setValue("");
        g.addEdge(e);
        Vertex v = new Vertex(startId);
        v.setValue("开始");
        v.setShape(v.SHAPE_ELLIPSE);
        v.setHeight(40);
        v.setWidth(40);
        v.setX(satrtX);
        v.setY(satrtY);
        String color = "#9BFF9B";
        v.setColor(color);
        v.setGradientColor(color);
        g.addVertexs(v);
        return g;
    }

    /**
     * 获取结束节点
     *
     * @param g
     * @return
     * @throws BPMException
     */
    private XMLGraph genEndVertex(XMLGraph g, String processDefVersionId)
            throws BPMException {
        EIProcessDefVersion eiProcessDefVersion = eiProcessDefVersionManager
                .loadByKey(processDefVersionId);
        if (eiProcessDefVersion
                .getAttributeInterpretedValue("BPD.EndOfWorkflow") != null) {
            String[] ends = StringUtility.split(eiProcessDefVersion
                    .getAttributeInterpretedValue("BPD.EndOfWorkflow")
                    .toString(), "$");
            for (int k = 0; k < ends.length; k++) {
                String[] endStrings = StringUtility.split(ends[k], ";");
                String endId = (UUID.randomUUID().toString()).toString();
                String endActivityDefId = endStrings[1];
                int endX = Integer.valueOf(endStrings[2]);
                int endY = Integer.valueOf(endStrings[3]);

                Edge e = new Edge((UUID.randomUUID().toString()).toString());
                e.setSourceId(endActivityDefId);
                e.setTargetId(endId);
                e.setValue("");
                g.addEdge(e);
                Vertex v = new Vertex(endId);
                v.setValue("结束");
                v.setShape(v.SHAPE_ELLIPSE);
                v.setHeight(40);
                v.setWidth(40);
                v.setX(endX);
                v.setY(endY);
                String color = "#C3D9FF";
                v.setColor(color);
                v.setGradientColor(color);
                g.addVertexs(v);
            }
        }

        return g;

    }

    /**
     * 添加活动定义节点
     *
     * @param g
     * @return
     * @throws BPMException
     */
    private XMLGraph genActivityDefVertex(XMLGraph g,
                                       List<ActivityDef> activityDefList) throws BPMException {
        for (int k = 0; k < activityDefList.size(); k++) {
            ActivityDef activityDef = activityDefList.get(k);
            Vertex v = new Vertex(activityDef.getActivityDefId());
            EIActivityDef eiActivityDef = activityDefManager
                    .loadByKey(activityDef.getActivityDefId());
            String text = activityDef.getName();
            v.setValue(text);
            v.setX(Integer.valueOf((String) eiActivityDef
                    .getAttributeInterpretedValue("BPD.XOffset")));
            v.setY(Integer.valueOf((String) eiActivityDef
                    .getAttributeInterpretedValue("BPD.YOffset")));
            g.addVertexs(v);

        }
        return g;
    }

    /**
     * 添加路由定义
     *
     * @param g
     * @return
     * @throws BPMException
     */
    private XMLGraph genRouteDefEdge(XMLGraph g, List<RouteDef> routeDefList)
            throws BPMException {
        for (int j = 0; j < routeDefList.size(); j++) {
            RouteDef routeDef = routeDefList.get(j);
            Edge e = new Edge(routeDef.getRouteDefId());
            e.setSourceId(routeDef.getFromActivityDef().getActivityDefId());
            e.setTargetId(routeDef.getToActivityDefId());
            e.setValue(routeDef.getName().length() > 8 ? routeDef.getName().substring(0, 8) + "..." : routeDef.getName());

            g.addEdge(e);
        }

        return g;
    }

    /**
     * 添加已完成路由
     *
     * @param g
     * @return
     * @throws BPMException
     */
    private XMLGraph genRouteInstEdge(XMLGraph g) throws BPMException {
        List<EIRouteInst> list = routeInstMgr
                .loadByWhere("WHERE  PROCESSINST_ID='"
                        + this.getProcessInst().getProcessInstId() + "'");
        for (int j = 0; j < list.size(); j++) {
            EIRouteInst eihisRouteInst = list.get(j);
            Edge ee = new Edge(eihisRouteInst.getRouteInstId());
            ee.setParent(SUBVIEWID);
            ee.setSourceId(eihisRouteInst.getFromActivityId());
            ee.setTargetId(eihisRouteInst.getToActivityId());
            ee.setValue(eihisRouteInst.getRouteName());
            g.addEdge(ee);
        }
        return g;
    }

    /**
     * @param g
     * @return
     * @throws BPMException
     */
    private XMLGraph genAcrtivityInstHistVertex(XMLGraph g, ProcessInst processInst)
            throws BPMException {
        g.addRoot(new Vertex(SUBVIEWID));

        List<ActivityInstHistory>
            activityHisList = processInst
                    .getActivityInstHistoryListByProcessInst();


        if (activityHisList.size() > 0) {
            for (int ff = 0; ff < activityHisList.size(); ff++) {
                ActivityInstHistory activityInstHistory = activityHisList
                        .get(ff);
                HistoryWrp historyWrp = new HistoryWrp(activityInstHistory,
                        ff);
                Vertex vv = new Vertex(activityInstHistory
                        .getActivityHistoryId());
                vv.setValue(historyWrp.getText());
                vv.setMessage(historyWrp.getTooltip());
                vv.setParent(SUBVIEWID);
                EIActivityDef eieActivityDef = activityDefManager
                        .loadByKey(activityInstHistory.getActivityDefId());
                vv.setX(Integer.valueOf((String) eieActivityDef
                        .getAttributeInterpretedValue("BPD.XOffset")));
                vv.setY(Integer.valueOf((String) eieActivityDef
                        .getAttributeInterpretedValue("BPD.YOffset")));

                //vv.setStatus(activityInstHistory.getRunStatus());
                vv.setColor(vv.COLOR_GREEN);
                g.addVertexs(vv);
            }
        }

        return g;
    }

    /**
     * @param g
     * @param activityInstList
     * @return
     * @throws BPMException
     */
    private XMLGraph genAcrtivityInstVertex(XMLGraph g,
                                         List<ActivityInst> activityInstList) throws BPMException {
        g.addRoot(new Vertex(SUBVIEWID));
        int xoffset = 0;

        for (int k = 0; k < activityInstList.size(); k++) {
            ActivityInst activityInst = activityInstList.get(k);
            CurrentWrp currentWrp = new CurrentWrp(activityInst,
                    k);
            Vertex v = new Vertex(activityInst.getActivityInstId());
            if (activityInst.getActivityInstHistoryListByActvityInst().size() > 0) {
                v.setValue(currentWrp.getText());
                v.setParent(SUBVIEWID);
                EIActivityDef eiActivityDef = activityDefManager
                        .loadByKey(activityInst.getActivityDefId());
                //xoffset = xoffset + 200;
                //xoffset = xoffset + 200;
                v.setX(Integer.valueOf((String) eiActivityDef
                        .getAttributeInterpretedValue("BPD.XOffset"))
                        + xoffset);
                v.setY(Integer.valueOf((String) eiActivityDef
                        .getAttributeInterpretedValue("BPD.YOffset")));
                v.setStatus(activityInst.getRunStatus().getType());
                v.setColor(v.COLOR_RED);
                g.addVertexs(v);
                List<ActivityInstHistory> activityHisList = activityInst
                        .getActivityInstHistoryListByActvityInst();

                if (activityHisList.size() > 0) {
                    for (int ff = 0; ff < activityHisList.size(); ff++) {

                        ActivityInstHistory activityInstHistory = activityHisList
                                .get(ff);
                        //没有回退的时候 MK liwenzhang  2012/06/12
                        if (!activityInst.getActivityDefId().equals(activityInstHistory.getActivityDefId())) {
                            HistoryWrp historyWrp = new HistoryWrp(
                                    activityInstHistory, ff);
                            Vertex vv = new Vertex(activityInstHistory
                                    .getActivityHistoryId());
                            vv.setValue(historyWrp.getText());
                            vv.setParent(SUBVIEWID);
                            EIActivityDef eieActivityDef = activityDefManager
                                    .loadByKey(activityInstHistory
                                            .getActivityDefId());
                            vv.setX(Integer.valueOf((String) eieActivityDef
                                    .getAttributeInterpretedValue("BPD.XOffset"))
                                    + xoffset);
                            vv.setY(Integer.valueOf((String) eieActivityDef
                                    .getAttributeInterpretedValue("BPD.YOffset")));

                            //	vv.setStatus(activityInstHistory.getRunStatus());

                            vv.setColor(vv.COLOR_GREEN);
                            g.addVertexs(vv);
                        }

                    }
                } else {

                }

            }
        }

        for (int k = 0; k < activityInstList.size(); k++) {
            ActivityInst activityInst = activityInstList.get(k);
            CurrentWrp currentWrp = new CurrentWrp(activityInst, k);
            Vertex v = new Vertex(activityInst.getActivityInstId());
            if (activityInst.getActivityInstHistoryListByActvityInst().size() == 0) {
                v.setValue(currentWrp.getText());
                v.setParent(SUBVIEWID);
                EIActivityDef eiActivityDef = activityDefManager
                        .loadByKey(activityInst.getActivityDefId());
                xoffset = xoffset + 100;

                if (activityInst.getProcessInst().getState().equals(
                        "notStarted")) {
                    v.setX(Integer.valueOf((String) eiActivityDef
                            .getAttributeInterpretedValue("BPD.XOffset")));
                    v.setY(Integer.valueOf((String) eiActivityDef
                            .getAttributeInterpretedValue("BPD.YOffset")));
                } else {
                    v.setX(Integer.valueOf((String) eiActivityDef
                            .getAttributeInterpretedValue("BPD.XOffset"))
                            + xoffset);
                    v.setY(Integer.valueOf((String) eiActivityDef
                            .getAttributeInterpretedValue("BPD.YOffset"))
                            + 30
                            + (k * 20)
                            / 2
                            - (activityInstList.size() * 10));

                }
                v.setColor(v.COLOR_RED);
                Edge ee = new Edge((UUID.randomUUID().toString()).toString());
                ee.setParent(SUBVIEWID);
                ee.setSourceId(activityInst.getActivityDefId());
                ee.setTargetId(activityInst.getActivityInstId());
                ee.setEdgeStatus(Edge.STATUS_ROUTEBACK);
                ee.setColor(v.COLOR_RED);
                g.addEdge(ee);
                g.addVertexs(v);
            }
        }

        return g;
    }


    public ProcessInst getProcessInst() throws BPMException {
        ProcessInst processInst = this.getActivityInst().getProcessInst();

        return processInst;
    }


    public ActivityInst getActivityInst() throws BPMException {

        ActivityInst activityInst = this.getClient().getActivityInst(this.activityInstId);

        return activityInst;
    }


    public String view() throws Exception {

        XMLGraph g = new XMLGraph();
        g = this.genProcessDefView(g, this.getProcessInst()
                .getProcessDefVersion());
        if (this.getProcessInst().getActivityInstList().size() > 0) {
            this.genAcrtivityInstVertex(g, this.getProcessInst()
                    .getActivityInstList());
        } else {
            this.genAcrtivityInstHistVertex(g, this.getProcessInst());
        }

        this.xml = g.getXmlStr("utf8");
        //return this.SUCCESS;
//        HttpServletResponse hsr = ServletActionContext.getResponse();
//        hsr.setContentType("text/xml;charset=utf-8");
//        PrintWriter pw = hsr.getWriter();
//        pw.write(this.xml);
        return null;
    }

    private List<ActivityInst> getActivityInstListByDefId(String activityDefId)
            throws Exception {
        List<ActivityInst> activityInstList = this.getProcessInst()
                .getActivityInstList();
        List<ActivityInst> newActivityInstList = new ArrayList<ActivityInst>();
        ;
        for (int k = 0; k < activityInstList.size(); k++) {
            ActivityInst activityInst = activityInstList.get(k);
            if (activityInst.getActivityDefId().equals(activityDefId)) {
                newActivityInstList.add(activityInst);
            }
        }
        return newActivityInstList;
    }

    private List<ActivityInstHistory> getActivityHisListByDefId(
            String activityDefId) throws Exception {
        List<ActivityInstHistory> activityHisList = this.getProcessInst()
                .getActivityInstHistoryListByProcessInst();
        List<ActivityInstHistory> newActivityHisList = new ArrayList<ActivityInstHistory>();
        ;
        for (int k = 0; k < activityHisList.size(); k++) {
            ActivityInstHistory activityHis = activityHisList.get(k);
            if (activityHis.getActivityDefId().equals(activityDefId)) {
                newActivityHisList.add(activityHis);
            }
        }
        return newActivityHisList;
    }

    private class HistoryWrp {
        private ActivityInstHistory activityInstHistory;

        private String text;

        private String tooltip;

        private WorkflowClientService client;

        List<Person> performers;

        private int index;

        public List<Person> getActivityHistoryPerformers(String activityHistoryId) throws BPMException {

            List<Person> performers = (List<Person>) this.client.getActivityInst(activityInstId).getRightAttribute(ActivityInstRightAtt.HISTORYPERFORMER);
            return performers;
        }


        public List<Person> getActivityInstPerformers(String activityInstId) throws BPMException {
            List<Person> performers = (List<Person>) this.client.getActivityInst(activityInstId).getRightAttribute(ActivityInstRightAtt.PERFORMER);
            return performers;
        }

        public List<Person> getPerformers() throws BPMException {
            if (performers == null) {
                performers = (List<Person>) this.client.getActivityInst(activityInstId).getRightAttribute(ActivityInstRightAtt.PERFORMER);
            }
            return performers;
        }

        public String getText() throws BPMException {
            List<Person> performers = this.getPerformers();

            ActivityDef activityDef = getClient()
                    .getActivityDef(activityInstHistory.getActivityDefId());
            if (performers.size() > 0) {
                Person person = performers.get(0);
//				text = person.getName() + "(" + index + ")\r"
//						+ activityInstHistory.getActivityDef().getName();
                text = "\r" + activityInstHistory.getActivityDef().getName();
            } else {
                text = "\r" + activityInstHistory.getActivityDef().getName();
            }
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public HistoryWrp(ActivityInstHistory history,
                          int index) throws BPMException {
            this.activityInstHistory = history;
            this.client = client;
            this.index = index;

        }

        public ActivityInstHistory getActivityInstHistory() {
            return activityInstHistory;
        }

        public String getArrivedTime() {
            return DateUtility.formatDate(activityInstHistory.getArrivedTime(),
                    "yyyy-MM-dd HH:mm:ss");
        }

        public String getEndTime() {
            return DateUtility.formatDate(activityInstHistory.getEndTime(),
                    "yyyy-MM-dd HH:mm:ss");
        }

        public String getTooltip() throws BPMException {
            List<Person> performers = this.getPerformers();
            tooltip = "";
            for (int k = 0; k < performers.size(); k++) {
                Person person = performers.get(k);
                tooltip = "办理人：" + person.getName() + "\r" + "办理时间："
                        + getEndTime() + "\r";
            }
            return tooltip;
        }

        public void setTooltip(String tooltip) {
            this.tooltip = tooltip;
        }

    }

    private class CurrentWrp {
        private ActivityInst activityInst;

        private String text;

        private String tooltip;

        private WorkflowClientService client;

        private int index;

        List<Person> performers;

        public List<Person> getPerformers() throws BPMException {
            if (performers == null) {

                performers = (List<Person>) this.client.getActivityInst(activityInstId).getRightAttribute(ActivityInstRightAtt.PERFORMER);

            }

            return performers;
        }

        public String getText() throws BPMException {

            performers = this.getPerformers();
            ActivityDef activityDef = client.getActivityDef(activityInst
                    .getActivityDefId());

            if (performers.size() > 0) {
                Person person = performers.get(0);
//				text = person.getName() + "(" + index + ")\r"
//						+ activityInst.getActivityDef().getName();
                text = "\r" + activityInst.getActivityDef().getName();

            } else {
                text = "\r" + activityInst.getActivityDef().getName();
            }

            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTooltip() throws BPMException {
            List<Person> performers = this.getPerformers();
            tooltip = "";
            for (int k = 0; k < performers.size(); k++) {
                Person person = performers.get(k);
                tooltip = "办理人：" + person.getName() + "\r" + "办理时间："
                        + getArrivedTime() + "\r";
            }
            return tooltip;
        }

        public CurrentWrp(ActivityInst activityInst, int index) throws BPMException {
            this.activityInst = activityInst;
            this.index = index;
        }

        public ActivityInst getActivityInst() {
            return activityInst;
        }

        public String getActivityHistoryId() {
            return activityInstId;
        }

        public String getArrivedTime() {
            return DateUtility.formatDate(activityInst.getArrivedTime(),
                    "yyyy-MM-dd HH:mm:ss");
        }

    }

    public String getActivityInstId() {
        return activityInstId;
    }

    public void setActivityInstId(String activityInstId) {
        this.activityInstId = activityInstId;
    }


    public String getXml() throws UnsupportedEncodingException {
        String xm = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
        xm += "<mxGraphModel>";
        xm += "<root>";
        xm += "<mxCell id=\"0\"/>";
        xm += "<mxCell id=\"1\" parent=\"0\"/>";
        xm += "<mxCell id=\"SUBVIEWID\" parent=\"0\"/>";
        xm += "<mxCell id=\"40869FA0-999B-11E0-9FA0-D6F9B1A7DC7C_Act1\" parent=\"1\" statusMessage=\"\" style=\"\" value=\"333333333\" vertex=\"1\">";
        xm += "<mxGeometry as=\"geometry\" height=\"40\" width=\"80\" x=\"270\" y=\"200\"/>";
        xm += "</mxCell>";
        xm += "</root>";
        xm += "</mxGraphModel>";
        return xml;
    }

    public WorkflowClientService getClient() {
        WorkflowClientService clientService = EsbUtil.parExpression(WorkflowClientService.class);
        return clientService;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

}
