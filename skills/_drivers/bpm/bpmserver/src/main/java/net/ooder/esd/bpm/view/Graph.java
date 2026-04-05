package net.ooder.esd.bpm.view;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    String alias = "demoPage";
    String key = "xui.UI.SVGPaper";
    String host = "this";

    List<Object> activiteNode = new ArrayList<>();
    List<Object> routeNodes = new ArrayList<>();
    List<Object> startNodes = new ArrayList<>();
    List<Object> endNodes = new ArrayList<>();

    @JSONField(serialize = false)
    Map<String, Object> nodeMap = new HashMap<String, Object>();

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<Object> getActiviteNode() {
        return activiteNode;
    }

    public void setActiviteNode(List<Object> activiteNode) {
        this.activiteNode = activiteNode;
    }

    public List<Object> getRouteNodes() {
        return routeNodes;
    }

    public void setRouteNodes(List<Object> routeNodes) {
        this.routeNodes = routeNodes;
    }

    public List<Object> getStartNodes() {
        return startNodes;
    }

    public void setStartNodes(List<Object> startNodes) {
        this.startNodes = startNodes;
    }

    public List<Object> getEndNodes() {
        return endNodes;
    }

    public void setEndNodes(List<Object> endNodes) {
        this.endNodes = endNodes;
    }

    public Object getNode(String alias) {
        return nodeMap.get(alias);
    }

    public void addChileNode(Object node) {
        // Stub implementation
    }
}
