package com.github.allisson95.algashop.ordering.infrastructure.beans;

import com.github.allisson95.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.github.allisson95.algashop.ordering.domain.model.order.CustomerHaveFreeShippingSpecification;
import com.github.allisson95.algashop.ordering.domain.model.order.Orders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SpecificationConfiguration {

    @Bean
    CustomerHaveFreeShippingSpecification customerHaveFreeShippingSpecification(final Orders orders) {
        return new CustomerHaveFreeShippingSpecification(
                orders,
                2L,
                new LoyaltyPoints(100),
                new LoyaltyPoints(2000)
        );
    }

}
