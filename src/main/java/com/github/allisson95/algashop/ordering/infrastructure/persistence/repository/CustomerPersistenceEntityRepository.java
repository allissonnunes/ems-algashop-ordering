package com.github.allisson95.algashop.ordering.infrastructure.persistence.repository;

import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

import java.util.UUID;

public interface CustomerPersistenceEntityRepository extends BaseJpaRepository<CustomerPersistenceEntity, UUID> {

}