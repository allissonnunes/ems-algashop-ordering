package com.github.allisson95.algashop.ordering.infrastructure.persistence.customer;

import com.github.allisson95.algashop.ordering.application.commons.AddressData;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerFilter;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerOutput;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerQueryService;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerSummaryOutput;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerId;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.utility.CriteriaJpaUtility;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
class CustomerQueryServiceImpl implements CustomerQueryService {

    private final JdbcClient jdbcClient;

    private final EntityManager entityManager;

    @Override
    public CustomerOutput findById(final UUID customerId) {
        requireNonNull(customerId, "customerId cannot be null");
        return jdbcClient.sql("""
                        SELECT
                          id,
                          first_name,
                          last_name,
                          birth_date,
                          email,
                          phone,
                          document,
                          promotion_notifications_allowed,
                          loyalty_points,
                          registered_at,
                          archived,
                          archived_at,
                          address_street,
                          address_number,
                          address_complement,
                          address_neighborhood,
                          address_city,
                          address_state,
                          address_zip_code
                        FROM
                          customer
                        WHERE
                          id = :customerId
                        """)
                .param("customerId", customerId)
                .query(customerOutputRowMapper())
                .optional()
                .orElseThrow(() -> new CustomerNotFoundException(new CustomerId(customerId)));
    }

    @Override
    public Page<CustomerSummaryOutput> filter(final CustomerFilter filter) {
        long totalQueryResults = countTotalQueryResult(filter);
        final PageRequest pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                filter.getSortDirectionOrDefault(),
                filter.getSortByPropertyOrDefault().getProperty()
        );
        if (totalQueryResults == 0) {
            return Page.empty(pageable);
        }
        return filterQuery(pageable, filter, totalQueryResults);
    }

    private long countTotalQueryResult(final CustomerFilter filter) {
        return CriteriaJpaUtility.countTotalQueryResult(
                entityManager,
                CustomerPersistenceEntity.class,
                (root, query, cb) -> toPredicates(cb, root, filter));
    }

    private Page<CustomerSummaryOutput> filterQuery(final PageRequest pageable, final CustomerFilter filter, final long totalQueryResults) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<CustomerSummaryOutput> query = cb.createQuery(CustomerSummaryOutput.class);
        final Root<CustomerPersistenceEntity> root = query.from(CustomerPersistenceEntity.class);

        query.select(
                cb.construct(
                        CustomerSummaryOutput.class,
                        root.get(CustomerPersistenceEntity_.id).alias("id"),
                        root.get(CustomerPersistenceEntity_.firstName).alias("firstName"),
                        root.get(CustomerPersistenceEntity_.lastName).alias("lastName"),
                        root.get(CustomerPersistenceEntity_.email).alias("email"),
                        root.get(CustomerPersistenceEntity_.document).alias("document"),
                        root.get(CustomerPersistenceEntity_.phone).alias("phone"),
                        root.get(CustomerPersistenceEntity_.birthDate).alias("birthDate"),
                        root.get(CustomerPersistenceEntity_.loyaltyPoints).alias("loyaltyPoints"),
                        root.get(CustomerPersistenceEntity_.registeredAt).alias("registeredAt"),
                        root.get(CustomerPersistenceEntity_.archivedAt).alias("archivedAt"),
                        root.get(CustomerPersistenceEntity_.promotionNotificationsAllowed).alias("promotionNotificationsAllowed"),
                        root.get(CustomerPersistenceEntity_.archived).alias("archived")
                ));

        final Predicate[] predicates = toPredicates(cb, root, filter);
        query.where(predicates);

        final List<Order> sortOrder = CriteriaJpaUtility.toCriteriaOrders(pageable, cb, root);
        if (!sortOrder.isEmpty()) {
            query.orderBy(sortOrder);
        }

        final TypedQuery<CustomerSummaryOutput> typedQuery = entityManager.createQuery(query);

        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        final List<CustomerSummaryOutput> customerSummaryOutputs = typedQuery.getResultList();

        return new PageImpl<>(customerSummaryOutputs, pageable, totalQueryResults);
    }

    private Predicate[] toPredicates(final CriteriaBuilder cb, final Root<CustomerPersistenceEntity> root, final CustomerFilter filter) {
        final List<Predicate> predicates = new ArrayList<>();

        if (filter.getFirstName() != null && !filter.getFirstName().isBlank()) {
            predicates.add(cb.like(cb.lower(root.get(CustomerPersistenceEntity_.firstName)), "%" + filter.getFirstName().toLowerCase(Locale.ROOT) + "%"));
        }

        if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
            predicates.add(cb.like(cb.lower(root.get(CustomerPersistenceEntity_.email)), "%" + filter.getEmail().toLowerCase(Locale.ROOT) + "%"));
        }

        return predicates.toArray(Predicate[]::new);
    }

    private RowMapper<CustomerOutput> customerOutputRowMapper() {
        return (final ResultSet rs, final int rowNum) -> CustomerOutput.builder()
                .id(rs.getObject("id", UUID.class))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .birthDate(rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null)
                .email(rs.getString("email"))
                .phone(rs.getString("phone"))
                .document(rs.getString("document"))
                .promotionNotificationsAllowed(rs.getBoolean("promotion_notifications_allowed"))
                .loyaltyPoints(rs.getInt("loyalty_points"))
                .registeredAt(rs.getTimestamp("registered_at").toInstant())
                .archived(rs.getBoolean("archived"))
                .archivedAt(rs.getTimestamp("archived_at") != null ? rs.getTimestamp("archived_at").toInstant() : null)
                .address(AddressData.builder()
                        .street(rs.getString("address_street"))
                        .number(rs.getString("address_number"))
                        .complement(rs.getString("address_complement"))
                        .neighborhood(rs.getString("address_neighborhood"))
                        .city(rs.getString("address_city"))
                        .state(rs.getString("address_state"))
                        .zipCode(rs.getString("address_zip_code"))
                        .build())
                .build();
    }

}
