package br.dev.allissonnunes.algashop.ordering.core.domain.model;

import java.util.List;

public interface DomainEventSource {

    List<Object> domainEvents();

    void clearDomainEvents();

}
