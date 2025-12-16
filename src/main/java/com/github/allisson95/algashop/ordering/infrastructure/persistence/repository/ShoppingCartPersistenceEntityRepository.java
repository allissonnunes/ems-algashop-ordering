package com.github.allisson95.algashop.ordering.infrastructure.persistence.repository;

import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity_;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartPersistenceEntityRepository extends JpaRepository<ShoppingCartPersistenceEntity, UUID> {

    @EntityGraph(attributePaths = { ShoppingCartPersistenceEntity_.ITEMS })
    Optional<ShoppingCartPersistenceEntity> findShoppingCartPersistenceEntityWithItemsById(UUID id);

    Optional<ShoppingCartPersistenceEntity> findByCustomer_Id(UUID customerId);

}