package com.example.Smart_Parking.Model;

public enum PaymentMethod {
    UPI,
    CARD,
    CASH;

    public static PaymentMethod fromString(String value) {
        return PaymentMethod.valueOf(value.trim().toUpperCase());
    }
}