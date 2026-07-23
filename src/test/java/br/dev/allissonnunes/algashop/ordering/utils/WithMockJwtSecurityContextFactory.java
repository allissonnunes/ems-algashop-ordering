package br.dev.allissonnunes.algashop.ordering.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WithMockJwtSecurityContextFactory implements WithSecurityContextFactory<WithMockJwt> {

    public static final String DEFAULT_ISSUER_URI = "http://algashop-authorization-server:8081";

    public static final String DEFAULT_SUBJECT = "test-user";

    public static final String[] ALL_SCOPES = new String[]{
            "orders:read",
            "orders:write",
            "customers:read",
            "customers:write",
            "shopping-carts:read",
            "shopping-carts:write",
            "shipping-costs:preview",
    };

    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    private JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

    @Override
    public SecurityContext createSecurityContext(final WithMockJwt withJwt) {
        SecurityContextHolder.clearContext();
        final Map<String, Object> headers = Map.of("alg", "none");
        final Map<String, Object> claims = new HashMap<>();
        claims.put("scope", List.of(withJwt.scopes()));
        if (withJwt.shouldGrantAllScopes()) {
            claims.put("scope", List.of(ALL_SCOPES));
        }
        final Instant issuedAt = Instant.now();
        final Jwt jwt = Jwt.withTokenValue("token")
                .issuer(DEFAULT_ISSUER_URI)
                .subject(withJwt.subject())
                .issuedAt(issuedAt)
                .notBefore(issuedAt)
                .expiresAt(issuedAt.plus(1L, ChronoUnit.MINUTES))
                .headers(h -> h.putAll(headers))
                .claims(c -> c.putAll(claims))
                .build();
        final Authentication authentication = this.jwtAuthenticationConverter.convert(jwt);
        final SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }

    @Autowired(required = false)
    void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }

    @Autowired(required = false)
    void setJwtAuthenticationConverter(final JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

}
