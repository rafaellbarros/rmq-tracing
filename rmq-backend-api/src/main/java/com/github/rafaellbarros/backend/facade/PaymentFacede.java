package com.github.rafaellbarros.backend.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.rafaellbarros.backend.dto.PaymentDTO;
import com.github.rafaellbarros.backend.exception.PaymentProcessingException;
import com.github.rafaellbarros.backend.producer.PaymentRequestProducer;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentFacede {

    private final PaymentRequestProducer paymentRequestProducer;
    private final Tracer tracer;

    public String requestPayment(PaymentDTO paymentDTO) {
        Span span = tracer.nextSpan().name("payment.request").start();
        try (Tracer.SpanInScope scope = tracer.withSpan(span)) {
            String traceId = span.context().traceId();
            String integrated = paymentRequestProducer.integrate(paymentDTO);
            log.info("Processing payment [TraceId: {}]: {}", traceId, integrated);
            return integrated;
        } catch (JsonProcessingException e) {
            log.error("Payment processing failed [TraceId: {}]: {}",
                    span.context().traceId(),
                    e.getMessage());
            throw new PaymentProcessingException("Error processing payment", e);
        } finally {
            span.end();
        }
    }

    public void errorPayment(String payload) {
        log.error("=== Payment Error Response === [{}]", payload);
    }

    public void successPayment(String payload) {
        log.info("=== Payment Success Response === [{}]", payload);
    }
}
