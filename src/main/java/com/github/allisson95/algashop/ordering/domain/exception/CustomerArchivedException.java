package com.github.allisson95.algashop.ordering.domain.exception;

public class CustomerArchivedException extends DomainException {

    public CustomerArchivedException() {
        super("Customer is archived and cannot be updated");
    }

}
