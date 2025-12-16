package com.github.allisson95.algashop.ordering.infrastructure.persistence.repository;

import com.github.allisson95.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerPersistenceEntityRepository extends JpaRepository<CustomerPersistenceEntity, UUID> {

    Optional<CustomerPersistenceEntity> findByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

}