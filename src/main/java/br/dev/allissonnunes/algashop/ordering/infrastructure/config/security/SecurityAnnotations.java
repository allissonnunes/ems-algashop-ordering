package br.dev.allissonnunes.algashop.ordering.infrastructure.config.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

public interface SecurityAnnotations {

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @PreAuthorize("@oauth2.hasScope('orders:read')")
    @interface CanReadOrders {

    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @PreAuthorize("@oauth2.hasScope('orders:write')")
    @interface CanWriteOrders {

    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @PreAuthorize("@oauth2.hasScope('customers:read')")
    @interface CanReadCustomers {

    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @PreAuthorize("@oauth2.hasScope('customers:write')")
    @interface CanWriteCustomers {

    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @PreAuthorize("@oauth2.hasScope('shopping-carts:read')")
    @interface CanReadShoppingCarts {

    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @PreAuthorize("@oauth2.hasScope('shopping-carts:write')")
    @interface CanWriteShoppingCarts {

    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @PreAuthorize("@oauth2.hasScope('shipping-costs:preview')")
    @interface CanPreviewShippingCosts {

    }

}
