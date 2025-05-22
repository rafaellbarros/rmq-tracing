package com.github.rafaellbarros.tracing.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "rmq.tracing")
public class TracingProperties {
    private boolean enabled = true;
    private String serviceName = "rmq-service";
    private String spanNamePattern = "rmq.%s";
}