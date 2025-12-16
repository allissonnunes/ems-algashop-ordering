package com.github.allisson95.algashop.ordering.infrastructure.persistence.repository;

import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity_;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderPersistenceEntityRepository extends BaseJpaRepository<OrderPersistenceEntity, Long> {

    @EntityGraph(attributePaths = { OrderPersistenceEntity_.ITEMS })
    Optional<OrderPersistenceEntity> findOrderPersistenceEntityWithItemsById(Long id);

    List<OrderPersistenceEntity> findByCustomer_IdAndPlacedAtBetween(UUID id, Instant placedAtStart, Instant placedAtEnd);

}