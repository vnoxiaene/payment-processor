package com.vnoxiaene.paymentprocessor.validator.service;

import com.vnoxiaene.paymentprocessor.shared.entity.PaymentStatus;
import com.vnoxiaene.paymentprocessor.shared.exception.PaymentNotFoundException;
import com.vnoxiaene.paymentprocessor.shared.exception.SellerNotFoundException;
import com.vnoxiaene.paymentprocessor.shared.repository.PaymentRepository;
import com.vnoxiaene.paymentprocessor.shared.repository.SellerRepository;
import com.vnoxiaene.paymentprocessor.validator.vo.PaymentRequestsVO;
import com.vnoxiaene.paymentprocessor.validator.vo.PaymentResponseVO;
import com.vnoxiaene.paymentprocessor.validator.vo.SellerPaymentsRequestVO;
import com.vnoxiaene.paymentprocessor.validator.vo.SellerPaymentsResponseVO;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

  private final SellerRepository sellerRepository;
  private final PaymentRepository paymentRepository;
  private final SQSService sqsService;

  @Override
  public SellerPaymentsResponseVO process(SellerPaymentsRequestVO sellerPaymentsRequestVO) {

    //ver no banco se o vendedor existe
    String sellerCode = sellerPaymentsRequestVO.getSellerCode();
    if (sellerNotFound(sellerCode)) {
      log.warn("Vendedor código {} não encontrado na base de dados", sellerCode);
      throw new SellerNotFoundException();
    }
    // validar codigos dos pagamentos
    Set<String> paymentCodes = sellerPaymentsRequestVO.getPayments().stream()
        .map(PaymentRequestsVO::getBillingCode).collect(Collectors.toSet());
    for (String paymentCode : paymentCodes) {
      if (paymentNotFound(paymentCode)) {
        log.warn("Payment não existe com o código {}", paymentCode);
        throw new PaymentNotFoundException();
      }
    }
    // construir response e enviar
    Set<PaymentResponseVO> paymentResponseVOS = new HashSet<>();
    sellerPaymentsRequestVO.getPayments().forEach(s -> {
      if (amountExcess(s)) {
        processExcessPayment(s, paymentResponseVOS);
      }
      if (amountFull(s)) {
        processFullPayment(s, paymentResponseVOS);
      } else {
        processPartialPayment(s, paymentResponseVOS);
      }
    });
    return SellerPaymentsResponseVO.builder().payments(paymentResponseVOS).sellerCode(sellerCode)
        .build();
  }

  private void processPartialPayment(PaymentRequestsVO s,
      Set<PaymentResponseVO> paymentResponseVOS) {
    PaymentResponseVO paymentResponseVO = PaymentResponseVO
        .builder()
        .status(PaymentStatus.PARTIAL)
        .billingCode(s.getBillingCode())
        .amountPaid(s.getAmountPaid())
        .build();
    sqsService.producePartialPayment(paymentResponseVO);
    paymentResponseVOS.add(paymentResponseVO);
  }

  private void processFullPayment(PaymentRequestsVO s, Set<PaymentResponseVO> paymentResponseVOS) {
    PaymentResponseVO paymentResponseVO = PaymentResponseVO
        .builder()
        .status(PaymentStatus.FULL)
        .billingCode(s.getBillingCode())
        .amountPaid(s.getAmountPaid())
        .build();
    sqsService.produceTotalPayment(paymentResponseVO);
    paymentResponseVOS.add(paymentResponseVO);
  }

  private void processExcessPayment(PaymentRequestsVO s,
      Set<PaymentResponseVO> paymentResponseVOS) {
    PaymentResponseVO paymentResponseVO = PaymentResponseVO
        .builder()
        .status(PaymentStatus.EXCESS)
        .billingCode(s.getBillingCode())
        .amountPaid(s.getAmountPaid())
        .build();
    sqsService.produceExcessPayment(paymentResponseVO);
    paymentResponseVOS.add(paymentResponseVO);
  }

  private boolean amountFull(PaymentRequestsVO s) {
    return s.getAmountPaid()
        .compareTo(paymentRepository.findByBillingCode(s.getBillingCode()).get().getAmount()) == 0;
  }

  private boolean amountExcess(PaymentRequestsVO s) {
    return s.getAmountPaid()
        .compareTo(paymentRepository.findByBillingCode(s.getBillingCode()).get().getAmount()) > 0;
  }

  private boolean paymentNotFound(String paymentCode) {
    return !paymentRepository.existsByBillingCode(paymentCode);
  }

  private boolean sellerNotFound(String sellerCode) {
    return !sellerRepository.existsByCode(sellerCode);
  }
}
