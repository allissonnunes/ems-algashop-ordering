package br.dev.allissonnunes.algashop.ordering.infrastructure.config.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

public interface SecurityAnnotations {

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @PreAuthorize("hasAuthority('SCOPE_orders:read')")
    @interface CanReadOrders {

    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @PreAuthorize("hasAuthority('SCOPE_orders:write')")
    @interface CanWriteOrders {

    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @PreAuthorize("hasAuthority('SCOPE_customers:read')")
    @interface CanReadCustomers {

    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @PreAuthorize("hasAuthority('SCOPE_customers:write')")
    @interface CanWriteCustomers {

    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @PreAuthorize("hasAuthority('SCOPE_shopping-carts:read')")
    @interface CanReadShoppingCarts {

    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @PreAuthorize("hasAuthority('SCOPE_shopping-carts:write')")
    @interface CanWriteShoppingCarts {

    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @PreAuthorize("hasAuthority('SCOPE_shipping-costs:preview')")
    @interface CanPreviewShippingCosts {

    }

}
