package br.dev.allissonnunes.algashop.ordering.core.domain.model;

public interface AggregateRoot<ID> extends DomainEventSource {

    ID getId();

}
