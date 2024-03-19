package com.vnoxiaene.paymentprocessor.shared.controller;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnoxiaene.paymentprocessor.validator.service.PaymentService;
import com.vnoxiaene.paymentprocessor.validator.vo.SellerPaymentsRequestVO;
import com.vnoxiaene.paymentprocessor.validator.vo.SellerPaymentsResponseVO;
import java.util.ArrayList;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {PaymentController.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class PaymentControllerTest {

  @Autowired
  private PaymentController paymentController;

  @MockBean
  private PaymentService paymentService;

  /**
   * Method under test: {@link PaymentController#validatePayments(SellerPaymentsRequestVO)}
   */
  @Test
  void testValidatePayments() throws Exception {
    // Arrange
    SellerPaymentsResponseVO.SellerPaymentsResponseVOBuilder builderResult = SellerPaymentsResponseVO.builder();
    SellerPaymentsResponseVO buildResult = builderResult.payments(new HashSet<>())
        .sellerCode("Seller Code").build();
    when(paymentService.process(Mockito.<SellerPaymentsRequestVO>any())).thenReturn(buildResult);
    MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/v1/payments")
        .contentType(MediaType.APPLICATION_JSON);

    ObjectMapper objectMapper = new ObjectMapper();
    SellerPaymentsRequestVO.SellerPaymentsRequestVOBuilder builderResult2 = SellerPaymentsRequestVO.builder();
    SellerPaymentsRequestVO buildResult2 = builderResult2.payments(new ArrayList<>())
        .sellerCode("Seller Code").build();
    MockHttpServletRequestBuilder requestBuilder = contentTypeResult
        .content(objectMapper.writeValueAsString(buildResult2));

    // Act and Assert
    MockMvcBuilders.standaloneSetup(paymentController)
        .build()
        .perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.content()
            .string("{\"sellerCode\":\"Seller Code\",\"payments\":[]}"));
  }
}
