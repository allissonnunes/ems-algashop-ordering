package br.dev.allissonnunes.algashop.ordering.infrastructure.config.resilience;

import br.dev.allissonnunes.algashop.ordering.infrastructure.config.errorhandling.BadGatewayException;
import br.dev.allissonnunes.algashop.ordering.infrastructure.config.errorhandling.GatewayTimeoutException;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;

import java.time.Duration;

//import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
//import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;

@Configuration
public class SpringCircuitBreakerConfiguration {

    public static final String PRODUCT_CATALOG_CB_ID = "productCatalogCB";

    public static final String RAPIDEX_API_CB_ID = "rapidexApiCB";

    @Bean
    Customizer<FrameworkRetryCircuitBreakerFactory> defaultCustomizer() {
        final RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(3L)
                .multiplier(2.0)
                .delay(Duration.ofSeconds(3L))
                .includes(GatewayTimeoutException.class, BadGatewayException.ServerErrorException.class)
                .build();
        return factory -> {
            factory.configureDefault(
                    id -> new FrameworkRetryConfigBuilder(id)
                            .retryPolicy(retryPolicy)
                            .openTimeout(Duration.ofSeconds(10L))
                            .resetTimeout(Duration.ofSeconds(25L))
                            .build()
            );

            factory.configure(
                    builder -> builder
                            .retryPolicy(retryPolicy)
                            .openTimeout(Duration.ofSeconds(10L))
                            .resetTimeout(Duration.ofSeconds(25L))
                            .build(),
                    PRODUCT_CATALOG_CB_ID
            );

            factory.configure(
                    builder -> builder
                            .retryPolicy(retryPolicy)
                            .openTimeout(Duration.ofSeconds(10L))
                            .resetTimeout(Duration.ofSeconds(25L))
                            .build(),
                    RAPIDEX_API_CB_ID
            );
        };
    }

//    @Bean
//    Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
//        return factory -> {
//            factory.configure(builder ->
//                            builder.circuitBreakerConfig(
//                                    CircuitBreakerConfig.custom()
//                                            .permittedNumberOfCallsInHalfOpenState(3)
//                                            .minimumNumberOfCalls(5)
//                                            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
//                                            .slidingWindowSize(5)
//                                            .ignoreExceptions(BadGatewayException.ClientErrorException.class)
//                                            .build()
//                            ),
//                    "productCatalogCB"
//            );
//        };
//    }

}
