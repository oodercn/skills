# skill-payment-unionpay

银联支付服务，支持快捷支付、网关支付、退款

## 功能特性

- 快捷支付 - 一键支付
- 网关支付 - 网银支付
- 退款处理 - 处理退款请求
- 订单查询 - 查询支付状态

## 快速开始

### 安装

```bash
skill install skill-payment-unionpay
```

### 配置

```yaml
skill-payment-unionpay:
  mer-id: ${UNIONPAY_MER_ID}
  cert-path: ${UNIONPAY_CERT_PATH}
  cert-password: ${UNIONPAY_CERT_PASSWORD}
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/payment/unionpay/pay \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "ORDER_001",
    "txnAmt": "10000",
    "orderDesc": "商品描述"
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [集成指南](docs/integration-guide.md)

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| UNIONPAY_MER_ID | string | 是 | 商户号 |
| UNIONPAY_CERT_PATH | string | 是 | 证书路径 |
| UNIONPAY_CERT_PASSWORD | string | 是 | 证书密码 |

## 许可证

Apache-2.0
