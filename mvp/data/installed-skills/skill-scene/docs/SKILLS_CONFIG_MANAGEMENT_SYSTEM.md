# Skills配置管理体系设计

## 一、配置管理架构总览

### 1.1 配置层级结构

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        Skills配置管理体系                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Layer 1: 系统级配置 (System Level)                                      │
│  ├── 全局配置 (Global Config)                                           │
│  ├── 环境配置 (Environment: dev/test/prod)                              │
│  └── 租户配置 (Tenant Config)                                           │
│                                                                         │
│  Layer 2: 分类级配置 (Category Level)                                    │
│  ├── LLM配置 (0x30-0x37)                                                │
│  ├── 数据库配置 (0x28-0x2F)                                             │
│  ├── 存储配置 (0x20-0x27)                                               │
│  └── ... (17种分类)                                                     │
│                                                                         │
│  Layer 3: 驱动级配置 (Driver Level)                                      │
│  ├── skill-llm-deepseek配置                                             │
│  ├── skill-db-mysql配置                                                 │
│  └── ... (具体驱动实例)                                                 │
│                                                                         │
│  Layer 4: 实例级配置 (Instance Level)                                    │
│  ├── 连接参数                                                           │
│  ├── 性能参数                                                           │
│  └── 安全参数                                                           │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 1.2 配置存储结构

```yaml
# config-hierarchy.yaml
system:
  version: "1.0.0"
  environment: "development"
  
categories:
  llm:
    selected: "skill-llm-deepseek"
    fallback: "skill-llm-ollama"
    drivers:
      skill-llm-deepseek:
        enabled: true
        priority: 1
        config:
          apiKey: "${DEEPSEEK_API_KEY}"
          model: "deepseek-chat"
          temperature: 0.7
          maxTokens: 4096
      skill-llm-ollama:
        enabled: true
        priority: 2
        config:
          baseUrl: "http://localhost:11434"
          model: "llama3"
```

---

## 二、17种分类独立分析

### 2.1 系统核心 (sys) - 0x00-0x07

**功能定位**: 系统基础设施，注册、配置、协议处理

**地址分配**:
| 地址 | 名称 | 功能 | 配置项 |
|------|------|------|--------|
| 0x00 | SYS_REGISTRY | 服务注册中心 | 注册中心地址、心跳间隔 |
| 0x01 | SYS_CONFIG | 系统配置中心 | 配置源、刷新间隔 |
| 0x02 | SYS_CAPABILITY | 能力管理服务 | 能力扫描路径、缓存策略 |
| 0x03 | SYS_PROTOCOL | 协议处理服务 | 协议映射、转换规则 |

**配置Schema**:
```yaml
sys:
  registry:
    address: "localhost:8500"
    heartbeatInterval: 30000
    retryCount: 3
  config:
    source: "file"  # file | database | nacos
    refreshInterval: 60000
    encryptEnabled: true
  capability:
    scanPaths: ["/skills", "/capabilities"]
    cacheEnabled: true
    cacheTTL: 300000
  protocol:
    supportedProtocols: ["http", "grpc", "mqtt"]
    defaultProtocol: "http"
```

**依赖关系**: 无依赖，系统启动时第一个加载

---

### 2.2 组织服务 (org) - 0x08-0x0F

**功能定位**: 用户组织管理，支持多种组织源

**地址分配**:
| 地址 | 名称 | 功能 | 配置项 |
|------|------|------|--------|
| 0x08 | ORG_LOCAL | 本地组织服务 | 用户存储、权限模型 |
| 0x09 | ORG_DINGDING | 钉钉组织服务 | AppKey、AppSecret |
| 0x0A | ORG_FEISHU | 飞书组织服务 | AppID、AppSecret |
| 0x0B | ORG_WECOM | 企业微信组织服务 | CorpID、AgentID |
| 0x0C | ORG_LDAP | LDAP组织服务 | LDAP地址、BindDN |

