package com.github.rafaellbarros.worker.config;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.micrometer.tracing.propagation.Propagator;


@Configuration
public class RabbitConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Propagator propagator) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        factory.setAfterReceivePostProcessors(message -> {
            if (propagator != null) {
                propagator.extract(message.getMessageProperties(), this::getHeaderValue);
            }
            return message;
        });

        return factory;
    }

    private String getHeaderValue(MessageProperties carrier, String key) {
        Object value = carrier.getHeaders().get(key);
        return value != null ? value.toString() : null;
    }
}