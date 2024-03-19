package com.vnoxiaene.paymentprocessor.validator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vnoxiaene.paymentprocessor.shared.entity.Payment;
import com.vnoxiaene.paymentprocessor.shared.entity.PaymentStatus;
import com.vnoxiaene.paymentprocessor.shared.entity.Seller;
import com.vnoxiaene.paymentprocessor.shared.exception.PaymentNotFoundException;
import com.vnoxiaene.paymentprocessor.shared.exception.SellerNotFoundException;
import com.vnoxiaene.paymentprocessor.shared.repository.PaymentRepository;
import com.vnoxiaene.paymentprocessor.shared.repository.SellerRepository;
import com.vnoxiaene.paymentprocessor.validator.vo.PaymentRequestsVO;
import com.vnoxiaene.paymentprocessor.validator.vo.PaymentResponseVO;
import com.vnoxiaene.paymentprocessor.validator.vo.SellerPaymentsRequestVO;
import com.vnoxiaene.paymentprocessor.validator.vo.SellerPaymentsResponseVO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {PaymentServiceImpl.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class PaymentServiceImplTest {

  @MockBean
  private PaymentRepository paymentRepository;

  @Autowired
  private PaymentServiceImpl paymentServiceImpl;

  @MockBean
  private SQSService sQSService;

  @MockBean
  private SellerRepository sellerRepository;

  /**
   * Method under test: {@link PaymentServiceImpl#process(SellerPaymentsRequestVO)}
   */
  @Test
  void testProcess() {
    // Arrange
    when(sellerRepository.existsByCode(Mockito.<String>any())).thenReturn(true);
    SellerPaymentsRequestVO.SellerPaymentsRequestVOBuilder builderResult = SellerPaymentsRequestVO.builder();
    SellerPaymentsRequestVO sellerPaymentsRequestVO = builderResult.payments(new ArrayList<>())
        .sellerCode("Seller Code")
        .build();

    // Act
    SellerPaymentsResponseVO actualProcessResult = paymentServiceImpl.process(
        sellerPaymentsRequestVO);

    // Assert
    verify(sellerRepository).existsByCode(eq("Seller Code"));
    assertEquals("Seller Code", actualProcessResult.getSellerCode());
    assertTrue(actualProcessResult.getPayments().isEmpty());
  }

  /**
   * Method under test: {@link PaymentServiceImpl#process(SellerPaymentsRequestVO)}
   */
  @Test
  void testProcess2() {
    // Arrange
    when(sellerRepository.existsByCode(Mockito.<String>any())).thenReturn(false);
    SellerPaymentsRequestVO.SellerPaymentsRequestVOBuilder builderResult = SellerPaymentsRequestVO.builder();
    SellerPaymentsRequestVO sellerPaymentsRequestVO = builderResult.payments(new ArrayList<>())
        .sellerCode("Seller Code")
        .build();

    // Act and Assert
    assertThrows(SellerNotFoundException.class,
        () -> paymentServiceImpl.process(sellerPaymentsRequestVO));
    verify(sellerRepository).existsByCode(eq("Seller Code"));
  }

  /**
   * Method under test: {@link PaymentServiceImpl#process(SellerPaymentsRequestVO)}
   */
  @Test
  void testProcess3() {
    // Arrange
    when(sellerRepository.existsByCode(Mockito.<String>any())).thenReturn(true);

    Seller seller = new Seller();
    seller.setCode("Code");
    seller.setId(1L);
    seller.setName("Name");

    Payment payment = new Payment();
    payment.setAmount(new BigDecimal("2.3"));
    payment.setAmountPaid(new BigDecimal("2.3"));
    payment.setBillingCode("Billing Code");
    payment.setId(1L);
    payment.setSeller(seller);
    payment.setStatus(PaymentStatus.PENDING);
    Optional<Payment> ofResult = Optional.of(payment);
    when(paymentRepository.findByBillingCode(Mockito.<String>any())).thenReturn(ofResult);
    when(paymentRepository.existsByBillingCode(Mockito.<String>any())).thenReturn(true);
    doNothing().when(sQSService).produceTotalPayment(Mockito.<PaymentResponseVO>any());

    ArrayList<PaymentRequestsVO> payments = new ArrayList<>();
    PaymentRequestsVO.PaymentRequestsVOBuilder builderResult = PaymentRequestsVO.builder();
    PaymentRequestsVO buildResult = builderResult.amountPaid(new BigDecimal("2.3"))
        .billingCode("Billing Code").build();
    payments.add(buildResult);
    SellerPaymentsRequestVO.SellerPaymentsRequestVOBuilder builderResult2 = SellerPaymentsRequestVO.builder();
    builderResult2.payments(payments);
    SellerPaymentsRequestVO.SellerPaymentsRequestVOBuilder sellerPaymentsRequestVOBuilder = mock(
        SellerPaymentsRequestVO.SellerPaymentsRequestVOBuilder.class);
    when(
        sellerPaymentsRequestVOBuilder.payments(Mockito.<List<PaymentRequestsVO>>any())).thenReturn(
        builderResult2);
    SellerPaymentsRequestVO sellerPaymentsRequestVO = sellerPaymentsRequestVOBuilder.payments(
            new ArrayList<>())
        .sellerCode("Seller Code")
        .build();

    // Act
    SellerPaymentsResponseVO actualProcessResult = paymentServiceImpl.process(
        sellerPaymentsRequestVO);

    // Assert
    verify(paymentRepository).existsByBillingCode(eq("Billing Code"));
    verify(paymentRepository, atLeast(1)).findByBillingCode(eq("Billing Code"));
    verify(sellerRepository).existsByCode(eq("Seller Code"));
    verify(sQSService).produceTotalPayment(Mockito.<PaymentResponseVO>any());
    verify(sellerPaymentsRequestVOBuilder).payments(Mockito.<List<PaymentRequestsVO>>any());
    assertEquals("Seller Code", actualProcessResult.getSellerCode());
    assertEquals(1, actualProcessResult.getPayments().size());
  }

  /**
   * Method under test: {@link PaymentServiceImpl#process(SellerPaymentsRequestVO)}
   */
  @Test
  void testProcess4() {
    // Arrange
    when(sellerRepository.existsByCode(Mockito.<String>any())).thenReturn(true);

    Seller seller = new Seller();
    seller.setCode("Code");
    seller.setId(1L);
    seller.setName("Name");

    Payment payment = new Payment();
    payment.setAmount(new BigDecimal("2.3"));
    payment.setAmountPaid(new BigDecimal("2.3"));
    payment.setBillingCode("Billing Code");
    payment.setId(1L);
    payment.setSeller(seller);
    payment.setStatus(PaymentStatus.PENDING);
    Optional<Payment> ofResult = Optional.of(payment);
    when(paymentRepository.findByBillingCode(Mockito.<String>any())).thenReturn(ofResult);
    when(paymentRepository.existsByBillingCode(Mockito.<String>any())).thenReturn(true);
    doThrow(new SellerNotFoundException()).when(sQSService)
        .produceTotalPayment(Mockito.<PaymentResponseVO>any());

    ArrayList<PaymentRequestsVO> payments = new ArrayList<>();
    PaymentRequestsVO.PaymentRequestsVOBuilder builderResult = PaymentRequestsVO.builder();
    PaymentRequestsVO buildResult = builderResult.amountPaid(new BigDecimal("2.3"))
        .billingCode("Billing Code").build();
    payments.add(buildResult);
    SellerPaymentsRequestVO.SellerPaymentsRequestVOBuilder builderResult2 = SellerPaymentsRequestVO.builder();
    builderResult2.payments(payments);
    SellerPaymentsRequestVO.SellerPaymentsRequestVOBuilder sellerPaymentsRequestVOBuilder = mock(
        SellerPaymentsRequestVO.SellerPaymentsRequestVOBuilder.class);
    when(
        sellerPaymentsRequestVOBuilder.payments(Mockito.<List<PaymentRequestsVO>>any())).thenReturn(
        builderResult2);
    SellerPaymentsRequestVO sellerPaymentsRequestVO = sellerPaymentsRequestVOBuilder.payments(
            new ArrayList<>())
        .sellerCode("Seller Code")
        .build();

    // Act and Assert
    assertThrows(SellerNotFoundException.class,
        () -> paymentServiceImpl.process(sellerPaymentsRequestVO));
    verify(paymentRepository).existsByBillingCode(eq("Billing Code"));
    verify(paymentRepository, atLeast(1)).findByBillingCode(eq("Billing Code"));
    verify(sellerRepository).existsByCode(eq("Seller Code"));
    verify(sQSService).produceTotalPayment(Mockito.<PaymentResponseVO>any());
    verify(sellerPaymentsRequestVOBuilder).payments(Mockito.<List<PaymentRequestsVO>>any());
  }

  /**
   * Method under test: {@link PaymentServiceImpl#process(SellerPaymentsRequestVO)}
   */
  @Test
  void testProcess5() {
    // Arrange
    when(sellerRepository.existsByCode(Mockito.<String>any())).thenReturn(true);

    Seller seller = new Seller();
    seller.setCode("Code");
    seller.setId(1L);
    seller.setName("Name");
    Payment payment = mock(Payment.class);
    when(payment.getAmount()).thenReturn(new BigDecimal("4.5"));
    doNothing().when(payment).setAmount(Mockito.<BigDecimal>any());
    doNothing().when(payment).setAmountPaid(Mockito.<BigDecimal>any());
    doNothing().when(payment).setBillingCode(Mockito.<String>any());
    doNothing().when(payment).setId(Mockito.<Long>any());
    doNothing().when(payment).setSeller(Mockito.<Seller>any());
    doNothing().when(payment).setStatus(Mockito.<PaymentStatus>any());
    payment.setAmount(new BigDecimal("2.3"));
    payment.setAmountPaid(new BigDecimal("2.3"));
    payment.setBillingCode("Billing Code");
    payment.setId(1L);
    payment.setSeller(seller);
    payment.setStatus(PaymentStatus.PENDING);
    Optional<Payment> ofResult = Optional.of(payment);
    when(paymentRepository.findByBillingCode(Mockito.<String>any())).thenReturn(ofResult);
    when(paymentRepository.existsByBillingCode(Mockito.<String>any())).thenReturn(true);
    doNothing().when(sQSService).producePartialPayment(Mockito.<PaymentResponseVO>any());

    ArrayList<PaymentRequestsVO> payments = new ArrayList<>();
    PaymentRequestsVO.PaymentRequestsVOBuilder builderResult = PaymentRequestsVO.builder();
    PaymentRequestsVO buildResult = builderResult.amountPaid(new BigDecimal("2.3"))
        .billingCode("Billing Code").build();
    payments.add(buildResult);
    SellerPaymentsRequestVO.SellerPaymentsRequestVOBuilder builderResult2 = SellerPaymentsRequestVO.builder();
    builderResult2.payments(payments);
    SellerPaymentsRequestVO.SellerPaymentsRequestVOBuilder sellerPaymentsRequestVOBuilder = mock(
        SellerPaymentsRequestVO.SellerPaymentsRequestVOBuilder.class);
    when(
        sellerPaymentsRequestVOBuilder.payments(Mockito.<List<PaymentRequestsVO>>any())).thenReturn(
        builderResult2);
    SellerPaymentsRequestVO sellerPaymentsRequestVO = sellerPaymentsRequestVOBuilder.payments(
            new ArrayList<>())
        .sellerCode("Seller Code")
        .build();

    // Act
    SellerPaymentsResponseVO actualProcessResult = paymentServiceImpl.process(
        sellerPaymentsRequestVO);

    // Assert
    verify(payment, atLeast(1)).getAmount();
    verify(payment).setAmount(Mockito.<BigDecimal>any());
    verify(payment).setAmountPaid(Mockito.<BigDecimal>any());
    verify(payment).setBillingCode(eq("Billing Code"));
    verify(payment).setId(Mockito.<Long>any());
    verify(payment).setSeller(Mockito.<Seller>any());
    verify(payment).setStatus(eq(PaymentStatus.PENDING));
    verify(paymentRepository).existsByBillingCode(eq("Billing Code"));
    verify(paymentRepository, atLeast(1)).findByBillingCode(eq("Billing Code"));
    verify(sellerRepository).existsByCode(eq("Seller Code"));
    verify(sQSService).producePartialPayment(Mockito.<PaymentResponseVO>any());
    verify(sellerPaymentsRequestVOBuilder).payments(Mockito.<List<PaymentRequestsVO>>any());
    assertEquals("Seller Code", actualProcessResult.getSellerCode());
    assertEquals(1, actualProcessResult.getPayments().size());
  }

  /**
   * Method under test: {@link PaymentServiceImpl#process(SellerPaymentsRequestVO)}
   */
  @Test
  void testProcess6() {
    // Arrange
    when(sellerRepository.existsByCode(Mockito.<String>any())).thenReturn(true);

    Seller seller = new Seller();
    seller.setCode("Code");
    seller.setId(1L);
    seller.setName("Name");
    Payment payment = mock(Payment.class);
    when(payment.getAmount()).thenReturn(new BigDecimal("-2.3"));
    doNothing().when(payment).setAmount(Mockito.<BigDecimal>any());
    doNothing().when(payment).setAmountPaid(Mockito.<BigDecimal>any());
    doNothing().when(payment).setBillingCode(Mockito.<String>any());
    doNothing().when(payment).setId(Mockito.<Long>any());
    doNothing().when(payment).setSeller(Mockito.<Seller>any());
    doNothing().when(payment).setStatus(Mockito.<PaymentStatus>any());
    payment.setAmount(new BigDecimal("2.3"));
    payment.setAmountPaid(new BigDecimal("2.3"));
    payment.setBillingCode("Billing Code");
    payment.setId(1L);
    payment.setSeller(seller);
    payment.setStatus(PaymentStatus.PENDING);
    Optional<Payment> ofResult = Optional.of(payment);
    when(paymentRepository.findByBillingCode(Mockito.<String>any())).thenReturn(ofResult);
    when(paymentRepository.existsByBillingCode(Mockito.<String>any())).thenReturn(true);
    doNothing().when(sQSService).produceExcessPayment(Mockito.<PaymentResponseVO>any());

    ArrayList<PaymentRequestsVO> payments = new ArrayList<>();
    PaymentRequestsVO.PaymentRequestsVOBuilder builderResult = PaymentRequestsVO.builder();
    PaymentRequestsVO buildResult = builderResult.amountPaid(new BigDecimal("2.3"))
        .billingCode("Billing Code").build();
    payments.add(buildResult);
    SellerPaymentsRequestVO.SellerPaymentsRequestVOBuilder builderResult2 = SellerPaymentsRequestVO.builder();
    builderResult2.payments(payments);
    SellerPaymentsRequestVO.SellerPaymentsRequestVOBuilder sellerPaymentsRequestVOBuilder = mock(
        SellerPaymentsRequestVO.SellerPaymentsRequestVOBuilder.class);
    when(
        sellerPaymentsRequestVOBuilder.payments(Mockito.<List<PaymentRequestsVO>>any())).thenReturn(
        builderResult2);
    SellerPaymentsRequestVO sellerPaymentsRequestVO = sellerPaymentsRequestVOBuilder.payments(
            new ArrayList<>())
        .sellerCode("Seller Code")
        .build();

    // Act
    SellerPaymentsResponseVO actualProcessResult = paymentServiceImpl.process(
        sellerPaymentsRequestVO);

    // Assert
    verify(payment, atLeast(1)).getAmount();
    verify(payment).setAmount(Mockito.<BigDecimal>any());
    verify(payment).setAmountPaid(Mockito.<BigDecimal>any());
    verify(payment).setBillingCode(eq("Billing Code"));
    verify(payment).setId(Mockito.<Long>any());
    verify(payment).setSeller(Mockito.<Seller>any());
    verify(payment).setStatus(eq(PaymentStatus.PENDING));
    verify(paymentRepository).existsByBillingCode(eq("Billing Code"));
    verify(paymentRepository, atLeast(1)).findByBillingCode(eq("Billing Code"));
    verify(sellerRepository).existsByCode(eq("Seller Code"));
    verify(sQSService).produceExcessPayment(Mockito.<PaymentResponseVO>any());
    verify(sellerPaymentsRequestVOBuilder).payments(Mockito.<List<PaymentRequestsVO>>any());
    assertEquals("Seller Code", actualProcessResult.getSellerCode());
    assertEquals(1, actualProcessResult.getPayments().size());
  }

  /**
   * Method under test: {@link PaymentServiceImpl#process(SellerPaymentsRequestVO)}
   */
  @Test
  void testProcess7() {
    // Arrange
    when(sellerRepository.existsByCode(Mockito.<String>any())).thenReturn(true);
    when(paymentRepository.existsByBillingCode(Mockito.<String>any())).thenReturn(false);

    Seller seller = new Seller();
    seller.setCode("Code");
    seller.setId(1L);
    seller.setName("Name");
    Payment payment = mock(Payment.class);
    doNothing().when(payment).setAmount(Mockito.<BigDecimal>any());
    doNothing().when(payment).setAmountPaid(Mockito.<BigDecimal>any());
    doNothing().when(payment).setBillingCode(Mockito.<String>any());
    doNothing().when(payment).setId(Mockito.<Long>any());
    doNothing().when(payment).setSeller(Mockito.<Seller>any());
    doNothing().when(payment).setStatus(Mockito.<PaymentStatus>any());
    payment.setAmount(new BigDecimal("2.3"));
    payment.setAmountPaid(new BigDecimal("2.3"));
    payment.setBillingCode("Billing Code");
    payment.setId(1L);
    payment.setSeller(seller);
    payment.setStatus(PaymentStatus.PENDING);
    Optional.of(payment);

    ArrayList<PaymentRequestsVO> payments = new ArrayList<>();
    PaymentRequestsVO.PaymentRequestsVOBuilder builderResult = PaymentRequestsVO.builder();
    PaymentRequestsVO buildResult = builderResult.amountPaid(new BigDecimal("2.3"))
        .billingCode("Billing Code").build();
    payments.add(buildResult);
    SellerPaymentsRequestVO.SellerPaymentsRequestVOBuilder builderResult2 = SellerPaymentsRequestVO.builder();
    builderResult2.payments(payments);
    SellerPaymentsRequestVO.SellerPaymentsRequestVOBuilder sellerPaymentsRequestVOBuilder = mock(
        SellerPaymentsRequestVO.SellerPaymentsRequestVOBuilder.class);
    when(
        sellerPaymentsRequestVOBuilder.payments(Mockito.<List<PaymentRequestsVO>>any())).thenReturn(
        builderResult2);
    SellerPaymentsRequestVO sellerPaymentsRequestVO = sellerPaymentsRequestVOBuilder.payments(
            new ArrayList<>())
        .sellerCode("Seller Code")
        .build();

    // Act and Assert
    assertThrows(PaymentNotFoundException.class,
        () -> paymentServiceImpl.process(sellerPaymentsRequestVO));
    verify(payment).setAmount(Mockito.<BigDecimal>any());
    verify(payment).setAmountPaid(Mockito.<BigDecimal>any());
    verify(payment).setBillingCode(eq("Billing Code"));
    verify(payment).setId(Mockito.<Long>any());
    verify(payment).setSeller(Mockito.<Seller>any());
    verify(payment).setStatus(eq(PaymentStatus.PENDING));
    verify(paymentRepository).existsByBillingCode(eq("Billing Code"));
    verify(sellerRepository).existsByCode(eq("Seller Code"));
    verify(sellerPaymentsRequestVOBuilder).payments(Mockito.<List<PaymentRequestsVO>>any());
  }
}