**配置Schema**:
```yaml
org:
  selected: "skill-org-local"
  sync:
    enabled: true
    interval: 3600000  # 1小时同步一次
    conflictStrategy: "merge"  # merge | override | skip
  
  drivers:
    skill-org-local:
      enabled: true
      config:
        storage: "database"  # database | file
        userTable: "sys_user"
        deptTable: "sys_dept"
        passwordEncoder: "bcrypt"
        
    skill-org-dingding:
      enabled: false
      config:
        appKey: "${DINGDING_APP_KEY}"
        appSecret: "${DINGDING_APP_SECRET}"
        corpId: "${DINGDING_CORP_ID}"
        agentId: "${DINGDING_AGENT_ID}"
        callbackUrl: "https://your-domain.com/callback/dingding"
        
    skill-org-feishu:
      enabled: false
      config:
        appId: "${FEISHU_APP_ID}"
        appSecret: "${FEISHU_APP_SECRET}"
        encryptKey: "${FEISHU_ENCRYPT_KEY}"
        verificationToken: "${FEISHU_VERIFY_TOKEN}"
        
    skill-org-wecom:
      enabled: false
      config:
        corpId: "${WECOM_CORP_ID}"
        agentId: "${WECOM_AGENT_ID}"
        secret: "${WECOM_SECRET}"
        token: "${WECOM_TOKEN}"
        encodingAESKey: "${WECOM_AES_KEY}"
        
    skill-org-ldap:
      enabled: false
      config:
        url: "ldap://ldap.example.com:389"
        baseDn: "dc=example,dc=com"
        bindDn: "cn=admin,dc=example,dc=com"
        bindPassword: "${LDAP_PASSWORD}"
        userFilter: "(uid={0})"
        groupFilter: "(member={0})"
```

**依赖关系**: 依赖 auth (认证服务)

---

### 2.3 认证服务 (auth) - 0x10-0x17

**功能定位**: 用户认证、令牌管理

**地址分配**:
| 地址 | 名称 | 功能 | 配置项 |
|------|------|------|--------|
| 0x10 | AUTH_USER | 用户认证服务 | 认证方式、密码策略 |
| 0x11 | AUTH_TOKEN | 令牌管理服务 | Token有效期、刷新策略 |

**配置Schema**:
```yaml
auth:
  user:
    authMethods: ["password", "oauth2", "saml", "ldap"]
    passwordPolicy:
      minLength: 8
      requireUppercase: true
      requireLowercase: true
      requireDigit: true
      requireSpecial: false
      maxAttempts: 5
      lockDuration: 1800000
    mfa:
      enabled: false
      methods: ["totp", "sms", "email"]
      
  token:
    type: "jwt"  # jwt | opaque
    issuer: "ooder-skills"
    accessTokenTTL: 7200000      # 2小时
    refreshTokenTTL: 604800000   # 7天
    algorithm: "RS256"
    privateKey: "${JWT_PRIVATE_KEY}"
    publicKey: "${JWT_PUBLIC_KEY}"
    refreshOnExpire: true
    maxConcurrentSessions: 5
```

**依赖关系**: 无依赖，系统启动时加载

---

### 2.4 网络服务 (net) - 0x18-0x1F

**功能定位**: 网络代理、DNS解析

**地址分配**:
| 地址 | 名称 | 功能 | 配置项 |
|------|------|------|--------|
| 0x18 | NET_PROXY | 网络代理服务 | 代理地址、认证 |
| 0x19 | NET_DNS | DNS服务 | DNS服务器、缓存 |

**配置Schema**:
```yaml
net:
  proxy:
    enabled: false
    httpProxy: "http://proxy.example.com:8080"
    httpsProxy: "http://proxy.example.com:8080"
    noProxy: ["localhost", "127.0.0.1", "*.internal"]
    auth:
      enabled: false
      username: "${PROXY_USER}"
      password: "${PROXY_PASSWORD}"
      
  dns:
    servers: ["8.8.8.8", "8.8.4.4"]
    cache:
      enabled: true
      ttl: 300000
      maxSize: 1000
    hosts:
      enabled: true
      file: "/etc/hosts"
```

---

### 2.5 文件存储 (vfs) - 0x20-0x27

