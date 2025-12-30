package com.github.allisson95.algashop.ordering.application.order.query;

import java.util.UUID;

public interface OrderQueryService {

    OrderDetailOutput findById(UUID orderId);

}
