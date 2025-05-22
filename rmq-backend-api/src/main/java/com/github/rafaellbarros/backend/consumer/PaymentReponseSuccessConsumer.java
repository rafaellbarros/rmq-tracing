package com.github.rafaellbarros.backend.consumer;

import com.github.rafaellbarros.backend.facade.PaymentFacede;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentReponseSuccessConsumer {

    private final PaymentFacede paymentFacede;
    private final Tracer tracer;

    @RabbitListener(queues = "${spring.rabbitmq.template.response-success-queue}")
    public void receive(@Payload Message message) {
        String traceId = message.getMessageProperties().getHeader("X-Trace-Id");

        Span span = tracer.nextSpan()
                .name("payment.response.success")
                .tag("original.trace_id", traceId)
                .start();

        try (Tracer.SpanInScope scope = tracer.withSpan(span)) {
            MDC.put("traceId", traceId != null ? traceId : span.context().traceId());

            String payload = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("Received SUCCESS response for original trace: {}", traceId);
            paymentFacede.successPayment(payload);
        } finally {
            span.end();
            MDC.remove("traceId");
        }
    }
}