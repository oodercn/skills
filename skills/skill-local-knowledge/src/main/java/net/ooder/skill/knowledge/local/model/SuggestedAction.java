package net.ooder.skill.knowledge.local.model;

public class SuggestedAction {
    
    private String type;
    private String url;
    private String apiEndpoint;
    private String method;
    private Object data;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getApiEndpoint() { return apiEndpoint; }
    public void setApiEndpoint(String apiEndpoint) { this.apiEndpoint = apiEndpoint; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
