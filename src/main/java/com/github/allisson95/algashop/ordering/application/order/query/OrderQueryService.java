package com.github.allisson95.algashop.ordering.application.order.query;

public interface OrderQueryService {

    OrderDetailOutput findById(String orderId);

}
