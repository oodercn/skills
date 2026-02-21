package net.ooder.skill.payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PaymentProvider {
    
    String getProviderType();
    
    List<String> getSupportedMethods();
    
    PaymentResult createPayment(PaymentRequest request);
    
    PaymentResult queryPayment(String paymentId);
    
    PaymentResult closePayment(String paymentId);
    
    RefundResult createRefund(RefundRequest request);
    
    RefundResult queryRefund(String refundId);
    
    PaymentResult handleCallback(Map<String, Object> callbackData);
    
    boolean verifySignature(Map<String, Object> data, String signature);
    
    public static class PaymentRequest {
        private String paymentId;
        private String orderId;
        private String subject;
        private BigDecimal amount;
        private String currency;
        private String method;
        private String notifyUrl;
        private String returnUrl;
        private Map<String, Object> metadata;
        
        public String getPaymentId() { return paymentId; }
        public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public String getNotifyUrl() { return notifyUrl; }
        public void setNotifyUrl(String notifyUrl) { this.notifyUrl = notifyUrl; }
        public String getReturnUrl() { return returnUrl; }
        public void setReturnUrl(String returnUrl) { this.returnUrl = returnUrl; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    public static class RefundRequest {
        private String refundId;
        private String paymentId;
        private BigDecimal amount;
        private String reason;
        private Map<String, Object> metadata;
        
        public String getRefundId() { return refundId; }
        public void setRefundId(String refundId) { this.refundId = refundId; }
        public String getPaymentId() { return paymentId; }
        public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    public static class PaymentResult {
        private boolean success;
        private String paymentId;
        private String orderId;
        private String status;
        private BigDecimal amount;
        private String payUrl;
        private String qrCode;
        private String transactionId;
        private long paidAt;
        private String errorCode;
        private String errorMessage;
        private Map<String, Object> extra;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getPaymentId() { return paymentId; }
        public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getPayUrl() { return payUrl; }
        public void setPayUrl(String payUrl) { this.payUrl = payUrl; }
        public String getQrCode() { return qrCode; }
        public void setQrCode(String qrCode) { this.qrCode = qrCode; }
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public long getPaidAt() { return paidAt; }
        public void setPaidAt(long paidAt) { this.paidAt = paidAt; }
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public Map<String, Object> getExtra() { return extra; }
        public void setExtra(Map<String, Object> extra) { this.extra = extra; }
    }
    
    public static class RefundResult {
        private boolean success;
        private String refundId;
        private String paymentId;
        private String status;
        private BigDecimal amount;
        private String transactionId;
        private long refundedAt;
        private String errorCode;
        private String errorMessage;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getRefundId() { return refundId; }
        public void setRefundId(String refundId) { this.refundId = refundId; }
        public String getPaymentId() { return paymentId; }
        public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public long getRefundedAt() { return refundedAt; }
        public void setRefundedAt(long refundedAt) { this.refundedAt = refundedAt; }
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
}
