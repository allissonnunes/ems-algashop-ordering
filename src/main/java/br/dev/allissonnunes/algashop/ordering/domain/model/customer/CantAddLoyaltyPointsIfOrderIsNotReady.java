package br.dev.allissonnunes.algashop.ordering.domain.model.customer;

import br.dev.allissonnunes.algashop.ordering.domain.model.DomainException;

public class CantAddLoyaltyPointsIfOrderIsNotReady extends DomainException {

    public CantAddLoyaltyPointsIfOrderIsNotReady() {
        super("Cant add loyalty points if order is not ready");
    }

}