**功能定位**: 虚拟文件系统，支持多种存储后端

**地址分配**:
| 地址 | 名称 | 功能 | 配置项 |
|------|------|------|--------|
| 0x20 | VFS_BASE | VFS基础服务 | 存储路由、缓存 |
| 0x21 | VFS_LOCAL | 本地文件存储 | 存储路径、权限 |
| 0x22 | VFS_MINIO | MinIO对象存储 | Endpoint、AccessKey |
| 0x23 | VFS_OSS | 阿里云OSS存储 | Region、Bucket |
| 0x24 | VFS_S3 | AWS S3存储 | Region、Bucket |
| 0x25 | VFS_DATABASE | 数据库存储 | 表名、字段映射 |

**配置Schema**:
```yaml
vfs:
  selected: "skill-vfs-local"
  defaultBucket: "default"
  maxFileSize: 104857600  # 100MB
  allowedTypes: ["*"]
  
  drivers:
    skill-vfs-local:
      enabled: true
      priority: 1
      config:
        basePath: "/data/storage"
        createIfNotExists: true
        permissions: "rw-r--r--"
        
    skill-vfs-minio:
      enabled: false
      priority: 2
      config:
        endpoint: "http://minio.example.com:9000"
        accessKey: "${MINIO_ACCESS_KEY}"
        secretKey: "${MINIO_SECRET_KEY}"
        region: "us-east-1"
        secure: false
        
    skill-vfs-oss:
      enabled: false
      priority: 3
      config:
        endpoint: "https://oss-cn-hangzhou.aliyuncs.com"
        accessKeyId: "${OSS_ACCESS_KEY}"
        accessKeySecret: "${OSS_SECRET_KEY}"
        bucketName: "ooder-skills"
        region: "cn-hangzhou"
        
    skill-vfs-s3:
      enabled: false
      priority: 4
      config:
        region: "us-east-1"
        accessKeyId: "${AWS_ACCESS_KEY}"
        secretAccessKey: "${AWS_SECRET_KEY}"
        bucketName: "ooder-skills"
        
    skill-vfs-database:
      enabled: false
      priority: 5
      config:
        tableName: "sys_file_storage"
        idColumn: "id"
        nameColumn: "name"
        dataColumn: "data"
        sizeColumn: "size"
```

**依赖关系**: 依赖 db (数据库)

---

### 2.6 数据库 (db) - 0x28-0x2F

**功能定位**: 数据库连接管理，支持多种数据库

**地址分配**:
| 地址 | 名称 | 功能 | 配置项 |
|------|------|------|--------|
| 0x28 | DB_MYSQL | MySQL数据库 | 连接参数、连接池 |
| 0x29 | DB_POSTGRES | PostgreSQL数据库 | 连接参数、连接池 |
| 0x2A | DB_MONGODB | MongoDB数据库 | 连接参数、连接池 |
| 0x2B | DB_REDIS | Redis缓存 | 连接参数、集群 |

**配置Schema**:
```yaml
db:
  selected: "skill-db-mysql"
  
  drivers:
    skill-db-mysql:
      enabled: true
      config:
        host: "${MYSQL_HOST:localhost}"
        port: ${MYSQL_PORT:3306}
        database: "${MYSQL_DATABASE:ooder_skills}"
        username: "${MYSQL_USER:root}"
        password: "${MYSQL_PASSWORD}"
        charset: "utf8mb4"
        timezone: "+08:00"
        pool:
          maxActive: 20
          maxIdle: 10
          minIdle: 5
          maxWait: 30000
        ssl:
          enabled: false
          verifyServer: true
          
    skill-db-postgres:
      enabled: false
      config:
        host: "${PG_HOST:localhost}"
        port: ${PG_PORT:5432}
        database: "${PG_DATABASE:ooder_skills}"
        username: "${PG_USER:postgres}"
        password: "${PG_PASSWORD}"
        schema: "public"
        sslMode: "disable"
        pool:
          maxActive: 20
          maxIdle: 10
          
    skill-db-mongodb:
      enabled: false
      config:
        uri: "${MONGODB_URI:mongodb://localhost:27017}"
        database: "${MONGODB_DATABASE:ooder_skills}"
        authDatabase: "admin"
        username: "${MONGODB_USER}"
        password: "${MONGODB_PASSWORD}"
        replicaSet: null
        pool:
          maxSize: 100
          minSize: 10
          maxWaitTime: 120000
          
    skill-db-redis:
      enabled: true
      config:
        mode: "standalone"  # standalone | cluster | sentinel
        host: "${REDIS_HOST:localhost}"
        port: ${REDIS_PORT:6379}
        password: "${REDIS_PASSWORD}"
        database: 0
        timeout: 5000
        pool:
          maxActive: 20
          maxIdle: 10
          minIdle: 5
        cluster:
          nodes: []
        sentinel:
          master: "mymaster"
          nodes: []
```

