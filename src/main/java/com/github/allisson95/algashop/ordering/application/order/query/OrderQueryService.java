package com.github.allisson95.algashop.ordering.application.order.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderQueryService {

    OrderDetailOutput findById(String orderId);

    Page<OrderSumaryOutput> filter(Pageable pageable);

}
