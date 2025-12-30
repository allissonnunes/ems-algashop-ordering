package com.github.allisson95.algashop.ordering.infrastructure.persistence.order;

import com.github.allisson95.algashop.ordering.application.order.query.OrderDetailOutput;
import com.github.allisson95.algashop.ordering.application.order.query.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
class OrderQueryServiceImpl implements OrderQueryService {

    private final JdbcClient jdbcClient;

    @Override
    public OrderDetailOutput findById(final UUID orderId) {
        return null;
    }

}
