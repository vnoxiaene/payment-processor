package com.vnoxiaene.paymentprocessor.shared.controller;

import com.vnoxiaene.paymentprocessor.validator.service.PaymentService;
import com.vnoxiaene.paymentprocessor.validator.vo.SellerPaymentsRequestVO;
import com.vnoxiaene.paymentprocessor.validator.vo.SellerPaymentsResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/payments")
@Validated
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<SellerPaymentsResponseVO> validatePayments(@RequestBody SellerPaymentsRequestVO sellerPaymentsRequestVO){
        return new ResponseEntity<>(paymentService.process(sellerPaymentsRequestVO), HttpStatus.OK);
    }

}
