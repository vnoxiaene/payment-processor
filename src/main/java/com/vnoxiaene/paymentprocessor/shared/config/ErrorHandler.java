package com.vnoxiaene.paymentprocessor.shared.config;

import com.vnoxiaene.paymentprocessor.shared.exception.PaymentNotFoundException;
import com.vnoxiaene.paymentprocessor.shared.exception.SellerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandler {

  @ExceptionHandler(PaymentNotFoundException.class)
  public ResponseEntity<Object> handleNoHandlerFoundException(PaymentNotFoundException ex) {
    return new ResponseEntity<>("payment not found", HttpStatus.NOT_FOUND);
  }
  @ExceptionHandler(SellerNotFoundException.class)
  public ResponseEntity<Object> handleNoHandlerFoundException(SellerNotFoundException ex) {
    return new ResponseEntity<>("seller not found", HttpStatus.NOT_FOUND);
  }
}
