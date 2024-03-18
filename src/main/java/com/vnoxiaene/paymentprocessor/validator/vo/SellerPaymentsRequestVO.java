package com.vnoxiaene.paymentprocessor.validator.vo;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class SellerPaymentsRequestVO {
    String sellerCode;
    List<PaymentRequestsVO> payments;
}
