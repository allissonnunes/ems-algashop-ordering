package br.dev.allissonnunes.algashop.ordering.core.application.customer;

import br.dev.allissonnunes.algashop.ordering.core.ports.in.customer.CustomerOutput;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.customer.ForConfirmCustomerRegistration;
import br.dev.allissonnunes.algashop.ordering.core.ports.out.customer.ForNotifyingCustomers;
import br.dev.allissonnunes.algashop.ordering.core.ports.out.customer.ForNotifyingCustomers.NotifyNewRegistrationInput;
import br.dev.allissonnunes.algashop.ordering.core.ports.out.customer.ForObtainingCustomers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class CustomerRegistrationConfirmationApplicationService implements ForConfirmCustomerRegistration {

    private final ForNotifyingCustomers forNotifyingCustomers;

    private final ForObtainingCustomers forObtainingCustomers;

    @Override
    public void confirm(final UUID rawCustomerId) {
        final CustomerOutput customerOutput = forObtainingCustomers.findById(rawCustomerId);
        final NotifyNewRegistrationInput input = new NotifyNewRegistrationInput(
                customerOutput.id(),
                customerOutput.firstName(),
                customerOutput.email()
        );
        forNotifyingCustomers.notifyNewRegistration(input);
    }

}
