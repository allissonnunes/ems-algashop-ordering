package com.github.allisson95.algashop.ordering.infrastructure.persistence.utility;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public final class CriteriaJpaUtility {

    private CriteriaJpaUtility() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> long countTotalQueryResult(@NonNull final EntityManager entityManager,
                                                 @NonNull final Class<T> entityClass,
                                                 @Nullable final PredicateBuilderFunction<T> predicateBuilderFn) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = cb.createQuery(Long.class);
        final Root<T> root = query.from(entityClass);

        final Expression<Long> countExpression = cb.count(root);
        query.select(countExpression);

        if (predicateBuilderFn != null) {
            final Predicate[] predicatesArray = predicateBuilderFn.toPredicates(root, query, cb);
            query.where(predicatesArray);
        }

        final TypedQuery<Long> typedQuery = entityManager.createQuery(query);

        return typedQuery.getSingleResult();
    }

    public static List<Order> toCriteriaOrders(@NonNull final Pageable pageable,
                                               @NonNull final CriteriaBuilder cb,
                                               @NonNull final Root<?> root) {
        final List<Order> orders = new ArrayList<>();
        for (final Sort.Order sortOrder : pageable.getSort()) {
            Path<?> path;
            try {
                path = root.get(sortOrder.getProperty());
            } catch (final IllegalArgumentException e) {
                // Skip invalid properties to avoid runtime errors
                continue;
            }

            if (sortOrder.isAscending()) {
                orders.add(cb.asc(path));
            } else {
                orders.add(cb.desc(path));
            }
        }
        return orders;
    }

    public interface PredicateBuilderFunction<T> {

        @Nullable
        Predicate[] toPredicates(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb);

    }

}
