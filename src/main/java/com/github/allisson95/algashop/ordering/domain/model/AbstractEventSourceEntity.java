package com.github.allisson95.algashop.ordering.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

public abstract class AbstractEventSourceEntity implements DomainEventSource {

    protected final List<Object> domainEvents = new ArrayList<>();

    protected void registerEvent(Object event) {
        requireNonNull(event, "event cannot be null");
        domainEvents.add(event);
    }

    @Override
    public List<Object> domainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    @Override
    public void clearDomainEvents() {
        domainEvents.clear();
    }

}
