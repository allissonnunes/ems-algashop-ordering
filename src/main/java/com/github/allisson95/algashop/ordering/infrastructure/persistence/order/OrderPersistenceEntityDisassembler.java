package com.github.allisson95.algashop.ordering.infrastructure.persistence.order;

import com.github.allisson95.algashop.ordering.domain.model.commons.*;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerId;
import com.github.allisson95.algashop.ordering.domain.model.order.*;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductId;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductName;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.commons.DomainVersionHandler;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;

@Component
public class OrderPersistenceEntityDisassembler {

    public Order toDomainEntity(final OrderPersistenceEntity orderPersistenceEntity) {
        final Order order = Order.existingOrder()
                .id(new OrderId(orderPersistenceEntity.getId()))
                .customerId(new CustomerId(orderPersistenceEntity.getCustomerId()))
                .totalAmount(new Money(orderPersistenceEntity.getTotalAmount()))
                .totalItems(new Quantity(orderPersistenceEntity.getTotalItems()))
                .placedAt(orderPersistenceEntity.getPlacedAt())
                .paidAt(orderPersistenceEntity.getPaidAt())
                .cancelledAt(orderPersistenceEntity.getCancelledAt())
                .readyAt(orderPersistenceEntity.getReadyAt())
                .billing(assembleBilling(orderPersistenceEntity.getBilling()))
                .shipping(assembleShipping(orderPersistenceEntity.getShipping()))
                .status(OrderStatus.valueOf(orderPersistenceEntity.getStatus()))
                .paymentMethod(PaymentMethod.valueOf(orderPersistenceEntity.getPaymentMethod()))
                .items(assembleOrderItems(orderPersistenceEntity.getItems()))
                .build();
        DomainVersionHandler.setVersion(order, orderPersistenceEntity.getVersion());
        return order;
    }

    private Billing assembleBilling(final BillingEmbeddable billing) {
        if (isNull(billing)) {
            return null;
        }

        return Billing.builder()
                .fullName(new FullName(billing.getFirstName(), billing.getLastName()))
                .document(new Document(billing.getDocument()))
                .phone(new Phone(billing.getPhone()))
                .email(new Email(billing.getEmail()))
                .address(assembleAddress(billing.getAddress()))
                .build();
    }

    private Shipping assembleShipping(final ShippingEmbeddable shipping) {
        if (isNull(shipping)) {
            return null;
        }

        return Shipping.builder()
                .recipient(assembleRecipient(shipping))
                .address(assembleAddress(shipping.getAddress()))
                .cost(new Money(shipping.getCost()))
                .expectedDeliveryDate(shipping.getExpectedDeliveryDate())
                .build();
    }

    private Recipient assembleRecipient(final ShippingEmbeddable shipping) {
        return Recipient.builder()
                .fullName(new FullName(shipping.getRecipient().getFirstName(), shipping.getRecipient().getLastName()))
                .document(new Document(shipping.getRecipient().getDocument()))
                .phone(new Phone(shipping.getRecipient().getPhone()))
                .build();
    }

    private Address assembleAddress(final AddressEmbeddable address) {
        return Address.builder()
                .street(address.getStreet())
                .number(address.getNumber())
                .complement(address.getComplement())
                .neighborhood(address.getNeighborhood())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(new ZipCode(address.getZipCode()))
                .build();
    }

    private Set<OrderItem> assembleOrderItems(final Set<OrderItemPersistenceEntity> items) {
        if (isNull(items) || items.isEmpty()) {
            return new LinkedHashSet<>();
        }
        return items.stream()
                .map(orderItemPersistenceEntity -> OrderItem.existingOrderItem()
                        .id(new OrderItemId(orderItemPersistenceEntity.getId()))
                        .orderId(new OrderId(orderItemPersistenceEntity.getOrderId()))
                        .productId(new ProductId(orderItemPersistenceEntity.getProductId()))
                        .productName(new ProductName(orderItemPersistenceEntity.getProductName()))
                        .price(new Money(orderItemPersistenceEntity.getPrice()))
                        .quantity(new Quantity(orderItemPersistenceEntity.getQuantity()))
                        .totalAmount(new Money(orderItemPersistenceEntity.getTotalAmount()))
                        .build()
                )
                .collect(toSet());
    }

}