**依赖关系**: 无依赖，系统启动时加载

---

### 2.7 大语言模型 (llm) - 0x30-0x37

**功能定位**: LLM服务集成，支持多种模型提供商

**地址分配**:
| 地址 | 名称 | 功能 | 配置项 |
|------|------|------|--------|
| 0x30 | LLM_BASE | LLM基础服务 | Provider路由 |
| 0x31 | LLM_OLLAMA | Ollama本地模型 | BaseURL、模型 |
| 0x32 | LLM_OPENAI | OpenAI API | API Key、模型 |
| 0x33 | LLM_QIANWEN | 通义千问 | API Key、模型 |
| 0x34 | LLM_DEEPSEEK | DeepSeek | API Key、模型 |
| 0x35 | LLM_VOLCENGINE | 火山引擎豆包 | API Key、模型 |

**配置Schema**:
```yaml
llm:
  selected: "skill-llm-deepseek"
  fallback: "skill-llm-ollama"
  defaultModel: "deepseek-chat"
  
  global:
    temperature: 0.7
    maxTokens: 4096
    topP: 0.9
    frequencyPenalty: 0
    presencePenalty: 0
    timeout: 60000
    retryCount: 3
    
  drivers:
    skill-llm-ollama:
      enabled: true
      priority: 2
      config:
        baseUrl: "${OLLAMA_BASE_URL:http://localhost:11434}"
        defaultModel: "llama3"
        models:
          - name: "llama3"
            contextWindow: 8192
            supportsFunctionCalling: false
          - name: "qwen2"
            contextWindow: 32768
            supportsFunctionCalling: true
        timeout: 120000
        
    skill-llm-openai:
      enabled: false
      priority: 3
      config:
        apiKey: "${OPENAI_API_KEY}"
        baseUrl: "https://api.openai.com/v1"
        organization: "${OPENAI_ORG_ID}"
        defaultModel: "gpt-4o"
        models:
          - name: "gpt-4o"
            contextWindow: 128000
            supportsFunctionCalling: true
            supportsVision: true
          - name: "gpt-4-turbo"
            contextWindow: 128000
            supportsFunctionCalling: true
          - name: "gpt-3.5-turbo"
            contextWindow: 16384
            supportsFunctionCalling: true
            
    skill-llm-qianwen:
      enabled: false
      priority: 4
      config:
        apiKey: "${QIANWEN_API_KEY}"
        baseUrl: "https://dashscope.aliyuncs.com/api/v1"
        defaultModel: "qwen-max"
        models:
          - name: "qwen-max"
            contextWindow: 32768
            supportsFunctionCalling: true
          - name: "qwen-plus"
            contextWindow: 8192
            supportsFunctionCalling: true
            
    skill-llm-deepseek:
      enabled: true
      priority: 1
      config:
        apiKey: "${DEEPSEEK_API_KEY}"
        baseUrl: "https://api.deepseek.com/v1"
        defaultModel: "deepseek-chat"
        models:
          - name: "deepseek-chat"
            contextWindow: 64000
            supportsFunctionCalling: true
          - name: "deepseek-coder"
            contextWindow: 16000
            supportsFunctionCalling: true
            
    skill-llm-volcengine:
      enabled: false
      priority: 5
      config:
        apiKey: "${VOLCENGINE_API_KEY}"
        baseUrl: "https://ark.cn-beijing.volces.com/api/v3"
        defaultModel: "doubao-pro-32k"
```

