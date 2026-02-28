package br.dev.allissonnunes.algashop.ordering.core.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.Repository;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.core.domain.model.customer.CustomerId;

import java.time.Year;
import java.util.List;

public interface Orders extends Repository<Order, OrderId> {

    List<Order> placedByCustomerInYear(CustomerId customerId, Year year);

    long salesQuantityByCustomerInYear(CustomerId customerId, Year year);

    Money totalSoldByCustomer(CustomerId customerId);

}
