package com.github.rafaellbarros.backend.producer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rafaellbarros.backend.dto.PaymentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRequestProducer {

    private final AmqpTemplate amqpTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.rabbitmq.template.exchange-request}")
    private String exchange;

    @Value("${spring.rabbitmq.template.request-routing-key}")
    private String routingKey;


    public String integrate(PaymentDTO paymentDTO) throws JsonProcessingException {
        var message = objectMapper.writeValueAsString(paymentDTO);
        amqpTemplate.convertAndSend(this.exchange, this.routingKey, message);
        return message;
    }
}