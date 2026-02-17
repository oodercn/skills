# Skill Development Guide

This guide explains how to develop skills that conform to SDK 0.7.1 specifications.

## Skill Types

### 1. Service Skill
Provides specific services like authentication, data access, etc.

### 2. Tool Skill
Provides utility functions like code generation, file processing, etc.

### 3. Enterprise Skill
Provides enterprise integration like DingTalk, Feishu organization sync.

## Development Steps

### 1. Create Project Structure

```
skill-xxx/
├── pom.xml
└── src/main/
    ├── java/net/ooder/skill/xxx/
    │   └── XxxSkillApplication.java
    └── resources/
        ├── skill.yaml
        └── application.yml
```

### 2. Configure pom.xml

```xml
<parent>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-skills</artifactId>
    <version>0.7.1</version>
</parent>

<artifactId>skill-xxx</artifactId>

<dependencies>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk</artifactId>
        <version>${agent-sdk.version}</version>
    </dependency>
</dependencies>
```

### 3. Implement Main Class

```java
@SpringBootApplication
@RestController
public class XxxSkillApplication implements CommandLineRunner {
    
    private OoderSDK sdk;
    private EndAgent endAgent;
    
    public static void main(String[] args) {
        SpringApplication.run(XxxSkillApplication.class, args);
    }
    
    @Override
    public void run(String... args) {
        LifecycleManager.getInstance().reset();
        
        SDKConfiguration config = new SDKConfiguration();
        config.setAgentId("skill-xxx-001");
        // ... more config
        
        sdk = OoderSDK.builder().configuration(config).build();
        sdk.initialize();
        sdk.start();
        
        endAgent = sdk.createEndAgent();
        endAgent.start();
    }
}
```

### 4. Create skill.yaml

Define capabilities, scenes, config, and endpoints.

## Best Practices

1. **Unique Skill ID**: Use globally unique skill IDs
2. **Capability Granularity**: Keep capabilities focused and single-purpose
3. **Configuration Security**: Mark sensitive configs as `secret: true`
4. **Health Check**: Always provide `/api/health` endpoint
5. **Graceful Shutdown**: Implement proper shutdown hooks
