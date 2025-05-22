package com.github.rafaellbarros.backend.producer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rafaellbarros.backend.dto.PaymentDTO;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final Tracer tracer;
    private final Propagator propagator;

    @Value("${spring.rabbitmq.template.exchange-request}")
    private String exchange;

    @Value("${spring.rabbitmq.template.request-routing-key}")
    private String routingKey;

    public String integrate(PaymentDTO paymentDTO) throws JsonProcessingException {
        Message message = MessageBuilder
                .withBody(objectMapper.writeValueAsBytes(paymentDTO))
                .setHeader("X-Trace-Id", tracer.currentSpan().context().traceId()) // Injeção EXPLÍCITA
                .build();

        // Garante que o propagador insira todos os headers de tracing
        propagator.inject(
                tracer.currentSpan().context(),
                message.getMessageProperties(),
                (carrier, key, value) -> carrier.getHeaders().put(key, value)
        );

        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        log.info("Message sent with originalTraceId: {}", tracer.currentSpan().context().traceId());
        return "Payment processed";
    }
}
