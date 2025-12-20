package com.github.allisson95.algashop.ordering.infrastructure.persistence;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware", dateTimeProviderRef = "dateTimeProvider")
@EnableJpaRepositories(
        basePackages = { "com.github.allisson95.algashop.ordering.infrastructure.persistence" }
)
public class SpringDataJpaConfiguration {

    @Bean
    AuditorAware<@NonNull UUID> auditorAware() {
        return () -> Optional.of(UUID.randomUUID());
    }

    @Bean
    DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(Instant.now());
    }

}
