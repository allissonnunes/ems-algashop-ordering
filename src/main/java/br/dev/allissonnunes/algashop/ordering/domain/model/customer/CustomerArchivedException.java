package br.dev.allissonnunes.algashop.ordering.domain.model.customer;

import br.dev.allissonnunes.algashop.ordering.domain.model.DomainException;

public class CustomerArchivedException extends DomainException {

    public CustomerArchivedException() {
        super("Customer is archived and cannot be updated");
    }

}
