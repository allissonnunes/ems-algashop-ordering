package br.dev.allissonnunes.algashop.ordering.infrastructure.listener.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCartCreatedEvent;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCartEmptiedEvent;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemAddedEvent;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemRemovedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ShoppingCartEventListener {

    @EventListener
    public void listen(final ShoppingCartCreatedEvent event) {
        log.info("Shopping cart created event received: {}", event);
    }

    @EventListener
    public void listen(final ShoppingCartEmptiedEvent event) {
        log.info("Shopping cart emptied event received: {}", event);
    }

    @EventListener
    public void listen(final ShoppingCartItemAddedEvent event) {
        log.info("Shopping cart item added event received: {}", event);
    }

    @EventListener
    public void listen(final ShoppingCartItemRemovedEvent event) {
        log.info("Shopping cart item removed event received: {}", event);
    }

}
