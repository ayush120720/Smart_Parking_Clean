package com.example.Smart_Parking.DTO;

public class PaymentDTO {
    private Long userId;
    private Long reserveId;
    private Double amount;
    private String method;            // <- IMPORTANT: String, not enum
    private String upiTransactionId;

    public PaymentDTO() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getReserveId() { return reserveId; }
    public void setReserveId(Long reserveId) { this.reserveId = reserveId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getUpiTransactionId() { return upiTransactionId; }
    public void setUpiTransactionId(String upiTransactionId) { this.upiTransactionId = upiTransactionId; }
}
