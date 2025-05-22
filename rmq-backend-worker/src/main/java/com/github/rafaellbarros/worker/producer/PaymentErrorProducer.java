package com.github.rafaellbarros.worker.producer;

import com.github.rafaellbarros.worker.exception.ResponseSendingException;
import io.micrometer.common.lang.Nullable;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentErrorProducer {

    private final RabbitTemplate rabbitTemplate;
    private final Propagator propagator;
    private final Tracer tracer;

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.template.error-routing-key}")
    private String routingKey;

    public void generateResponse(String errorMessage, @Nullable String originalTraceId) {
        try {
            Message message = MessageBuilder
                    .withBody(errorMessage.getBytes(StandardCharsets.UTF_8))
                    .setHeader("X-Trace-Id", originalTraceId != null ? originalTraceId : getCurrentTraceId())
                    .build();

            // Injeta o contexto de tracing
            if (tracer.currentSpan() != null) {
                propagator.inject(
                        tracer.currentSpan().context(),
                        message.getMessageProperties(),
                        (carrier, key, value) -> carrier.getHeaders().put(key, value)
                );
            }

            rabbitTemplate.convertAndSend(exchange, routingKey, message);
            log.error("Payment error response sent [OriginalTraceId: {}] - Message: {}",
                    originalTraceId,
                    truncate(errorMessage));
        } catch (Exception e) {
            log.error("Failed to send error response [TraceId: {}]", getCurrentTraceId(), e);
            throw new ResponseSendingException("Failed to send success response", e);
        }
    }

    private String getCurrentTraceId() {
        return tracer.currentSpan() != null ?
                tracer.currentSpan().context().traceId() :
                "no-trace-id";
    }

    private String truncate(String message) {
        return message.length() > 100 ? message.substring(0, 100) + "..." : message;
    }
}
