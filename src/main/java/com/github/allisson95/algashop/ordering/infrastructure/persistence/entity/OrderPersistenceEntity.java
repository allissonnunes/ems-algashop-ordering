package com.github.allisson95.algashop.ordering.infrastructure.persistence.entity;

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
import java.util.Objects;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class OrderPersistenceEntity {

    @ToString.Include
    @Id
    @Column(nullable = false, updatable = false)
    private Long id;

    private UUID customerId;

    private BigDecimal totalAmount;

    private Integer totalItems;

    private Instant placedAt;

    private Instant paidAt;

    private Instant cancelledAt;

    private Instant readyAt;

    private String status;

    private String paymentMethod;

    @CreatedBy
    private UUID createdBy;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedBy
    private UUID lastModifiedBy;

    @LastModifiedDate
    private Instant lastModifiedAt;

    @Version
    private Integer version;

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