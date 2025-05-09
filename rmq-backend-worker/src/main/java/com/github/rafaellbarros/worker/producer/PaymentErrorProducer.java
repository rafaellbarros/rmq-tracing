package com.github.rafaellbarros.worker.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentErrorProducer {

    private final AmqpTemplate amqpTemplate;

    @Value("${spring.rabbitmq.template.exchange-response}")
    private String exchange;

    @Value("${spring.rabbitmq.template.error-routing-key}")
    private String routingKey;

    public void generateResponse(String message)  {
        amqpTemplate.convertAndSend(this.exchange, this.routingKey, message);
        log.error("Message error reponse sent to RabbitMQ: {}", message);
    }
}
