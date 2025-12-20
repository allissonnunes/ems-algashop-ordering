package com.github.allisson95.algashop.ordering.infrastructure.persistence.shoppingcart;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartPersistenceEntityRepository extends JpaRepository<ShoppingCartPersistenceEntity, UUID> {

    @EntityGraph(attributePaths = { ShoppingCartPersistenceEntity_.ITEMS })
    Optional<ShoppingCartPersistenceEntity> findShoppingCartPersistenceEntityWithItemsById(UUID id);

    Optional<ShoppingCartPersistenceEntity> findByCustomer_Id(UUID customerId);

    boolean existsByCustomer_Id(UUID customerId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE
                ShoppingCartItemPersistenceEntity i
            SET
                i.price = :price,
                i.totalAmount = :price * i.quantity
            WHERE
                i.productId = :productId
            """)
    void updateItemPrice(@Param("productId") UUID productId, @Param("price") BigDecimal price);

    @Modifying
    @Transactional
    @Query("""
            UPDATE
                ShoppingCartItemPersistenceEntity i
            SET
                i.available = :available
            WHERE
                i.productId = :productId
            """)
    void updateItemAvailability(@Param("productId") UUID productId, @Param("available") Boolean available);

    @Modifying
    @Transactional
    @Query("""
            UPDATE
                ShoppingCartPersistenceEntity sc
            SET
                sc.totalAmount = (
                    SELECT SUM(i.totalAmount)
                    FROM ShoppingCartItemPersistenceEntity i
                    WHERE i.shoppingCart = sc
                )
            WHERE
                EXISTS (
                    SELECT 1
                    FROM ShoppingCartItemPersistenceEntity i2
                    WHERE
                        i2.shoppingCart = sc
                        AND i2.productId = :productId
                )
            """)
    void recalculateTotalAmountForShoppingCartsWithProductId(@Param("productId") UUID productId);

}