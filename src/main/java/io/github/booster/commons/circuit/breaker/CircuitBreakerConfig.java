package io.github.booster.commons.circuit.breaker;

import arrow.core.Option;
import io.github.booster.commons.metrics.MetricsRegistry;
import io.github.booster.commons.pool.NamedObjectPool;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Circuit breaker config objects that can be created as a Spring bean.
 * This object also caches circuit breakers created and returns cached value
 * if name is the same to avoid creating duplicate circuit breakers.
 */
public class CircuitBreakerConfig extends NamedObjectPool<Option<CircuitBreaker>> {

    private static Logger LOG = LoggerFactory.getLogger(CircuitBreakerConfig.class);

    private Map<String, CircuitBreakerSetting> circuitBreakerSettings;

    private MetricsRegistry registry;

    /**
     * Default constructor
     */
    public CircuitBreakerConfig() {
        this(null);
    }

    /**
     * Constructor with default settings.
     * @param settings {@link CircuitBreakerSetting} identified by name.
     */
    public CircuitBreakerConfig(Map<String, CircuitBreakerSetting> settings) {
        this.setCircuitBreakerSettings(settings);
    }

    /**
     * Setter method for use as Spring configuration properties.
     * @param circuitBreakerSettings map of {@link CircuitBreakerSetting}, key is the name for each setting.
     */
    public void setCircuitBreakerSettings(Map<String, CircuitBreakerSetting> circuitBreakerSettings) {
        this.circuitBreakerSettings = circuitBreakerSettings == null ? new HashMap<>() : circuitBreakerSettings;
    }

    public void setMetricsRegistry(MetricsRegistry registry) {
        this.registry = registry == null ?
                new MetricsRegistry() :
                registry;
    }

    @Override
    protected Option<CircuitBreaker> createObject(String name) {
        return this.circuitBreakerSettings.containsKey(name) ?
                this.circuitBreakerSettings.get(name).buildCircuitBreaker(name, this.registry) :
                Option.fromNullable(null);
    }

    @Override
    public Option<Option<CircuitBreaker>> getOption(String name) {
        throw new UnsupportedOperationException("operation not supported");
    }
}
