package com.tanthanh.paymentservice.repository;

import com.tanthanh.paymentservice.data.Payment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PaymentRepository extends ReactiveCrudRepository<Payment,Long> {
}