**依赖关系**: 无依赖，按需加载

---

### 2.8 知识库 (know) - 0x38-0x3F

**功能定位**: 知识管理、向量检索、RAG服务

**地址分配**:
| 地址 | 名称 | 功能 | 配置项 |
|------|------|------|--------|
| 0x38 | KNOW_BASE | 知识库基础服务 | 存储配置 |
| 0x39 | KNOW_VECTOR | 向量知识库 | 向量维度、索引 |
| 0x3A | KNOW_RAG | RAG检索服务 | 检索参数 |
| 0x3B | KNOW_EMBEDDING | 嵌入服务 | 模型配置 |

**配置Schema**:
```yaml
know:
  selected: "skill-know-rag"
  
  base:
    storage: "database"  # database | filesystem
    documentPath: "/data/knowledge"
    supportedFormats: ["pdf", "docx", "txt", "md", "html"]
    maxDocumentSize: 52428800  # 50MB
    
  vector:
    enabled: true
    dimension: 1536
    indexType: "hnsw"  # hnsw | ivf | flat
    metric: "cosine"   # cosine | euclidean | dot
    indexParams:
      m: 16
      efConstruction: 200
      
  rag:
    enabled: true
    topK: 5
    scoreThreshold: 0.7
    rerank:
      enabled: true
      model: "bge-reranker"
    chunkSize: 500
    chunkOverlap: 50
    
  embedding:
    provider: "openai"  # openai | local | custom
    model: "text-embedding-3-small"
    dimension: 1536
    batchSize: 100
    
  drivers:
    skill-know-vector:
      enabled: true
      config:
        backend: "milvus"  # milvus | pinecone | weaviate | qdrant
        host: "${VECTOR_HOST:localhost}"
        port: ${VECTOR_PORT:19530}
        collection: "knowledge_vectors"
        
    skill-know-rag:
      enabled: true
      config:
        llmProvider: "deepseek"
        embeddingProvider: "openai"
        maxContextLength: 4000
```

**依赖关系**: 依赖 llm (大语言模型)、db (数据库)

---

### 2.9 支付服务 (payment) - 0x40-0x47

**功能定位**: 支付集成，支持多种支付渠道

**地址分配**:
| 地址 | 名称 | 功能 | 配置项 |
|------|------|------|--------|
| 0x40 | PAY_BASE | 支付基础服务 | 支付路由 |
| 0x41 | PAY_ALIPAY | 支付宝 | AppID、密钥 |
| 0x42 | PAY_WECHAT | 微信支付 | AppID、密钥 |
| 0x43 | PAY_UNIONPAY | 银联支付 | 商户号、密钥 |

**配置Schema**:
```yaml
payment:
  selected: "skill-pay-alipay"
  callbackUrl: "https://your-domain.com/callback/payment"
  notifyUrl: "https://your-domain.com/notify/payment"
  
  drivers:
    skill-pay-alipay:
      enabled: true
      config:
        appId: "${ALIPAY_APP_ID}"
        privateKey: "${ALIPAY_PRIVATE_KEY}"
        alipayPublicKey: "${ALIPAY_PUBLIC_KEY}"
        gateway: "https://openapi.alipay.com/gateway.do"
        signType: "RSA2"
        sandbox: false
        
    skill-pay-wechat:
      enabled: false
      config:
        appId: "${WECHAT_APP_ID}"
        mchId: "${WECHAT_MCH_ID}"
        apiKey: "${WECHAT_API_KEY}"
        apiV3Key: "${WECHAT_API_V3_KEY}"
        certPath: "/certs/wechat/apiclient_cert.p12"
        notifyUrl: "https://your-domain.com/notify/wechat"
        sandbox: false
```

**依赖关系**: 依赖 db (数据库)、vfs (文件存储-证书)

---

