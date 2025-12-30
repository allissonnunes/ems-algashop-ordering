package com.github.allisson95.algashop.ordering.infrastructure.persistence.customer;

import com.github.allisson95.algashop.ordering.application.commons.AddressData;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerOutput;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerQueryService;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerId;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
class CustomerQueryServiceImpl implements CustomerQueryService {

    private final JdbcClient jdbcClient;

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
