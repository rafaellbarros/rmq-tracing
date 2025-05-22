package com.github.rafaellbarros.tracing.core.util;

import io.micrometer.tracing.Baggage;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TracingUtil {
    private final Tracer tracer;

    public String getCurrentTraceId() {
        return tracer.currentSpan() != null ?
                tracer.currentSpan().context().traceId() :
                null;
    }

    public String getCurrentSpanId() {
        return tracer.currentSpan() != null ?
                tracer.currentSpan().context().spanId() :
                null;
    }

    public void setBaggage(String key, String value) {
        if (tracer.currentSpan() != null) {
            tracer.createBaggage(key).set(value);
        }
    }

    public String getBaggage(String key) {
        if (tracer.currentSpan() != null) {
            Baggage baggage = tracer.getBaggage(key);
            return baggage != null ? baggage.get() : null;
        }
        return null;
    }
}