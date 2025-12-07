package com.github.allisson95.algashop.ordering.infrastructure.persistence.provider;

import com.github.allisson95.algashop.ordering.domain.model.entity.Order;
import com.github.allisson95.algashop.ordering.domain.model.repository.Orders;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
public class OrdersPersistenceProvider implements Orders {

    private final OrderPersistenceEntityRepository repository;

    private final OrderPersistenceEntityAssembler assembler;

    private final OrderPersistenceEntityDisassembler disassembler;

    private final EntityManager entityManager;

    @Override
    public Optional<Order> ofId(final OrderId orderId) {
        return this.repository.findById(orderId.value().toLong())
                .map(this.disassembler::toDomainEntity);
    }

    @Override
    public boolean exists(final OrderId orderId) {
        return this.repository.existsById(orderId.value().toLong());
    }

    @Override
    public void add(final Order order) {
        this.repository.findById(order.id().value().toLong())
                .ifPresentOrElse(
                        orderPersistenceEntity -> this.updateOrder(orderPersistenceEntity, order),
                        () -> this.insertOrder(order)
                );
    }

    @Override
    public long count() {
        return this.repository.count();
    }

    private void updateOrder(final OrderPersistenceEntity orderPersistenceEntity, final Order order) {
        this.assembler.merge(orderPersistenceEntity, order);
        this.entityManager.detach(orderPersistenceEntity);
        this.repository.updateAndFlush(orderPersistenceEntity);
        this.updateVersion(order, orderPersistenceEntity);
    }

    private void insertOrder(final Order order) {
        final OrderPersistenceEntity orderPersistenceEntity = this.assembler.fromDomain(order);
        this.repository.persistAndFlush(orderPersistenceEntity);
        this.updateVersion(order, orderPersistenceEntity);
    }

    @SneakyThrows
    private void updateVersion(final Order order, final OrderPersistenceEntity orderEntity) {
        final var domainVersionMethodHandler = DomainVersionMethodHandler.of(order, orderEntity.getVersion());
        ReflectionUtils.doWithMethods(Order.class, domainVersionMethodHandler, domainVersionMethodHandler);
    }

    static class DomainVersionMethodHandler implements ReflectionUtils.MethodFilter, ReflectionUtils.MethodCallback {

        private final Object target;

        private final Object version;

        private DomainVersionMethodHandler(final Object target, final Object version) {
            this.target = requireNonNull(target);
            this.version = requireNonNull(version);
        }

        static DomainVersionMethodHandler of(final Object target, final Object version) {
            return new DomainVersionMethodHandler(target, version);
        }

        @Override
        public void doWith(final @NonNull Method method) throws IllegalArgumentException, IllegalAccessException {
            ReflectionUtils.makeAccessible(method);
            ReflectionUtils.invokeMethod(method, target, version);
            method.setAccessible(false);
        }

        @Override
        public boolean matches(final Method method) {
            return method.getName().equals("setVersion") && method.getParameterCount() == 1;
        }

    }

}
