package com.github.allisson95.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.github.allisson95.algashop.ordering.infrastructure.persistence.commons.AbstractEntity;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "shopping_cart")
@EntityListeners(AuditingEntityListener.class)
public class ShoppingCartPersistenceEntity extends AbstractEntity<UUID> {

    @ToString.Include
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerPersistenceEntity customer;

    private BigDecimal totalAmount;

    private Integer totalItems;

    @OneToMany(mappedBy = ShoppingCartItemPersistenceEntity_.SHOPPING_CART, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ShoppingCartItemPersistenceEntity> items = new LinkedHashSet<>();

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

    public UUID getCustomerId() {
        return this.customer.getId();
    }

    public void replaceItems(final Set<ShoppingCartItemPersistenceEntity> items) {
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

    private void addItem(final ShoppingCartItemPersistenceEntity item) {
        if (isNull(item)) {
            return;
        }

        item.setShoppingCart(this);
        this.items.add(item);
    }

    private void setItems(final Set<ShoppingCartItemPersistenceEntity> items) {
        this.items = requireNonNullElseGet(items, LinkedHashSet::new);
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        final ShoppingCartPersistenceEntity that = (ShoppingCartPersistenceEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

}