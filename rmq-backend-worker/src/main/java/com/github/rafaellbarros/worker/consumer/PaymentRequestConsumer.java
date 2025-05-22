package com.github.rafaellbarros.worker.consumer;

import com.github.rafaellbarros.worker.producer.PaymentErrorProducer;
import com.github.rafaellbarros.worker.producer.PaymentSuccessProducer;
import io.micrometer.tracing.Span;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestConsumer {

    private final PaymentErrorProducer paymentErrorProducer;
    private final PaymentSuccessProducer paymentSuccessProducer;
    private final Tracer tracer;

    @RabbitListener(queues = "${spring.rabbitmq.template.request-queue}")
    public void receiveMessage(Message message) {

        String originalTraceId = message.getMessageProperties().getHeader("X-Trace-Id");

        Span span = tracer.nextSpan()
                .name("payment.process")
                .tag("original.trace_id", originalTraceId)
                .start();

        try (Tracer.SpanInScope scope = tracer.withSpan(span)) {
            MDC.put("traceId", originalTraceId != null ? originalTraceId : span.context().traceId());

            String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("Processing payment [OriginalTraceId: {}]", originalTraceId);
            processPayment(messageBody, originalTraceId);

        } finally {
            span.end();
            MDC.remove("traceId");
        }
    }

    @NewSpan("payment.process.logic")
    private void processPayment(String messageContent, String originalTraceId) {
        if (new Random().nextBoolean()) {
            paymentSuccessProducer.generateResponse("Payment success: " + messageContent, originalTraceId);
        } else {
            paymentErrorProducer.generateResponse("Payment error: " + messageContent, originalTraceId);
        }
    }
}
