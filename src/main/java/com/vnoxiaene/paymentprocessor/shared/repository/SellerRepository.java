package com.vnoxiaene.paymentprocessor.shared.repository;

import com.vnoxiaene.paymentprocessor.shared.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    boolean existsByCode(String sellerCode);
}
