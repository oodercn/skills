# skill-payment-alipay

支付宝支付服务，支持支付、退款、查询、对账

## 功能特性

- 支付下单 - 创建支付订单
- 订单查询 - 查询支付状态
- 退款处理 - 处理退款请求
- 对账功能 - 下载对账单

## 快速开始

### 安装

```bash
skill install skill-payment-alipay
```

### 配置

```yaml
skill-payment-alipay:
  app-id: ${ALIPAY_APP_ID}
  private-key: ${ALIPAY_PRIVATE_KEY}
  alipay-public-key: ${ALIPAY_PUBLIC_KEY}
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/payment/alipay/pay \
  -H "Content-Type: application/json" \
  -d '{
    "outTradeNo": "ORDER_001",
    "totalAmount": "100.00",
    "subject": "商品名称"
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [安全指南](docs/security-guide.md)
- [集成指南](docs/integration-guide.md)

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ALIPAY_APP_ID | string | 是 | 支付宝应用ID |
| ALIPAY_PRIVATE_KEY | string | 是 | 应用私钥 |
| ALIPAY_PUBLIC_KEY | string | 是 | 支付宝公钥 |

## 许可证

Apache-2.0
