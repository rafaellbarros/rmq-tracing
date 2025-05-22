package com.github.rafaellbarros.tracing.core.config;

import brave.Tracing;
import brave.handler.SpanHandler;
import brave.propagation.B3Propagation;
import brave.sampler.Sampler;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.brave.bridge.BravePropagator;
import io.micrometer.tracing.propagation.Propagator;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.urlconnection.URLConnectionSender;


@Configuration
public class TracingCoreConfig {


    @Value("${management.tracing.zipkin.endpoint}")
    private String zipkinEndpoint;



    @Bean
    Sender zipkinSender() {
        return URLConnectionSender.create(zipkinEndpoint);
    }


    @Bean
    AsyncReporter<zipkin2.Span> zipkinReporter(Sender sender) {
        return AsyncReporter.create(sender);
    }

    @Bean
    Propagator propagator(Tracing tracing) {
        return new BravePropagator(tracing);
    }


    @Bean
    Tracing braveTracing(SpanHandler reporter) {
        return Tracing.newBuilder()
                .localServiceName("rmq-tracing-core")
                .addSpanHandler(reporter)
                .traceId128Bit(true)
                .sampler(Sampler.ALWAYS_SAMPLE)
                .propagationFactory(B3Propagation.FACTORY)
                .build();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Propagator propagator, Tracer tracer) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setBeforePublishPostProcessors(message -> {
            if (tracer.currentSpan() != null) {
                propagator.inject(
                        tracer.currentSpan().context(),
                        message.getMessageProperties(),
                        (carrier, key, value) -> carrier.getHeaders().put(key, value)
                );
            }
            return message;
        });
        return rabbitTemplate;
    }
}