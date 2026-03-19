package net.ooder.skill.payment.alipay;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.payment.PaymentProvider;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class AlipayProvider implements PaymentProvider {
    
    private String appId;
    private String privateKey;
    private String alipayPublicKey;
    private String gatewayUrl = "https://openapi.alipay.com/gateway.do";
    
    public AlipayProvider() {
        this.appId = System.getenv("ALIPAY_APP_ID");
        this.privateKey = System.getenv("ALIPAY_PRIVATE_KEY");
        this.alipayPublicKey = System.getenv("ALIPAY_PUBLIC_KEY");
    }
    
    @Override
    public String getProviderType() {
        return "alipay";
    }
    
    @Override
    public List<String> getSupportedMethods() {
        return Arrays.asList(
            "page",
            "wap",
            "app",
            "native",
            "face",
            "h5"
        );
    }
    
    @Override
    public PaymentResult createPayment(PaymentRequest request) {
        log.info("Alipay createPayment: orderId={}, amount={}", request.getOrderId(), request.getAmount());
        
        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setPaymentId(request.getPaymentId());
        result.setOrderId(request.getOrderId());
        result.setStatus("created");
        result.setAmount(request.getAmount());
        
        String method = request.getMethod() != null ? request.getMethod() : "page";
        switch (method) {
            case "native":
                result.setQrCode("https://qr.alipay.com/mock-" + request.getPaymentId());
                break;
            case "page":
            case "wap":
            case "h5":
                result.setPayUrl(gatewayUrl + "?paymentId=" + request.getPaymentId());
                break;
            default:
                result.setPayUrl(gatewayUrl + "?paymentId=" + request.getPaymentId());
        }
        
        Map<String, Object> extra = new HashMap<>();
        extra.put("provider", "alipay");
        extra.put("method", method);
        extra.put("appId", appId);
        result.setExtra(extra);
        
        return result;
    }
    
    @Override
    public PaymentResult queryPayment(String paymentId) {
        log.info("Alipay queryPayment: paymentId={}", paymentId);
        
        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setPaymentId(paymentId);
        result.setStatus("pending");
        result.setAmount(BigDecimal.ZERO);
        
        return result;
    }
    
    @Override
    public PaymentResult closePayment(String paymentId) {
        log.info("Alipay closePayment: paymentId={}", paymentId);
        
        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setPaymentId(paymentId);
        result.setStatus("closed");
        
        return result;
    }
    
    @Override
    public RefundResult createRefund(RefundRequest request) {
        log.info("Alipay createRefund: paymentId={}, amount={}", request.getPaymentId(), request.getAmount());
        
        RefundResult result = new RefundResult();
        result.setSuccess(true);
        result.setRefundId(request.getRefundId());
        result.setPaymentId(request.getPaymentId());
        result.setStatus("processing");
        result.setAmount(request.getAmount());
        result.setTransactionId("ALIREFUND" + System.currentTimeMillis());
        
        return result;
    }
    
    @Override
    public RefundResult queryRefund(String refundId) {
        log.info("Alipay queryRefund: refundId={}", refundId);
        
        RefundResult result = new RefundResult();
        result.setSuccess(true);
        result.setRefundId(refundId);
        result.setStatus("success");
        
        return result;
    }
    
    @Override
    public PaymentResult handleCallback(Map<String, Object> callbackData) {
        log.info("Alipay handleCallback: {}", callbackData);
        
        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setPaymentId((String) callbackData.get("out_trade_no"));
        result.setTransactionId((String) callbackData.get("trade_no"));
        result.setStatus("paid");
        result.setPaidAt(System.currentTimeMillis());
        
        String tradeStatus = (String) callbackData.get("trade_status");
        if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
            result.setStatus("paid");
        } else if ("TRADE_CLOSED".equals(tradeStatus)) {
            result.setStatus("closed");
        }
        
        return result;
    }
    
    @Override
    public boolean verifySignature(Map<String, Object> data, String signature) {
        log.info("Alipay verifySignature");
        return true;
    }
    
    public void setAppId(String appId) {
        this.appId = appId;
    }
    
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
    
    public void setAlipayPublicKey(String alipayPublicKey) {
        this.alipayPublicKey = alipayPublicKey;
    }
    
    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }
}
