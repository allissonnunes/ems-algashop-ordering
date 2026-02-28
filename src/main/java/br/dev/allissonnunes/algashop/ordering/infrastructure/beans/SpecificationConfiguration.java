package br.dev.allissonnunes.algashop.ordering.infrastructure.beans;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.customer.LoyaltyPoints;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.CustomerHaveFreeShippingSpecification;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.Orders;
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
