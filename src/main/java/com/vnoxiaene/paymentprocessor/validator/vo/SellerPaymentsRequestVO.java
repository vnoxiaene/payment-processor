package com.vnoxiaene.paymentprocessor.validator.vo;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class SellerPaymentsRequestVO {
    String sellerCode;
    List<PaymentRequestsVO> payments;
}
