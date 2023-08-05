package com.tanthanh.paymentservice.model;

import com.tanthanh.paymentservice.data.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private long id;
    private String accountId;
    private double amount;
    private String status;

    public static PaymentDTO entityToDto(Payment payment){
        return  PaymentDTO.builder()
                .id(payment.getId())
                .accountId(payment.getAccountId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .build();
    }
    public static Payment dtoToEntity(PaymentDTO paymentDTO){
        return  Payment.builder()
                .accountId(paymentDTO.getAccountId())
                .amount(paymentDTO.getAmount())
                .status(paymentDTO.getStatus())
                .build();
    }
}
