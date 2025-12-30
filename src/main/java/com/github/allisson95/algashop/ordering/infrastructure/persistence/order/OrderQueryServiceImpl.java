package com.github.allisson95.algashop.ordering.infrastructure.persistence.order;

import com.github.allisson95.algashop.ordering.application.checkout.BillingData;
import com.github.allisson95.algashop.ordering.application.checkout.RecipientData;
import com.github.allisson95.algashop.ordering.application.commons.AddressData;
import com.github.allisson95.algashop.ordering.application.order.query.*;
import com.github.allisson95.algashop.ordering.domain.model.order.OrderId;
import com.github.allisson95.algashop.ordering.domain.model.order.OrderItemId;
import com.github.allisson95.algashop.ordering.domain.model.order.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
class OrderQueryServiceImpl implements OrderQueryService {

    private final JdbcClient jdbcClient;

    @Override
    public OrderDetailOutput findById(final String rawOrderId) {
        requireNonNull(rawOrderId, "rawOrderId cannot be null");
        final OrderId orderId = new OrderId(rawOrderId);
        return jdbcClient.sql("""
                        SELECT
                            o.id AS order_id,
                            o.total_items,
                            o.total_amount,
                            o.placed_at,
                            o.paid_at,
                            o.canceled_at,
                            o.ready_at,
                            o.status,
                            o.payment_method,
                            o.shipping_cost,
                            o.shipping_expected_delivery_date,
                            o.shipping_recipient_first_name,
                            o.shipping_recipient_last_name,
                            o.shipping_recipient_document,
                            o.shipping_recipient_phone,
                            o.shipping_address_street,
                            o.shipping_address_number,
                            o.shipping_address_complement,
                            o.shipping_address_neighborhood,
                            o.shipping_address_city,
                            o.shipping_address_state,
                            o.shipping_address_zip_code,
                            o.billing_first_name,
                            o.billing_last_name,
                            o.billing_document,
                            o.billing_phone,
                            o.billing_email,
                            o.billing_address_street,
                            o.billing_address_number,
                            o.billing_address_complement,
                            o.billing_address_neighborhood,
                            o.billing_address_city,
                            o.billing_address_state,
                            o.billing_address_zip_code,
                            c.id AS customer_id,
                            c.first_name AS customer_first_name,
                            c.last_name AS customer_last_name,
                            c.email AS customer_email,
                            c.document AS customer_document,
                            c.phone AS customer_phone,
                            i.id AS item_id,
                            i.product_id AS item_product_id,
                            i.product_name AS item_product_name,
                            i.price AS item_price,
                            i.quantity AS item_quantity,
                            i.total_amount AS item_total_amount
                        FROM
                            "'order'" o
                        INNER JOIN
                            customer c ON c.id = o.customer_id
                        LEFT JOIN
                            order_item i ON i.order_id = o.id
                        WHERE
                            o.id = :orderId
                        """)
                .param("orderId", orderId.value().toLong())
                .query(rs -> {
                    OrderDetailOutput order = null;
                    List<OrderItemDetailOutput> items = new ArrayList<>();

                    while (rs.next()) {
                        if (order == null) {
                            order = mapOrder(rs, items);
                        }

                        Long itemId = rs.getObject("item_id", Long.class);
                        if (itemId != null) {
                            items.add(mapOrderItem(rs));
                        }
                    }

                    return Optional.ofNullable(order);
                })
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private OrderDetailOutput mapOrder(final @NonNull ResultSet rs, final List<OrderItemDetailOutput> items) throws SQLException {
        return OrderDetailOutput.builder()
                .id(new OrderId(rs.getObject("order_id", Long.class)).toString())
                .customer(CustomerMinimalOutput.builder()
                        .id(rs.getObject("customer_id", UUID.class))
                        .firstName(rs.getString("customer_first_name"))
                        .lastName(rs.getString("customer_last_name"))
                        .email(rs.getString("customer_email"))
                        .document(rs.getString("customer_document"))
                        .phone(rs.getString("customer_phone"))
                        .build())
                .totalItems(rs.getInt("total_items"))
                .totalAmount(rs.getBigDecimal("total_amount"))
                .placedAt(rs.getTimestamp("placed_at") != null ? rs.getTimestamp("placed_at").toInstant() : null)
                .paidAt(rs.getTimestamp("paid_at") != null ? rs.getTimestamp("paid_at").toInstant() : null)
                .canceledAt(rs.getTimestamp("canceled_at") != null ? rs.getTimestamp("canceled_at").toInstant() : null)
                .readyAt(rs.getTimestamp("ready_at") != null ? rs.getTimestamp("ready_at").toInstant() : null)
                .status(rs.getString("status"))
                .paymentMethod(rs.getString("payment_method"))
                .shipping(ShippingData.builder()
                        .cost(rs.getBigDecimal("shipping_cost"))
                        .expectedDeliveryDate(rs.getDate("shipping_expected_delivery_date") != null ? rs.getDate("shipping_expected_delivery_date").toLocalDate() : null)
                        .recipient(RecipientData.builder()
                                .firstName(rs.getString("shipping_recipient_first_name"))
                                .lastName(rs.getString("shipping_recipient_last_name"))
                                .document(rs.getString("shipping_recipient_document"))
                                .phone(rs.getString("shipping_recipient_phone"))
                                .build())
                        .address(AddressData.builder()
                                .street(rs.getString("shipping_address_street"))
                                .number(rs.getString("shipping_address_number"))
                                .complement(rs.getString("shipping_address_complement"))
                                .neighborhood(rs.getString("shipping_address_neighborhood"))
                                .city(rs.getString("shipping_address_city"))
                                .state(rs.getString("shipping_address_state"))
                                .zipCode(rs.getString("shipping_address_zip_code"))
                                .build())
                        .build())
                .billing(BillingData.builder()
                        .firstName(rs.getString("billing_first_name"))
                        .lastName(rs.getString("billing_last_name"))
                        .document(rs.getString("billing_document"))
                        .phone(rs.getString("billing_phone"))
                        .email(rs.getString("billing_email"))
                        .address(AddressData.builder()
                                .street(rs.getString("billing_address_street"))
                                .number(rs.getString("billing_address_number"))
                                .complement(rs.getString("billing_address_complement"))
                                .neighborhood(rs.getString("billing_address_neighborhood"))
                                .city(rs.getString("billing_address_city"))
                                .state(rs.getString("billing_address_state"))
                                .zipCode(rs.getString("billing_address_zip_code"))
                                .build())
                        .build())
                .items(items)
                .build();
    }

    private OrderItemDetailOutput mapOrderItem(final @NonNull ResultSet rs) throws SQLException {
        return OrderItemDetailOutput.builder()
                .id(new OrderItemId(rs.getObject("item_id", Long.class)).toString())
                .orderId(new OrderId(rs.getObject("order_id", Long.class)).toString())
                .productId(rs.getObject("item_product_id", UUID.class))
                .productName(rs.getString("item_product_name"))
                .price(rs.getBigDecimal("item_price"))
                .quantity(rs.getInt("item_quantity"))
                .totalAmount(rs.getBigDecimal("item_total_amount"))
                .build();
    }

}