### 2.10 媒体服务 (media) - 0x48-0x4F

**功能定位**: 社交媒体内容发布

**地址分配**:
| 地址 | 名称 | 功能 | 配置项 |
|------|------|------|--------|
| 0x48 | MEDIA_BASE | 媒体基础服务 | 内容路由 |
| 0x49 | MEDIA_WECHAT | 微信公众号 | AppID、Secret |
| 0x4A | MEDIA_WEIBO | 微博 | AppKey、Secret |
| 0x4B | MEDIA_ZHIHU | 知乎 | ClientID、Secret |
| 0x4C | MEDIA_TOUTIAO | 头条 | AppID、Secret |

**配置Schema**:
```yaml
media:
  selected: "skill-media-wechat"
  
  drivers:
    skill-media-wechat:
      enabled: true
      config:
        appId: "${WECHAT_MP_APP_ID}"
        appSecret: "${WECHAT_MP_SECRET}"
        token: "${WECHAT_MP_TOKEN}"
        encodingAESKey: "${WECHAT_MP_AES_KEY}"
        
    skill-media-weibo:
      enabled: false
      config:
        appKey: "${WEIBO_APP_KEY}"
        appSecret: "${WEIBO_APP_SECRET}"
        redirectUri: "https://your-domain.com/callback/weibo"
        
    skill-media-zhihu:
      enabled: false
      config:
        clientId: "${ZHIHU_CLIENT_ID}"
        clientSecret: "${ZHIHU_CLIENT_SECRET}"
        redirectUri: "https://your-domain.com/callback/zhihu"
```

---

### 2.11 通讯服务 (comm) - 0x50-0x57

**功能定位**: 消息通讯、通知推送

**地址分配**:
| 地址 | 名称 | 功能 | 配置项 |
|------|------|------|--------|
| 0x50 | COMM_BASE | 通讯基础服务 | 消息路由 |
| 0x51 | COMM_MQTT | MQTT服务 | Broker地址 |
| 0x52 | COMM_EMAIL | 邮件服务 | SMTP配置 |
| 0x53 | COMM_NOTIFY | 通知服务 | 通知渠道 |
| 0x54 | COMM_IM | 即时通讯 | IM配置 |
| 0x55 | COMM_GROUP | 群组通讯 | 群组配置 |

**配置Schema**:
```yaml
comm:
  mqtt:
    enabled: true
    broker: "tcp://localhost:1883"
    clientId: "ooder-skills-server"
    username: "${MQTT_USER}"
    password: "${MQTT_PASSWORD}"
    cleanSession: true
    keepAlive: 60
    qos: 1
    
  email:
    enabled: true
    host: "${SMTP_HOST:smtp.example.com}"
    port: ${SMTP_PORT:587}
    username: "${SMTP_USER}"
    password: "${SMTP_PASSWORD}"
    from: "noreply@example.com"
    fromName: "Ooder Skills"
    tls: true
    ssl: false
    
  notify:
    enabled: true
    channels: ["email", "sms", "push"]
    templates:
      path: "/templates/notify"
      
  im:
    enabled: false
    provider: "custom"
    baseUrl: "${IM_BASE_URL}"
    appId: "${IM_APP_ID}"
    appSecret: "${IM_APP_SECRET}"
```

---

### 2.12 监控服务 (mon) - 0x58-0x5F

**功能定位**: 系统监控、健康检查、日志管理

**地址分配**:
| 地址 | 名称 | 功能 | 配置项 |
|------|------|------|--------|
| 0x58 | MON_BASE | 监控基础服务 | 监控配置 |
| 0x59 | MON_HEALTH | 健康检查 | 检查间隔 |
| 0x5A | MON_AGENT | 代理管理 | Agent配置 |
| 0x5B | MON_METRICS | 指标采集 | 指标配置 |
| 0x5C | MON_LOG | 日志服务 | 日志配置 |

