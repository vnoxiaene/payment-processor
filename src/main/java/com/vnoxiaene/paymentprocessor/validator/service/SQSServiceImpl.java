package com.vnoxiaene.paymentprocessor.validator.service;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.vnoxiaene.paymentprocessor.shared.config.EnvVariables;
import com.vnoxiaene.paymentprocessor.validator.vo.PaymentResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SQSServiceImpl implements
    SQSService {

  private final EnvVariables envVariables;
  private final AmazonSQS amazonSQS;

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



  public void sendMessageToSqs(String sqsURL,String message) {

    SendMessageRequest sendMessageRequest = null;
    try {
      sendMessageRequest = new SendMessageRequest().
          withQueueUrl(sqsURL)
          .withMessageBody(message);
      amazonSQS.sendMessage(sendMessageRequest);
    } catch (Exception e) {
      log.error("Error while trying to send message {} to topic {}",message,  sqsURL, e);
    }
  }
}
