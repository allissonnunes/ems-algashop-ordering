package br.dev.allissonnunes.algashop.ordering.infrastructure.adapters.out.notification.customer;

import br.dev.allissonnunes.algashop.ordering.core.ports.out.customer.ForNotifyingCustomers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class FakeForNotifyingCustomers implements ForNotifyingCustomers {

    @Override
    public void notifyNewRegistration(final NotifyNewRegistrationInput input) {
        log.info(
                "Welcome {}!\nUse your email {} to access the system.",
                input.firstName(),
                input.email()
        );
    }

}
