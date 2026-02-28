package br.dev.allissonnunes.algashop.ordering.core.domain.model.customer;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.DomainException;

public class CustomerArchivedException extends DomainException {

    public CustomerArchivedException() {
        super("Customer is archived and cannot be updated");
    }

}
