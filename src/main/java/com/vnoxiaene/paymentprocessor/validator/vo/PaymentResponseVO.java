package com.vnoxiaene.paymentprocessor.validator.vo;

import com.vnoxiaene.paymentprocessor.shared.entity.PaymentStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
public class PaymentResponseVO {
    String billingCode;
    BigDecimal amountPaid;
    PaymentStatus status;
}
