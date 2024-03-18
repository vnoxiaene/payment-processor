package com.vnoxiaene.paymentprocessor.validator.vo;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder(toBuilder = true)
public class SellerPaymentsResponseVO {
    String sellerCode;
    Set<PaymentResponseVO> payments;
}
