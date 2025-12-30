package com.github.allisson95.algashop.ordering.application.checkout;

import com.github.allisson95.algashop.ordering.application.utility.Mapper;
import com.github.allisson95.algashop.ordering.domain.model.commons.Quantity;
import com.github.allisson95.algashop.ordering.domain.model.commons.ZipCode;
import com.github.allisson95.algashop.ordering.domain.model.customer.Customer;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerId;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.github.allisson95.algashop.ordering.domain.model.customer.Customers;
import com.github.allisson95.algashop.ordering.domain.model.order.*;
import com.github.allisson95.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.github.allisson95.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.github.allisson95.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationResponse;
import com.github.allisson95.algashop.ordering.domain.model.product.Product;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductCatalogService;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductId;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class BuyNowApplicationService {

    private final BuyNowService buyNowService;

    private final ProductCatalogService productCatalogService;

    private final ShippingCostService shippingCostService;

    private final OriginAddressService originAddressService;

    private final Orders orders;

    private final Mapper mapper;

    private final ShippingInputDisassembler shippingInputDisassembler;

    private final Customers customers;

    @Transactional
    public String buyNow(final BuyNowInput input) {
        requireNonNull(input, "input cannot be null");

        final PaymentMethod paymentMethod = PaymentMethod.valueOf(input.paymentMethod());
        final CustomerId customerId = new CustomerId(input.customerId());
        final Quantity quantity = new Quantity(input.quantity());

        final Customer customer = findCustomer(customerId);
        final Product product = findProduct(new ProductId(input.productId()));

        final Shipping shipping = shippingInputDisassembler.toDomainModel(input.shipping(), calculateShippingCost(input.shipping()));
        final Billing billing = mapper.convert(input.billing(), Billing.class);

        Order order = buyNowService.buyNow(
                product, customer, billing, shipping, quantity, paymentMethod
        );

        orders.add(order);

        return order.getId().toString();
    }

    private Customer findCustomer(final CustomerId customerId) {
        return customers.ofId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    private Product findProduct(final ProductId productId) {
        return productCatalogService.ofId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private CalculationResponse calculateShippingCost(final ShippingInput shipping) {
        final ZipCode origin = originAddressService.originAddress().zipCode();
        final ZipCode destination = new ZipCode(shipping.address().zipCode());
        return shippingCostService.calculate(new ShippingCostService.CalculationRequest(origin, destination));
    }

}
