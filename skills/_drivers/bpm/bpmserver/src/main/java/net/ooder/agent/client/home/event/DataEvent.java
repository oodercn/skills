package net.ooder.agent.client.home.event;

import java.io.Serializable;
import java.util.Map;

public class DataEvent implements Serializable {
    private String source;
    private String type;
    private Map<String, Object> data;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
