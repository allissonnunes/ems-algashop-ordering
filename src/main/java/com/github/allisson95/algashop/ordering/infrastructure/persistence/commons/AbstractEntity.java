package com.github.allisson95.algashop.ordering.infrastructure.persistence.commons;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Transient;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;

@MappedSuperclass
public abstract class AbstractEntity<A extends AbstractAggregateRoot<A>, PK extends Serializable>
        extends AbstractAggregateRoot<A>
        implements Persistable<PK> {

    @Transient
    private boolean isNew = true;

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

}
