package com.vnoxiaene.paymentprocessor.validator.vo;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class PaymentRequestsVO {
    String billingCode;
    BigDecimal amountPaid;
}