**配置Schema**:
```yaml
mon:
  health:
    enabled: true
    checkInterval: 30000
    services:
      - name: "database"
        type: "db"
        critical: true
      - name: "redis"
        type: "cache"
        critical: true
      - name: "llm"
        type: "external"
        critical: false
        
  metrics:
    enabled: true
    interval: 60000
    retention: 7  # days
    export:
      prometheus:
        enabled: true
        port: 9090
        
  log:
    enabled: true
    level: "INFO"
    format: "json"
    output: ["console", "file"]
    file:
      path: "/var/log/ooder-skills"
      maxSize: 104857600  # 100MB
      maxBackups: 10
      maxAge: 30
      compress: true
```

---

### 2.13 物联网 (iot) - 0x60-0x67

**功能定位**: IoT设备管理、容器编排

**地址分配**:
| 地址 | 名称 | 功能 | 配置项 |
|------|------|------|--------|
| 0x60 | IOT_BASE | 物联网基础服务 | 设备管理 |
| 0x61 | IOT_K8S | Kubernetes | K8s配置 |
| 0x62 | IOT_HOSTING | 托管服务 | 托管配置 |
| 0x63 | IOT_OPENWRT | OpenWrt | 路由器配置 |

**配置Schema**:
```yaml
iot:
  k8s:
    enabled: false
    kubeconfig: "${KUBECONFIG:/etc/kubernetes/config}"
    context: "default"
    namespace: "ooder-skills"
    
  hosting:
    enabled: false
    provider: "aws"  # aws | azure | gcp | aliyun
    region: "us-east-1"
    cluster: "ooder-skills-cluster"
    
  openwrt:
    enabled: false
    host: "${OPENWRT_HOST}"
    username: "${OPENWRT_USER}"
    password: "${OPENWRT_PASSWORD}"
```

---

### 2.14 搜索服务 (search) - 0x68-0x6F

**功能定位**: 全文搜索、索引管理

**地址分配**:
| 地址 | 名称 | 功能 | 配置项 |
|------|------|------|--------|
| 0x68 | SEARCH_BASE | 搜索基础服务 | 搜索配置 |
| 0x69 | SEARCH_ES | Elasticsearch | ES配置 |

**配置Schema**:
```yaml
search:
  selected: "skill-search-es"
  
  drivers:
    skill-search-base:
      enabled: true
      config:
        type: "lucene"
        indexPath: "/data/search/index"
        analyzer: "standard"
        
    skill-search-es:
      enabled: true
      config:
        hosts: ["http://localhost:9200"]
        username: "${ES_USER}"
        password: "${ES_PASSWORD}"
        indexPrefix: "ooder_"
        numberOfShards: 3
        numberOfReplicas: 1
        refreshInterval: "1s"
```

---

### 2.15 调度服务 (sched) - 0x70-0x77

**功能定位**: 定时任务调度

**地址分配**:
| 地址 | 名称 | 功能 | 配置项 |
|------|------|------|--------|
| 0x70 | SCHED_BASE | 调度基础服务 | 调度配置 |
| 0x71 | SCHED_QUARTZ | Quartz调度器 | Quartz配置 |

**配置Schema**:
```yaml
sched:
  selected: "skill-sched-quartz"
  
  drivers:
    skill-sched-base:
      enabled: true
      config:
        poolSize: 10
        threadNamePrefix: "scheduler-"
        
    skill-sched-quartz:
      enabled: true
      config:
        instanceName: "OoderSkillsScheduler"
        instanceId: "AUTO"
        threadPool:
          class: "org.quartz.simpl.SimpleThreadPool"
          threadCount: 10
          threadPriority: 5
        jobStore:
          class: "org.quartz.impl.jdbcjobstore.JobStoreTX"
          driverDelegate: "org.quartz.impl.jdbcjobstore.StdJDBCDelegate"
          tablePrefix: "QRTZ_"
          isClustered: true
          clusterCheckinInterval: 20000
```

---

### 2.16 安全服务 (sec) - 0x78-0x7F

**功能定位**: 访问控制、审计日志

