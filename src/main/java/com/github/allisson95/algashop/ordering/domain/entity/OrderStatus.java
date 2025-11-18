package com.github.allisson95.algashop.ordering.domain.entity;

import java.util.EnumSet;
import java.util.Set;

public enum OrderStatus {
    DRAFT {
        @Override
        Set<OrderStatus> allowedUpdateTo() {
            return EnumSet.of(PLACED, CANCELED);
        }
    },
    PLACED {
        @Override
        Set<OrderStatus> allowedUpdateTo() {
            return EnumSet.of(PAID, CANCELED);
        }
    },
    PAID {
        @Override
        Set<OrderStatus> allowedUpdateTo() {
            return EnumSet.of(READY, CANCELED);
        }
    },
    READY {
        @Override
        Set<OrderStatus> allowedUpdateTo() {
            return EnumSet.of(CANCELED);
        }
    },
    CANCELED {
        @Override
        Set<OrderStatus> allowedUpdateTo() {
            return EnumSet.noneOf(OrderStatus.class);
        }
    };

    abstract Set<OrderStatus> allowedUpdateTo();

    public boolean canBeUpdatedTo(final OrderStatus newStatus) {
        return this.allowedUpdateTo().contains(newStatus);
    }

    public boolean cantBeUpdatedTo(final OrderStatus newStatus) {
        return !canBeUpdatedTo(newStatus);
    }

}
