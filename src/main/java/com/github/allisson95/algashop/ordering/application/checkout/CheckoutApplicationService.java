package com.github.allisson95.algashop.ordering.application.checkout;

import com.github.allisson95.algashop.ordering.application.utility.Mapper;
import com.github.allisson95.algashop.ordering.domain.model.commons.ZipCode;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.github.allisson95.algashop.ordering.domain.model.customer.Customers;
import com.github.allisson95.algashop.ordering.domain.model.order.Billing;
import com.github.allisson95.algashop.ordering.domain.model.order.CheckoutService;
import com.github.allisson95.algashop.ordering.domain.model.order.Orders;
import com.github.allisson95.algashop.ordering.domain.model.order.PaymentMethod;
import com.github.allisson95.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.github.allisson95.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.github.allisson95.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationResponse;
import com.github.allisson95.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.github.allisson95.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.github.allisson95.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class CheckoutApplicationService {

    private final CheckoutService checkoutService;

    private final ShoppingCarts shoppingCarts;

    private final Orders orders;

    private final OriginAddressService originAddressService;

    private final ShippingCostService shippingCostService;

    private final Mapper mapper;

    private final ShippingInputDisassembler shippingInputDisassembler;

    private final Customers customers;

    @Transactional
    public @NonNull String checkout(final CheckoutInput input) {
        requireNonNull(input, "input cannot be null");

        final var shoppingCartId = new ShoppingCartId(input.shoppingCartId());
        final var shoppingCart = shoppingCarts.ofId(shoppingCartId)
                .orElseThrow(() -> new ShoppingCartNotFoundException(shoppingCartId));

        final var customerId = shoppingCart.getCustomerId();
        final var customer = customers.ofId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        final var shippingCostDetails = calculateShippingCost(input.shipping());

        final var paymentMethod = PaymentMethod.valueOf(input.paymentMethod());
        final var billing = mapper.convert(input.billing(), Billing.class);
        final var shipping = shippingInputDisassembler.toDomainModel(input.shipping(), shippingCostDetails);

        final var order = checkoutService.checkout(
                customer,
                shoppingCart,
                billing,
                shipping,
                paymentMethod
        );

        orders.add(order);
        shoppingCarts.add(shoppingCart);

        return order.getId().toString();
    }

    private CalculationResponse calculateShippingCost(final ShippingInput shipping) {
        final ZipCode origin = originAddressService.originAddress().zipCode();
        final ZipCode destination = new ZipCode(shipping.address().zipCode());
        return shippingCostService.calculate(new ShippingCostService.CalculationRequest(origin, destination));
    }

}
