package com.vnoxiaene.paymentprocessor.validator.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.vnoxiaene.paymentprocessor.shared.config.EnvVariables;
import com.vnoxiaene.paymentprocessor.shared.entity.PaymentStatus;
import com.vnoxiaene.paymentprocessor.validator.vo.PaymentResponseVO;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {SQSServiceImpl.class, EnvVariables.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class SQSServiceImplTest {

  @MockBean
  private AmazonSQS amazonSQS;


  @Autowired
  private SQSServiceImpl sQSServiceImpl;

  /**
   * Method under test: {@link SQSServiceImpl#producePartialPayment(PaymentResponseVO)}
   */
  @Test
  void testProducePartialPayment() {
    // Arrange
    when(amazonSQS.sendMessage(Mockito.<SendMessageRequest>any())).thenReturn(
        new SendMessageResult());
    PaymentResponseVO.PaymentResponseVOBuilder builderResult = PaymentResponseVO.builder();
    PaymentResponseVO paymentResponseVO = builderResult.amountPaid(new BigDecimal("2.3"))
        .billingCode("Billing Code")
        .status(PaymentStatus.PENDING)
        .build();

    // Act
    sQSServiceImpl.producePartialPayment(paymentResponseVO);

    // Assert
    verify(amazonSQS).sendMessage(Mockito.<SendMessageRequest>any());
  }

  /**
   * Method under test: {@link SQSServiceImpl#produceTotalPayment(PaymentResponseVO)}
   */
  @Test
  void testProduceTotalPayment() {
    // Arrange
    when(amazonSQS.sendMessage(Mockito.<SendMessageRequest>any())).thenReturn(
        new SendMessageResult());
    PaymentResponseVO.PaymentResponseVOBuilder builderResult = PaymentResponseVO.builder();
    PaymentResponseVO paymentResponseVO = builderResult.amountPaid(new BigDecimal("2.3"))
        .billingCode("Billing Code")
        .status(PaymentStatus.PENDING)
        .build();

    // Act
    sQSServiceImpl.produceTotalPayment(paymentResponseVO);

    // Assert
    verify(amazonSQS).sendMessage(Mockito.<SendMessageRequest>any());
  }

  /**
   * Method under test: {@link SQSServiceImpl#produceExcessPayment(PaymentResponseVO)}
   */
  @Test
  void testProduceExcessPayment() {
    // Arrange
    when(amazonSQS.sendMessage(Mockito.<SendMessageRequest>any())).thenReturn(
        new SendMessageResult());
    PaymentResponseVO.PaymentResponseVOBuilder builderResult = PaymentResponseVO.builder();
    PaymentResponseVO paymentResponseVO = builderResult.amountPaid(new BigDecimal("2.3"))
        .billingCode("Billing Code")
        .status(PaymentStatus.PENDING)
        .build();

    // Act
    sQSServiceImpl.produceExcessPayment(paymentResponseVO);

    // Assert
    verify(amazonSQS).sendMessage(Mockito.<SendMessageRequest>any());
  }

  /**
   * Method under test: {@link SQSServiceImpl#sendMessageToSqs(String, String)}
   */
  @Test
  void testSendMessageToSqs() {
    // Arrange
    when(amazonSQS.sendMessage(Mockito.<SendMessageRequest>any())).thenReturn(
        new SendMessageResult());

    // Act
    sQSServiceImpl.sendMessageToSqs("https://example.org/example", "Not all who wander are lost");

    // Assert
    verify(amazonSQS).sendMessage(Mockito.<SendMessageRequest>any());
  }
}
