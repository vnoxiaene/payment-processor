package com.vnoxiaene.paymentprocessor.validator.vo;

import java.util.Set;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class SellerPaymentsResponseVO {
    String sellerCode;
    Set<PaymentResponseVO> payments;
}
