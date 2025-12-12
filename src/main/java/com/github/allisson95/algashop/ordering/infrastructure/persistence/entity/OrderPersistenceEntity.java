package com.github.allisson95.algashop.ordering.infrastructure.persistence.entity;

import com.github.allisson95.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNullElseGet;

@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "'order'")
@EntityListeners(AuditingEntityListener.class)
public class OrderPersistenceEntity {

    @ToString.Include
    @Id
    @Column(nullable = false)
    private Long id;

    @JoinColumn(name = "customer_id", nullable = false)
    @ManyToOne(optional = false)
    private CustomerPersistenceEntity customer;

    private BigDecimal totalAmount;

    private Integer totalItems;

    private Instant placedAt;

    private Instant paidAt;

    private Instant cancelledAt;

    private Instant readyAt;

    @Embedded
    private BillingEmbeddable billing;

    @Embedded
    private ShippingEmbeddable shipping;

    private String status;

    private String paymentMethod;

    @OneToMany(mappedBy = OrderItemPersistenceEntity_.ORDER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItemPersistenceEntity> items = new LinkedHashSet<>();

    @CreatedBy
    private UUID createdBy;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedBy
    private UUID lastModifiedBy;

    @LastModifiedDate
    private Instant lastModifiedAt;

    @Version
    private Long version;

    @Builder
    public OrderPersistenceEntity(final Long id, final CustomerPersistenceEntity customer, final BigDecimal totalAmount, final Integer totalItems, final Instant placedAt, final Instant paidAt, final Instant cancelledAt, final Instant readyAt, final BillingEmbeddable billing, final ShippingEmbeddable shipping, final String status, final String paymentMethod, final Set<OrderItemPersistenceEntity> items, final UUID createdBy, final Instant createdAt, final UUID lastModifiedBy, final Instant lastModifiedAt, final Long version) {
        this.id = id;
        this.customer = customer;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
        this.placedAt = placedAt;
        this.paidAt = paidAt;
        this.cancelledAt = cancelledAt;
        this.readyAt = readyAt;
        this.billing = billing;
        this.shipping = shipping;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.replaceItems(items);
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedAt = lastModifiedAt;
        this.version = version;
    }

    public UUID getCustomerId() {
        if (isNull(this.customer)) {
            return null;
        }
        return this.customer.getId();
    }

    public void replaceItems(final Set<OrderItemPersistenceEntity> items) {
        if (isNull(this.getItems())) {
            this.setItems(new LinkedHashSet<>());
        } else {
            this.getItems().clear();
        }

        if (isNull(items) || items.isEmpty()) {
            return;
        }

        items.forEach(this::addItem);
    }

    private void addItem(final OrderItemPersistenceEntity item) {
        if (isNull(item)) {
            return;
        }

        item.setOrder(this);
        this.items.add(item);
    }

    private void setItems(final Set<OrderItemPersistenceEntity> items) {
        this.items = requireNonNullElseGet(items, LinkedHashSet::new);
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        final OrderPersistenceEntity entity = (OrderPersistenceEntity) o;
        return getId() != null && Objects.equals(getId(), entity.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

}