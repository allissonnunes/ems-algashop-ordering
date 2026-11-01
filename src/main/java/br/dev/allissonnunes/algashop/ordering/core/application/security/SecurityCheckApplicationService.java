package br.dev.allissonnunes.algashop.ordering.core.application.security;

import java.util.UUID;

public interface SecurityCheckApplicationService {

    UUID getAuthenticatedUserId();

    boolean isAuthenticated();

    boolean isMachineAuthentication();

    boolean isCustomer();

}
