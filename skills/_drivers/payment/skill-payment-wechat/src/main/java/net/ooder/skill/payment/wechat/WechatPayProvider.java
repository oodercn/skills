package net.ooder.skill.payment.wechat;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.payment.PaymentProvider;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class WechatPayProvider implements PaymentProvider {
    
    private String appId;
    private String mchId;
    private String apiKey;
    private String apiV3Key;
    private String certPath;
    private String notifyUrl = "https://api.mch.weixin.qq.com";
    
    public WechatPayProvider() {
        this.appId = System.getenv("WECHAT_APP_ID");
        this.mchId = System.getenv("WECHAT_MCH_ID");
        this.apiKey = System.getenv("WECHAT_API_KEY");
        this.apiV3Key = System.getenv("WECHAT_API_V3_KEY");
    }
    
    @Override
    public String getProviderType() {
        return "wechat";
    }
    
    @Override
    public List<String> getSupportedMethods() {
        return Arrays.asList(
            "jsapi",
            "native",
            "app",
            "h5",
            "mini_program",
            "face"
        );
    }
    
    @Override
    public PaymentResult createPayment(PaymentRequest request) {
        log.info("WechatPay createPayment: orderId={}, amount={}", request.getOrderId(), request.getAmount());
        
        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setPaymentId(request.getPaymentId());
        result.setOrderId(request.getOrderId());
        result.setStatus("created");
        result.setAmount(request.getAmount());
        
        String method = request.getMethod() != null ? request.getMethod() : "native";
        switch (method) {
            case "native":
                result.setQrCode("weixin://wxpay/bizpayurl?pr=" + request.getPaymentId());
                break;
            case "jsapi":
            case "mini_program":
                Map<String, Object> jsapiParams = new HashMap<>();
                jsapiParams.put("appId", appId);
                jsapiParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
                jsapiParams.put("nonceStr", UUID.randomUUID().toString());
                jsapiParams.put("package", "prepay_id=wx" + request.getPaymentId());
                jsapiParams.put("signType", "RSA");
                result.setExtra(jsapiParams);
                break;
            case "h5":
                result.setPayUrl("https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?prepay_id=" + request.getPaymentId());
                break;
            default:
                result.setPayUrl(notifyUrl + "?paymentId=" + request.getPaymentId());
        }
        
        Map<String, Object> extra = result.getExtra();
        if (extra == null) {
            extra = new HashMap<>();
        }
        extra.put("provider", "wechat");
        extra.put("method", method);
        extra.put("appId", appId);
        extra.put("mchId", mchId);
        result.setExtra(extra);
        
        return result;
    }
    
    @Override
    public PaymentResult queryPayment(String paymentId) {
        log.info("WechatPay queryPayment: paymentId={}", paymentId);
        
        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setPaymentId(paymentId);
        result.setStatus("pending");
        result.setAmount(BigDecimal.ZERO);
        
        return result;
    }
    
    @Override
    public PaymentResult closePayment(String paymentId) {
        log.info("WechatPay closePayment: paymentId={}", paymentId);
        
        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setPaymentId(paymentId);
        result.setStatus("closed");
        
        return result;
    }
    
    @Override
    public RefundResult createRefund(RefundRequest request) {
        log.info("WechatPay createRefund: paymentId={}, amount={}", request.getPaymentId(), request.getAmount());
        
        RefundResult result = new RefundResult();
        result.setSuccess(true);
        result.setRefundId(request.getRefundId());
        result.setPaymentId(request.getPaymentId());
        result.setStatus("processing");
        result.setAmount(request.getAmount());
        result.setTransactionId("WXREFUND" + System.currentTimeMillis());
        
        return result;
    }
    
    @Override
    public RefundResult queryRefund(String refundId) {
        log.info("WechatPay queryRefund: refundId={}", refundId);
        
        RefundResult result = new RefundResult();
        result.setSuccess(true);
        result.setRefundId(refundId);
        result.setStatus("success");
        
        return result;
    }
    
    @Override
    public PaymentResult handleCallback(Map<String, Object> callbackData) {
        log.info("WechatPay handleCallback: {}", callbackData);
        
        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setPaymentId((String) callbackData.get("out_trade_no"));
        result.setTransactionId((String) callbackData.get("transaction_id"));
        
        String tradeState = (String) callbackData.get("trade_state");
        switch (tradeState) {
            case "SUCCESS":
                result.setStatus("paid");
                result.setPaidAt(System.currentTimeMillis());
                break;
            case "REFUND":
                result.setStatus("refunded");
                break;
            case "NOTPAY":
            case "USERPAYING":
                result.setStatus("pending");
                break;
            case "CLOSED":
            case "PAYERROR":
                result.setStatus("failed");
                break;
            default:
                result.setStatus("unknown");
        }
        
        return result;
    }
    
    @Override
    public boolean verifySignature(Map<String, Object> data, String signature) {
        log.info("WechatPay verifySignature");
        return true;
    }
    
    public void setAppId(String appId) {
        this.appId = appId;
    }
    
    public void setMchId(String mchId) {
        this.mchId = mchId;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public void setApiV3Key(String apiV3Key) {
        this.apiV3Key = apiV3Key;
    }
    
    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }
    
    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
}
