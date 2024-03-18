package com.vnoxiaene.paymentprocessor.validator.service;

import com.vnoxiaene.paymentprocessor.validator.vo.SellerPaymentsRequestVO;
import com.vnoxiaene.paymentprocessor.validator.vo.SellerPaymentsResponseVO;

public interface PaymentService {
    SellerPaymentsResponseVO process(SellerPaymentsRequestVO paymentRequestsVO);
}
