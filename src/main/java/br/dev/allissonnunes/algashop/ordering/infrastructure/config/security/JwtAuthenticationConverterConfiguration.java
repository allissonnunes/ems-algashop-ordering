package br.dev.allissonnunes.algashop.ordering.infrastructure.config.security;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.DelegatingJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;

@Configuration(proxyBeanMethods = false)
class JwtAuthenticationConverterConfiguration {

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        final var scopesConverter = createScopesConverter();
        final var rolesConverter = createRolesConverter();
        final var delegatingConverter = new DelegatingJwtGrantedAuthoritiesConverter(scopesConverter, rolesConverter);

        final JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(delegatingConverter);
        return jwtAuthenticationConverter;
    }

    private @NonNull Converter<Jwt, Collection<GrantedAuthority>> createScopesConverter() {
        final var scopesConverter = new JwtGrantedAuthoritiesConverter();
        scopesConverter.setAuthorityPrefix("SCOPE_"); // O default já é SCOPE_. Deixando explícito para fins didáticos
        scopesConverter.setAuthoritiesClaimName("scope"); // O default já é scope. Deixando explícito para fins didáticos
        return scopesConverter;
    }

    private @NonNull Converter<Jwt, Collection<GrantedAuthority>> createRolesConverter() {
        final var rolesConverter = new JwtGrantedAuthoritiesConverter();
        rolesConverter.setAuthorityPrefix("ROLE_");
        rolesConverter.setAuthoritiesClaimName("role");
        return rolesConverter;
    }

}
