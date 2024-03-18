package com.vnoxiaene.paymentprocessor.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@lombok.Value
public class EnvVariables {

  String sqsPartialPaymentQueue;
  String sqsTotalPaymentQueue;
  String sqsExcessPaymentQueue;


  public EnvVariables(@Value("${sqs.queue.partial.payments}") String sqsPartialPaymentQueue,@Value("${sqs.queue.total.payments}") String sqsTotalPaymentQueue,
      @Value("${sqs.queue.excess.payments}") String sqsExcessPaymentQueue) {
    this.sqsPartialPaymentQueue = sqsPartialPaymentQueue;
    this.sqsTotalPaymentQueue = sqsTotalPaymentQueue;
    this.sqsExcessPaymentQueue = sqsExcessPaymentQueue;
  }
}
