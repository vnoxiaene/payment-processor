package com.vnoxiaene.paymentprocessor.validator.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.vnoxiaene.paymentprocessor.shared.config.EnvVariables;
import com.vnoxiaene.paymentprocessor.validator.vo.PaymentResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SQSServiceImpl implements
    SQSService {

  private final EnvVariables envVariables;

  @Override
  public void producePartialPayment(PaymentResponseVO paymentResponseVO) {
    sendMessageToSqs(envVariables.getSqsPartialPaymentQueue(), paymentResponseVO.toString());
  }

  @Override
  public void produceTotalPayment(PaymentResponseVO paymentResponseVO) {
    sendMessageToSqs(envVariables.getSqsTotalPaymentQueue(), paymentResponseVO.toString());
  }

  @Override
  public void produceExcessPayment(PaymentResponseVO paymentResponseVO) {
    sendMessageToSqs(envVariables.getSqsExcessPaymentQueue(), paymentResponseVO.toString());
  }
  private void sendMessageToSqs(String queueUrl, String messageBody) {
    AmazonSQS sqs = AmazonSQSClientBuilder.standard().build();
    SendMessageRequest send_msg_request = new SendMessageRequest()
        .withQueueUrl(queueUrl)
        .withMessageBody(messageBody)
        .withDelaySeconds(5);
    sqs.sendMessage(send_msg_request);
  }
}
