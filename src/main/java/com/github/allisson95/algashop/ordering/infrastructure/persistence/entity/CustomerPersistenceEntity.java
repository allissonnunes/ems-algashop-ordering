package com.github.allisson95.algashop.ordering.infrastructure.persistence.entity;

import com.github.allisson95.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "customer")
@EntityListeners(AuditingEntityListener.class)
public class CustomerPersistenceEntity implements Persistable<UUID> {

    @ToString.Include
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    private String email;

    private String phone;

    private String document;

    private Boolean promotionNotificationsAllowed;

    private Boolean archived;

    private Instant registeredAt;

    private Instant archivedAt;

    private Integer loyaltyPoints;

    @Embedded
    private AddressEmbeddable address;

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

    @Builder.Default
    @Transient
    private boolean isNew = true;

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

}
