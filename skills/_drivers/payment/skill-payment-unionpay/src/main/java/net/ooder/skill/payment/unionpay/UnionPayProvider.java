package net.ooder.skill.payment.unionpay;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.payment.PaymentProvider;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class UnionPayProvider implements PaymentProvider {
    
    private String merId;
    private String certPath;
    private String certPassword;
    private String frontUrl = "https://gateway.95516.com";
    private String backUrl;
    
    public UnionPayProvider() {
        this.merId = System.getenv("UNIONPAY_MER_ID");
        this.certPath = System.getenv("UNIONPAY_CERT_PATH");
        this.certPassword = System.getenv("UNIONPAY_CERT_PASSWORD");
    }
    
    @Override
    public String getProviderType() {
        return "unionpay";
    }
    
    @Override
    public List<String> getSupportedMethods() {
        return Arrays.asList(
            "web",
            "wap",
            "app",
            "qr",
            "b2b"
        );
    }
    
    @Override
    public PaymentResult createPayment(PaymentRequest request) {
        log.info("UnionPay createPayment: orderId={}, amount={}", request.getOrderId(), request.getAmount());
        
        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setPaymentId(request.getPaymentId());
        result.setOrderId(request.getOrderId());
        result.setStatus("created");
        result.setAmount(request.getAmount());
        
        String method = request.getMethod() != null ? request.getMethod() : "web";
        switch (method) {
            case "qr":
                result.setQrCode("https://qr.95516.com/" + request.getPaymentId());
                break;
            case "web":
            case "wap":
            case "b2b":
                result.setPayUrl(frontUrl + "/gateway/api/frontTransReq.do?orderId=" + request.getPaymentId());
                break;
            case "app":
                Map<String, Object> appParams = new HashMap<>();
                appParams.put("tn", "TN" + System.currentTimeMillis());
                appParams.put("mode", "01");
                result.setExtra(appParams);
                break;
            default:
                result.setPayUrl(frontUrl + "?paymentId=" + request.getPaymentId());
        }
        
        Map<String, Object> extra = result.getExtra();
        if (extra == null) {
            extra = new HashMap<>();
        }
        extra.put("provider", "unionpay");
        extra.put("method", method);
        extra.put("merId", merId);
        result.setExtra(extra);
        
        return result;
    }
    
    @Override
    public PaymentResult queryPayment(String paymentId) {
        log.info("UnionPay queryPayment: paymentId={}", paymentId);
        
        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setPaymentId(paymentId);
        result.setStatus("pending");
        result.setAmount(BigDecimal.ZERO);
        
        return result;
    }
    
    @Override
    public PaymentResult closePayment(String paymentId) {
        log.info("UnionPay closePayment: paymentId={}", paymentId);
        
        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setPaymentId(paymentId);
        result.setStatus("closed");
        
        return result;
    }
    
    @Override
    public RefundResult createRefund(RefundRequest request) {
        log.info("UnionPay createRefund: paymentId={}, amount={}", request.getPaymentId(), request.getAmount());
        
        RefundResult result = new RefundResult();
        result.setSuccess(true);
        result.setRefundId(request.getRefundId());
        result.setPaymentId(request.getPaymentId());
        result.setStatus("processing");
        result.setAmount(request.getAmount());
        result.setTransactionId("UNIONREFUND" + System.currentTimeMillis());
        
        return result;
    }
    
    @Override
    public RefundResult queryRefund(String refundId) {
        log.info("UnionPay queryRefund: refundId={}", refundId);
        
        RefundResult result = new RefundResult();
        result.setSuccess(true);
        result.setRefundId(refundId);
        result.setStatus("success");
        
        return result;
    }
    
    @Override
    public PaymentResult handleCallback(Map<String, Object> callbackData) {
        log.info("UnionPay handleCallback: {}", callbackData);
        
        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setPaymentId((String) callbackData.get("orderId"));
        result.setTransactionId((String) callbackData.get("queryId"));
        
        String respCode = (String) callbackData.get("respCode");
        if ("00".equals(respCode)) {
            result.setStatus("paid");
            result.setPaidAt(System.currentTimeMillis());
        } else {
            result.setStatus("failed");
            result.setErrorCode(respCode);
            result.setErrorMessage((String) callbackData.get("respMsg"));
        }
        
        return result;
    }
    
    @Override
    public boolean verifySignature(Map<String, Object> data, String signature) {
        log.info("UnionPay verifySignature");
        return true;
    }
    
    public void setMerId(String merId) {
        this.merId = merId;
    }
    
    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }
    
    public void setCertPassword(String certPassword) {
        this.certPassword = certPassword;
    }
    
    public void setFrontUrl(String frontUrl) {
        this.frontUrl = frontUrl;
    }
    
    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }
}
