package br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Address;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.Billing;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.Order;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderItem;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.Shipping;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.commons.DomainVersionHandler;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
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
            orderPersistenceEntity.setId(order.getId().value().toLong());
        }

        final CustomerPersistenceEntity customerPersistenceEntity = this.customerRepository.getReferenceById(order.getCustomerId().value());
        orderPersistenceEntity.setCustomer(customerPersistenceEntity);

        orderPersistenceEntity.setTotalAmount(order.getTotalAmount().value());
        orderPersistenceEntity.setTotalItems(order.getTotalItems().value());
        orderPersistenceEntity.setPlacedAt(order.getPlacedAt());
        orderPersistenceEntity.setPaidAt(order.getPaidAt());
        orderPersistenceEntity.setCanceledAt(order.getCanceledAt());
        orderPersistenceEntity.setReadyAt(order.getReadyAt());
        orderPersistenceEntity.setBilling(assembleBilling(order.getBilling()));
        orderPersistenceEntity.setShipping(assembleShipping(order.getShipping()));
        orderPersistenceEntity.setStatus(order.getStatus().name());
        orderPersistenceEntity.setPaymentMethod(order.getPaymentMethod().name());
        orderPersistenceEntity.replaceItems(mergeItems(order, orderPersistenceEntity));
        orderPersistenceEntity.setVersion(DomainVersionHandler.getVersion(order));
        orderPersistenceEntity.setDomainEventSupplier(order::domainEvents);
        orderPersistenceEntity.setOnAllEventsPublished(order::clearDomainEvents);

        return orderPersistenceEntity;
    }

    public OrderItemPersistenceEntity fromDomain(final OrderItem orderItem) {
        return merge(new OrderItemPersistenceEntity(), orderItem);
    }

    public OrderItemPersistenceEntity merge(final OrderItemPersistenceEntity orderItemPersistenceEntity, final OrderItem orderItem) {
        requireNonNull(orderItemPersistenceEntity, "orderItemPersistenceEntity cannot be null");
        requireNonNull(orderItem, "orderItem cannot be null");

        orderItemPersistenceEntity.setId(orderItem.getId().value().toLong());
        orderItemPersistenceEntity.setProductId(orderItem.getProductId().value());
        orderItemPersistenceEntity.setProductName(orderItem.getProductName().value());
        orderItemPersistenceEntity.setPrice(orderItem.getPrice().value());
        orderItemPersistenceEntity.setQuantity(orderItem.getQuantity().value());
        orderItemPersistenceEntity.setTotalAmount(orderItem.getTotalAmount().value());

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
                .map(orderItem -> ofNullable(existingItemMap.get(orderItem.getId().value().toLong()))
                        .map(existingItem -> merge(existingItem, orderItem))
                        .orElse(fromDomain(orderItem)))
                .collect(toSet());
    }

}
