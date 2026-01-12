package br.dev.allissonnunes.algashop.ordering.domain.model;

public interface AggregateRoot<ID> extends DomainEventSource {

    ID getId();

}
