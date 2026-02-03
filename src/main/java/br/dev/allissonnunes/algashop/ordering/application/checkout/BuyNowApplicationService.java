package br.dev.allissonnunes.algashop.ordering.application.checkout;

import br.dev.allissonnunes.algashop.ordering.application.utility.Mapper;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.ZipCode;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.Customer;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.Customers;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.*;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationResponse;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductCatalogService;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductId;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductNotFoundException;
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
        final ProductId productId = new ProductId(input.productId());
        final Quantity quantity = new Quantity(input.quantity());

        final Customer customer = customers.ofId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        final Product product = productCatalogService.ofId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        final Shipping shipping = shippingInputDisassembler.toDomainModel(input.shipping(), calculateShippingCost(input.shipping()));
        final Billing billing = mapper.convert(input.billing(), Billing.class);

        Order order = buyNowService.buyNow(
                product, customer, billing, shipping, quantity, paymentMethod
        );

        orders.add(order);

        return order.getId().toString();
    }

    private CalculationResponse calculateShippingCost(final ShippingInput shipping) {
        final ZipCode origin = originAddressService.originAddress().zipCode();
        final ZipCode destination = new ZipCode(shipping.address().zipCode());
        return shippingCostService.calculate(new ShippingCostService.CalculationRequest(origin, destination));
    }

}
