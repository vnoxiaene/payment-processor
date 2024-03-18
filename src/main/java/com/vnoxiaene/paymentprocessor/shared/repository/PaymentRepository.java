package com.vnoxiaene.paymentprocessor.shared.repository;

import com.vnoxiaene.paymentprocessor.shared.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByBillingCode(String code);
    Optional<Payment> findByBillingCode(String code);
}
