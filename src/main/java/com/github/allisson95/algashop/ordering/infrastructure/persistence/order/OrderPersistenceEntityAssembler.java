package com.github.allisson95.algashop.ordering.infrastructure.persistence.order;

import com.github.allisson95.algashop.ordering.domain.model.commons.Address;
import com.github.allisson95.algashop.ordering.domain.model.order.Billing;
import com.github.allisson95.algashop.ordering.domain.model.order.Order;
import com.github.allisson95.algashop.ordering.domain.model.order.OrderItem;
import com.github.allisson95.algashop.ordering.domain.model.order.Shipping;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.commons.DomainVersionHandler;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
@Component
public class OrderPersistenceEntityAssembler {

    private final CustomerPersistenceEntityRepository customerRepository;

    public OrderPersistenceEntity fromDomain(final Order order) {
        return merge(new OrderPersistenceEntity(), order);
    }

    public OrderPersistenceEntity merge(final OrderPersistenceEntity orderPersistenceEntity, final Order order) {
        requireNonNull(orderPersistenceEntity, "orderPersistenceEntity cannot be null");
        requireNonNull(order, "order cannot be null");

        if (isNull(orderPersistenceEntity.getId())) {
            orderPersistenceEntity.setId(order.id().value().toLong());
        }

        final CustomerPersistenceEntity customerPersistenceEntity = this.customerRepository.getReferenceById(order.getCustomerId().value());
        orderPersistenceEntity.setCustomer(customerPersistenceEntity);

        orderPersistenceEntity.setTotalAmount(order.getTotalAmount().value());
        orderPersistenceEntity.setTotalItems(order.getTotalItems().value());
        orderPersistenceEntity.setPlacedAt(order.getPlacedAt());
        orderPersistenceEntity.setPaidAt(order.getPaidAt());
        orderPersistenceEntity.setCancelledAt(order.getCancelledAt());
        orderPersistenceEntity.setReadyAt(order.getReadyAt());
        orderPersistenceEntity.setBilling(assembleBilling(order.getBilling()));
        orderPersistenceEntity.setShipping(assembleShipping(order.getShipping()));
        orderPersistenceEntity.setStatus(order.getStatus().name());
        orderPersistenceEntity.setPaymentMethod(order.getPaymentMethod().name());
        orderPersistenceEntity.replaceItems(mergeItems(order, orderPersistenceEntity));
        orderPersistenceEntity.setVersion(DomainVersionHandler.getVersion(order));

        return orderPersistenceEntity;
    }

    public OrderItemPersistenceEntity fromDomain(final OrderItem orderItem) {
        return merge(new OrderItemPersistenceEntity(), orderItem);
    }

    public OrderItemPersistenceEntity merge(final OrderItemPersistenceEntity orderItemPersistenceEntity, final OrderItem orderItem) {
        requireNonNull(orderItemPersistenceEntity, "orderItemPersistenceEntity cannot be null");
        requireNonNull(orderItem, "orderItem cannot be null");

        orderItemPersistenceEntity.setId(orderItem.id().value().toLong());
        orderItemPersistenceEntity.setProductId(orderItem.productId().toString());
        orderItemPersistenceEntity.setProductName(orderItem.productName().value());
        orderItemPersistenceEntity.setPrice(orderItem.price().value());
        orderItemPersistenceEntity.setQuantity(orderItem.quantity().value());
        orderItemPersistenceEntity.setTotalAmount(orderItem.totalAmount().value());

        return orderItemPersistenceEntity;
    }

    private BillingEmbeddable assembleBilling(final Billing billing) {
        if (isNull(billing)) {
            return null;
        }

        return BillingEmbeddable.builder()
                .firstName(billing.fullName().firstName())
                .lastName(billing.fullName().lastName())
                .document(billing.document().value())
                .phone(billing.phone().value())
                .email(billing.email().value())
                .address(assembleAddress(billing.address()))
                .build();
    }

    private ShippingEmbeddable assembleShipping(final Shipping shipping) {
        if (isNull(shipping)) {
            return null;
        }

        return ShippingEmbeddable.builder()
                .recipient(assembleRecipient(shipping))
                .address(assembleAddress(shipping.address()))
                .cost(shipping.cost().value())
                .expectedDeliveryDate(shipping.expectedDeliveryDate())
                .build();
    }

    private RecipientEmbeddable assembleRecipient(final Shipping shipping) {
        return RecipientEmbeddable.builder()
                .firstName(shipping.recipient().fullName().firstName())
                .lastName(shipping.recipient().fullName().lastName())
                .document(shipping.recipient().document().value())
                .phone(shipping.recipient().phone().value())
                .build();
    }

    private AddressEmbeddable assembleAddress(final Address address) {
        return AddressEmbeddable.builder()
                .street(address.street())
                .number(address.number())
                .complement(address.complement())
                .neighborhood(address.neighborhood())
                .city(address.city())
                .state(address.state())
                .zipCode(address.zipCode().value())
                .build();
    }

    private Set<OrderItemPersistenceEntity> mergeItems(final Order order, final OrderPersistenceEntity orderPersistenceEntity) {
        final Set<OrderItem> orderItems = order.getItems();
        if (isNull(orderItems) || orderItems.isEmpty()) {
            return new LinkedHashSet<>();
        }

        final Set<OrderItemPersistenceEntity> existingItems = orderPersistenceEntity.getItems();
        if (isNull(existingItems) || existingItems.isEmpty()) {
            return orderItems.stream()
                    .map(this::fromDomain)
                    .collect(toSet());
        }

        final Map<Long, OrderItemPersistenceEntity> existingItemMap = existingItems.stream()
                .collect(toMap(OrderItemPersistenceEntity::getId, identity()));

        return orderItems.stream()
                .map(orderItem -> ofNullable(existingItemMap.get(orderItem.id().value().toLong()))
                        .map(existingItem -> merge(existingItem, orderItem))
                        .orElse(fromDomain(orderItem)))
                .collect(toSet());
    }

}
