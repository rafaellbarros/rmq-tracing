package com.github.rafaellbarros.backend.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.rafaellbarros.backend.dto.PaymentDTO;
import com.github.rafaellbarros.backend.producer.PaymentRequestProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentFacede {

    private final PaymentRequestProducer paymentRequestProducer;

    public String requestPayment(PaymentDTO paymentDTO) {

        try {
            String integrated = paymentRequestProducer.integrate(paymentDTO);
            log.info("Processing payment for order: [{}]", integrated);
            return "Payment awaiting confirmation: " + integrated;
        } catch (JsonProcessingException e) {
            return "Error processing payment: " + e.getMessage();
        }

    }

    public void errorPayment(String payload) {
        log.error("=== Response Error === [{}]", payload);
    }

    public void successPayment(String payload) {
        log.info("=== Response Success === [{}]", payload);
    }
}
