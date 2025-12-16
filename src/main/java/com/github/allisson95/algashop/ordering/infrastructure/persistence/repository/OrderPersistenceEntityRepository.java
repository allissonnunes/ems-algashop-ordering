package com.github.allisson95.algashop.ordering.infrastructure.persistence.repository;

import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity_;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderPersistenceEntityRepository extends BaseJpaRepository<OrderPersistenceEntity, Long> {

    @EntityGraph(attributePaths = { OrderPersistenceEntity_.ITEMS })
    Optional<OrderPersistenceEntity> findOrderPersistenceEntityWithItemsById(Long id);

    @Query("""
            SELECT o
            FROM OrderPersistenceEntity o
            WHERE o.customer.id = :customerId
            AND YEAR(o.placedAt) = :year
            AND o.cancelledAt IS NULL
            """)
    List<OrderPersistenceEntity> placedByCustomerInYear(@Param("customerId") UUID customerId, @Param("year") Integer year);

    @Query("""
            SELECT COUNT(o)
            FROM OrderPersistenceEntity o
            WHERE o.customer.id = :customerId
            AND YEAR(o.placedAt) = :year
            AND o.paidAt IS NOT NULL
            AND o.cancelledAt IS NULL
            """)
    long salesQuantityByCustomerInYear(@Param("customerId") UUID customerId, @Param("year") Integer year);

    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0)
            FROM OrderPersistenceEntity o
            WHERE o.customer.id = :customerId
            AND o.paidAt IS NOT NULL
            AND o.cancelledAt IS NULL
            """)
    BigDecimal totalSoldByCustomer(@Param("customerId") UUID customerId);

}