package com.github.allisson95.algashop.ordering.application.checkout;

import com.github.allisson95.algashop.ordering.domain.model.commons.Quantity;
import com.github.allisson95.algashop.ordering.domain.model.commons.ZipCode;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerId;
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

    private final ShippingInputDisassembler shippingInputDisassembler;

    private final BillingInputDisassembler billingInputDisassembler;

    @Transactional
    public String buyNow(final BuyNowInput input) {
        requireNonNull(input, "input cannot be null");

        final PaymentMethod paymentMethod = PaymentMethod.valueOf(input.paymentMethod());
        final CustomerId customerId = new CustomerId(input.customerId());
        final Quantity quantity = new Quantity(input.quantity());

        final Product product = findProduct(new ProductId(input.productId()));

        CalculationResponse shippingCostDetails = calculateShippingCost(input.shipping());

        final Shipping shipping = shippingInputDisassembler.toDomainModel(input.shipping(), shippingCostDetails);

        final Billing billing = billingInputDisassembler.toDomainModel(input.billing());

        Order order = buyNowService.buyNow(
                product, customerId, billing, shipping, quantity, paymentMethod
        );

        orders.add(order);

        return order.getId().toString();
    }

    private CalculationResponse calculateShippingCost(final ShippingInput shipping) {
        final ZipCode origin = originAddressService.originAddress().zipCode();
        final ZipCode destination = new ZipCode(shipping.address().zipCode());
        return shippingCostService.calculate(new ShippingCostService.CalculationRequest(origin, destination));
    }

    private Product findProduct(final ProductId productId) {
        return productCatalogService.ofId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

}
