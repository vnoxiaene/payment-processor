package com.vnoxiaene.paymentprocessor.validator.vo;

import com.vnoxiaene.paymentprocessor.shared.entity.PaymentStatus;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class PaymentResponseVO {
    String billingCode;
    BigDecimal amountPaid;
    PaymentStatus status;
}
