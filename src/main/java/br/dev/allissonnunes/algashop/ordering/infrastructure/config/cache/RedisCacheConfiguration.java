package br.dev.allissonnunes.algashop.ordering.infrastructure.config.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@EnableCaching
@ConditionalOnProperty(prefix = "spring.cache", name = "type", havingValue = "redis")
@Configuration
class RedisCacheConfiguration {

    @Bean
    RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        final var classLoader = Thread.currentThread().getContextClassLoader();
        final var defaultCacheConfig = org.springframework.data.redis.cache.RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(1L))
                .computePrefixWith(cacheName -> cacheName + ":")
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new JdkSerializationRedisSerializer(classLoader)
                ));

        return (builder) -> builder
                .cacheDefaults(defaultCacheConfig)
                .withCacheConfiguration(
                        "algashop:product-catalog-api:v1",
                        defaultCacheConfig.disableCachingNullValues()
                                .entryTtl(Duration.ofMinutes(5L))
                );
    }

    @Bean(name = "cache")
    HealthIndicator cacheHealthIndicator(final RedisConnectionFactory redisConnectionFactory) {
        return () -> {
            RedisConnection connection = null;
            try {
                connection = RedisConnectionUtils.getConnection(redisConnectionFactory);
                connection.ping();
                return Health.up().build();
            } catch (final Exception e) {
                return Health
                        .status("DEGRADED")
                        .withDetail("error", e.getMessage())
                        .withException(e)
                        .build();
            } finally {
                RedisConnectionUtils.releaseConnection(connection, redisConnectionFactory);
            }
        };
    }

}
