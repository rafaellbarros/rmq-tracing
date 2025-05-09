package com.github.rafaellbarros.backend.consumer;

import com.github.rafaellbarros.backend.facade.PaymentFacede;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentReponseSuccessConsumer {

    private final PaymentFacede paymentFacede;

    @RabbitListener(queues = {"${spring.rabbitmq.template.response-success-queue}"})
    public void receive(@Payload Message message) {
        log.info("Received message: {}, Time: {}", message, LocalDateTime.now());
        var payload = message.getPayload().toString();
        paymentFacede.successPayment(payload);
    }
}
