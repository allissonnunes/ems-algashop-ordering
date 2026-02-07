package br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.Order;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderId;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.Orders;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.commons.DomainVersionHandler;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class OrdersPersistenceProvider implements Orders {

    private final OrderPersistenceEntityRepository repository;

    private final OrderPersistenceEntityAssembler assembler;

    private final OrderPersistenceEntityDisassembler disassembler;

    private final EntityManager entityManager;

    @Override
    public Optional<Order> ofId(final OrderId orderId) {
        return this.repository.findOrderPersistenceEntityWithItemsById(orderId.value().toLong())
                .map(this.disassembler::toDomainEntity);
    }

    @Override
    public boolean exists(final OrderId orderId) {
        return this.repository.existsById(orderId.value().toLong());
    }

    @Transactional
    @Override
    public void add(final Order order) {
        this.repository.findOrderPersistenceEntityWithItemsById(order.getId().value().toLong())
                .ifPresentOrElse(
                        orderPersistenceEntity -> this.updateOrder(orderPersistenceEntity, order),
                        () -> this.insertOrder(order)
                );
    }

    @Override
    public long count() {
        return this.repository.count();
    }

    @Override
    public List<Order> placedByCustomerInYear(final CustomerId customerId, final Year year) {
//        final Instant beginningOfYearInstant = year.atDay(1).atStartOfDay().atOffset(ZoneOffset.UTC).toInstant();
//        final Instant endOfYearInstant = year.atDay(year.length()).atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC).toInstant();
        return this.repository.placedByCustomerInYear(customerId.value(), year.getValue()).stream()
                .map(this.disassembler::toDomainEntity)
                .toList();
    }

    @Override
    public long salesQuantityByCustomerInYear(final CustomerId customerId, final Year year) {
        return this.repository.salesQuantityByCustomerInYear(customerId.value(), year.getValue());
    }

    @Override
    public Money totalSoldByCustomer(final CustomerId customerId) {
        return new Money(this.repository.totalSoldByCustomer(customerId.value()));
    }

    private void updateOrder(final OrderPersistenceEntity orderPersistenceEntity, final Order order) {
        this.assembler.merge(orderPersistenceEntity, order);
        this.entityManager.detach(orderPersistenceEntity);
        this.repository.saveAndFlush(orderPersistenceEntity);
        this.updateVersion(order, orderPersistenceEntity);
    }

    private void insertOrder(final Order order) {
        final OrderPersistenceEntity orderPersistenceEntity = this.assembler.fromDomain(order);
        this.repository.saveAndFlush(orderPersistenceEntity);
        this.updateVersion(order, orderPersistenceEntity);
    }

    @SneakyThrows
    private void updateVersion(final Order order, final OrderPersistenceEntity orderEntity) {
        DomainVersionHandler.setVersion(order, orderEntity.getVersion());
    }

}
