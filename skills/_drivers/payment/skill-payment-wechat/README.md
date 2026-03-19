# skill-payment-wechat

微信支付服务，支持JSAPI支付、Native支付、H5支付、退款

## 功能特性

- JSAPI支付 - 公众号/小程序支付
- Native支付 - 扫码支付
- H5支付 - 手机网页支付
- 退款处理 - 处理退款请求

## 快速开始

### 安装

```bash
skill install skill-payment-wechat
```

### 配置

```yaml
skill-payment-wechat:
  app-id: ${WECHAT_PAY_APP_ID}
  mch-id: ${WECHAT_PAY_MCH_ID}
  api-key: ${WECHAT_PAY_API_KEY}
  cert-path: ${WECHAT_PAY_CERT_PATH}
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/payment/wechat/jsapi \
  -H "Content-Type: application/json" \
  -d '{
    "outTradeNo": "ORDER_001",
    "totalFee": 100,
    "body": "商品描述",
    "openid": "user_openid"
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [证书配置](docs/cert-config.md)
- [集成指南](docs/integration-guide.md)

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| WECHAT_PAY_APP_ID | string | 是 | 微信AppID |
| WECHAT_PAY_MCH_ID | string | 是 | 商户号 |
| WECHAT_PAY_API_KEY | string | 是 | API密钥 |
| WECHAT_PAY_CERT_PATH | string | 是 | 证书路径 |

## 许可证

Apache-2.0
