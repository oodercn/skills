package net.ooder.bpm.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Edge extends GraphNode {
    private GraphNode source;
    private GraphNode target;
    private String sourceId;
    private String targetId;
    private List<Point> points;

    private String edgeStatus;

    private boolean dashed;//是否是虚线


    //endArrow
    public static final String ARROW_DIAMOND = "diamond";//方点
    public static final String ARROW_CLASSIC = "classic"; //小箭头
    public static final String ARROW_BLOCK = "block"; //实心箭头
    public static final String ARROW_OPEN = "open";   //小箭头
    public static final String ARROW_OVAL = "oval";   //圆点

    public static final String colorName = "strokeColor";

    public static final Map<String, String> edgeStatusMap = new HashMap<String, String>();


    /**
     * 没到达
     */
    public static final String STATUS_NOTROUTE = "notroute";

    /**
     * 已到达
     */
    public static final String STATUS_ROUTED = "routed";

    /**
     * 能到达
     */
    public static final String STATUS_CANROUTE = "canroute";

    /**
     * 不能到达
     */
    public static final String STATUS_NOTCANROUTE = "notcantroute";

    /**
     * 特送
     */
    public static final String STATUS_SPECIALSEND = "specialsend";

    /**
     * 退回
     */
    public static final String STATUS_ROUTEBACK = "routeback";

    static {
        edgeStatusMap.put(STATUS_NOTROUTE, "endArrow=" + ARROW_DIAMOND + ";strokeColor=#CAC8BB;");//灰色
        edgeStatusMap.put(STATUS_ROUTED, "strokeColor=#000;");//黑色
        edgeStatusMap.put(STATUS_CANROUTE, "");
        edgeStatusMap.put(STATUS_NOTCANROUTE, "dashed=true;");//虚线
        edgeStatusMap.put(STATUS_SPECIALSEND, "edgeStyle=loopEdgeStyle;strokeColor=#000;");//拐弯 黑
        edgeStatusMap.put(STATUS_ROUTEBACK, "edgeStyle=loopEdgeStyle;strokeColor=#f00;");//拐弯 红
    }


    public String getEdgeStatus() {
        return edgeStatus;
    }

    public void setEdgeStatus(String edgeStatus) {
        this.edgeStatus = edgeStatus;
    }

    public boolean isDashed() {
        return dashed;
    }

    public void setDashed(boolean dashed) {
        this.dashed = dashed;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void addPoint(Point point) {
        if (this.points == null) {
            this.points = new ArrayList<Point>();
        }
        this.points.add(point);
    }


    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public GraphNode getSource() {
        return source;
    }

    private void setSource(GraphNode source) {
        this.source = source;
        this.sourceId = source.getId();
    }

    public GraphNode getTarget() {
        return target;
    }

    private void setTarget(GraphNode target) {
        this.target = target;
        this.targetId = target.getId();
    }

    public Edge(String id) {
        this.id = id;
        this.type = GraphNode.EDGE;
    }
}

