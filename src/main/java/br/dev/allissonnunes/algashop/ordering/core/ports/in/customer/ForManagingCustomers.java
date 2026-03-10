package br.dev.allissonnunes.algashop.ordering.core.ports.in.customer;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface ForManagingCustomers {

    @Transactional
    UUID create(CustomerInput input);

    @Transactional
    void update(UUID rawCustomerId, CustomerUpdateInput input);

    @Transactional
    void archive(UUID rawCustomerId);

    @Transactional
    void changeEmail(UUID rawCustomerId, String newEmail);

}
