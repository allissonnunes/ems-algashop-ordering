package br.dev.allissonnunes.algashop.ordering.core.domain.model;

public interface RemoveCapableRepository<T extends AggregateRoot<ID>, ID> extends Repository<T, ID> {

    void remove(ID id);

    void remove(T entity);

}
