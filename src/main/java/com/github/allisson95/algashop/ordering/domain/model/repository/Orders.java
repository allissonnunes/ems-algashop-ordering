package com.github.allisson95.algashop.ordering.domain.model.repository;

import com.github.allisson95.algashop.ordering.domain.model.entity.Order;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.id.OrderId;

import java.time.Year;
import java.util.List;

public interface Orders extends Repository<Order, OrderId> {

    List<Order> placedByCustomerInYear(CustomerId customerId, Year year);

}