**地址分配**:
| 地址 | 名称 | 功能 | 配置项 |
|------|------|------|--------|
| 0x78 | SEC_BASE | 安全基础服务 | 安全配置 |
| 0x79 | SEC_ACCESS | 访问控制 | RBAC配置 |
| 0x7A | SEC_AUDIT | 审计服务 | 审计配置 |

**配置Schema**:
```yaml
sec:
  access:
    enabled: true
    model: "rbac"  # rbac | abac | acl
    superAdmin: "admin"
    defaultRole: "user"
    cache:
      enabled: true
      ttl: 300000
      
  audit:
    enabled: true
    logAll: false
    logMethods: ["POST", "PUT", "DELETE"]
    sensitiveFields: ["password", "token", "secret"]
    storage: "database"
    retention: 90  # days
    
  encryption:
    enabled: true
    algorithm: "AES-256-GCM"
    key: "${ENCRYPTION_KEY}"
```

---

### 2.17 工具服务 (util) - 0xF0-0xFF

**功能定位**: 通用工具服务

**地址分配**:
| 地址 | 名称 | 功能 | 配置项 |
|------|------|------|--------|
| 0xF0 | UTIL_BASE | 工具基础服务 | 工具配置 |
| 0xF1 | UTIL_REPORT | 报表服务 | 报表配置 |
| 0xF2 | UTIL_SHARE | 分享服务 | 分享配置 |
| 0xF3 | UTIL_MARKET | 技能市场 | 市场配置 |

**配置Schema**:
```yaml
util:
  report:
    enabled: true
    templates:
      path: "/templates/reports"
    formats: ["pdf", "excel", "html"]
    storage:
      path: "/data/reports"
      retention: 30
      
  share:
    enabled: true
    baseUrl: "https://your-domain.com/share"
    expireDefault: 604800  # 7天
    maxExpire: 2592000     # 30天
    
  market:
    enabled: true
    registryUrl: "https://market.ooder.io"
    cache:
      enabled: true
      ttl: 3600000
```

---

## 三、配置管理API设计

### 3.1 配置CRUD API

```http
# 获取分类配置
GET /api/v1/config/categories/{category}

# 更新分类配置
PUT /api/v1/config/categories/{category}

# 获取驱动配置
GET /api/v1/config/drivers/{driverId}

# 更新驱动配置
PUT /api/v1/config/drivers/{driverId}

# 测试驱动连接
POST /api/v1/config/drivers/{driverId}/test

# 获取配置历史
GET /api/v1/config/history/{category}

# 回滚配置
POST /api/v1/config/rollback/{category}/{version}
```

### 3.2 配置验证规则

每个配置项都有对应的验证规则：

```yaml
validation:
  llm:
    apiKey:
      required: true
      pattern: "^[a-zA-Z0-9_-]{32,}$"
    model:
      required: true
      enum: ["deepseek-chat", "gpt-4o", "qwen-max"]
    temperature:
      min: 0
      max: 2
      type: "number"
    maxTokens:
      min: 1
      max: 128000
      type: "integer"
      
  db:
    host:
      required: true
      pattern: "^[a-zA-Z0-9.-]+$"
    port:
      min: 1
      max: 65535
      type: "integer"
    database:
      required: true
      pattern: "^[a-zA-Z0-9_]+$"
```

---

## 四、配置管理流程

### 4.1 配置加载流程

```
1. 系统启动
   ├── 加载系统级配置 (sys)
   ├── 加载认证配置 (auth)
   ├── 加载数据库配置 (db)
   └── 加载其他分类配置

2. 配置验证
   ├── 格式验证
   ├── 依赖验证
   └── 连接测试

3. 配置生效
   ├── 注册服务
   ├── 初始化连接
   └── 启动服务
```

### 4.2 配置更新流程

```
1. 接收配置更新请求
   ├── 权限验证
   ├── 参数验证
   └── 依赖检查

2. 执行配置更新
   ├── 备份当前配置
   ├── 应用新配置
   └── 记录变更日志

3. 验证配置生效
   ├── 连接测试
   ├── 功能验证
   └── 回滚机制
```

---

**文档版本**: 1.0  
**创建日期**: 2026-03-12  
**作者**: AI Assistant
