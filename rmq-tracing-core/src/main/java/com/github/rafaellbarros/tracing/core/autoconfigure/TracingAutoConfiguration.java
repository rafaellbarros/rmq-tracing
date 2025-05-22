package com.github.rafaellbarros.tracing.core.autoconfigure;

import com.github.rafaellbarros.tracing.core.config.TracingProperties;
import com.github.rafaellbarros.tracing.core.filter.TracingFilter;
import com.github.rafaellbarros.tracing.core.util.TracingUtil;
import io.micrometer.tracing.Tracer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(TracingProperties.class)
@ConditionalOnProperty(prefix = "rmq.tracing", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TracingAutoConfiguration implements WebMvcConfigurer {

    @Bean
    @ConditionalOnClass(name = "jakarta.servlet.http.HttpServletRequest")
    public TracingFilter tracingFilter(TracingUtil tracingUtil) {
        return new TracingFilter(tracingUtil);
    }

    @Bean
    public TracingUtil tracingUtil(Tracer tracer) {
        return new TracingUtil(tracer);
    }
}