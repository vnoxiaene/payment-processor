package com.vnoxiaene.paymentprocessor.validator.service;

import com.vnoxiaene.paymentprocessor.validator.vo.PaymentResponseVO;

public interface SQSService {

    void producePartialPayment(PaymentResponseVO paymentResponseVO);
    void produceTotalPayment(PaymentResponseVO paymentResponseVO);
    void produceExcessPayment(PaymentResponseVO paymentResponseVO);
}
