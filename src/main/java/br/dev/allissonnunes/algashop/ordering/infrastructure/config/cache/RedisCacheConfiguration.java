package br.dev.allissonnunes.algashop.ordering.infrastructure.config.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

}
