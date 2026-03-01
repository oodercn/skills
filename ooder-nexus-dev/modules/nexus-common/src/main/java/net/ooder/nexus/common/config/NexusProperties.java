package net.ooder.nexus.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "nexus")
public class NexusProperties {
    
    private Product product = new Product();
    private P2p p2p = new P2p();
    private Push push = new Push();
    private Llm llm = new Llm();
    private Skill skill = new Skill();
    private Map<String, Object> features = new HashMap<>();
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public P2p getP2p() { return p2p; }
    public void setP2p(P2p p2p) { this.p2p = p2p; }
    public Push getPush() { return push; }
    public void setPush(Push push) { this.push = push; }
    public Llm getLlm() { return llm; }
    public void setLlm(Llm llm) { this.llm = llm; }
    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }
    public Map<String, Object> getFeatures() { return features; }
    public void setFeatures(Map<String, Object> features) { this.features = features; }
    
    public static class Product {
        private String id;
        private String name;
        private String version;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
    }
    
    public static class P2p {
        private boolean enabled = true;
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
    
    public static class Push {
        private boolean enabled = false;
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
    
    public static class Llm {
        private boolean enabled = false;
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
    
    public static class Skill {
        private boolean dynamic = false;
        private K8s k8s = new K8s();
        private Hosting hosting = new Hosting();
        
        public boolean isDynamic() { return dynamic; }
        public void setDynamic(boolean dynamic) { this.dynamic = dynamic; }
        public K8s getK8s() { return k8s; }
        public void setK8s(K8s k8s) { this.k8s = k8s; }
        public Hosting getHosting() { return hosting; }
        public void setHosting(Hosting hosting) { this.hosting = hosting; }
    }
    
    public static class K8s {
        private boolean enabled = false;
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
    
    public static class Hosting {
        private boolean enabled = false;
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
}
