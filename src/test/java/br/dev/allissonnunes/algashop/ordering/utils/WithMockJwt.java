package br.dev.allissonnunes.algashop.ordering.utils;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.*;

import static br.dev.allissonnunes.algashop.ordering.utils.WithMockJwtSecurityContextFactory.DEFAULT_SUBJECT;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithMockJwtSecurityContextFactory.class)
public @interface WithMockJwt {

    /**
     * Specifies the subject (principal) to be used in the security context.
     * The subject typically represents the identity of the user or entity
     * for which authentication is being simulated.
     *
     * @return the subject string to be used in the authentication context.
     * The default is "test-user".
     */
    String subject() default DEFAULT_SUBJECT;

    /**
     * Specifies a collection of scopes to be used in the security context.
     * <p>
     * The provided scopes are typically used to represent the permissions or access rights
     * granted to a user or client in the context of OAuth 2.0 or similar authentication mechanisms.
     *
     * @return an array of scope strings to be applied to the authentication context.
     */
    String[] scopes() default {};

    /**
     * Determines whether all available scopes should be granted in the security context.
     * <p>
     * When set to {@code true}, all predefined scopes will be granted.
     * Otherwise, only the scopes explicitly specified via the {@code scopes} property will be granted.
     *
     * @return {@code true} if all scopes should be granted, otherwise {@code false}.
     * The default value is {@code false}.
     */
    boolean shouldGrantAllScopes() default false;

}
