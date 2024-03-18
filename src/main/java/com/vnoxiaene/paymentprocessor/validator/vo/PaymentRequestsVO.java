package com.vnoxiaene.paymentprocessor.validator.vo;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
public class PaymentRequestsVO {
    String billingCode;
    BigDecimal amountPaid;
}
