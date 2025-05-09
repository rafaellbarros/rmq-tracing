package com.github.rafaellbarros.worker.consumer;

import com.github.rafaellbarros.worker.producer.PaymentErrorProducer;
import com.github.rafaellbarros.worker.producer.PaymentSuccessProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestConsumer {

    private final PaymentErrorProducer paymentErrorProducer;
    private final PaymentSuccessProducer paymentSuccessProducer;

    @RabbitListener(queues = {"request"})
    public void receiveMessage(@Payload Message message) {
        log.info("Received message: {}", message);

        if (new Random().nextBoolean()) {
            paymentSuccessProducer.generateResponse("Payment success " + message);
        } else {
            paymentErrorProducer.generateResponse("Payment error "+ message);
        }
    }
}
