package br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Quantity;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.Customers;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.Product;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductId;
import br.dev.allissonnunes.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.infrastructure.AbstractInfrastructureIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

class ShoppingCartUpdateProviderIT extends AbstractInfrastructureIT {

    @Autowired
    private Customers customers;

    @Autowired
    private ShoppingCartsPersistenceProvider shoppingCartsPersistenceProvider;

    @Autowired
    private ShoppingCartUpdateProvider shoppingCartUpdateProvider;

    @BeforeEach
    void setUp() {
        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void shouldUpdateItemPriceAndTotalAmountAndKeepPersistenceEntityState() {
        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        final Product product1 = ProductTestDataBuilder.anAltProduct().price(new Money("2000")).build();
        final Product product2 = ProductTestDataBuilder.anAltProduct().price(new Money("200")).build();
        shoppingCart.addItem(product1, new Quantity(2));
        shoppingCart.addItem(product2, new Quantity(1));
        shoppingCartsPersistenceProvider.add(shoppingCart);
        final ProductId productIdToUpdate = product1.id();
        final ProductId productIdToNotUpdate = product2.id();
        final Money newProduct1Price = new Money("1500");
        final Money expectedItemTotalPrice = newProduct1Price.multiply(new Quantity(2));
        final Money expectedCartTotalAmount = expectedItemTotalPrice.add(product2.price());

        shoppingCartUpdateProvider.adjustPrice(productIdToUpdate, newProduct1Price);

        final ShoppingCart updatedShoppingCart = shoppingCartsPersistenceProvider.ofId(shoppingCart.getId()).orElseThrow();
        assertWith(updatedShoppingCart,
                sc -> assertThat(sc.getTotalAmount()).isEqualTo(expectedCartTotalAmount),
                sc -> assertThat(sc.getTotalItems()).isEqualTo(new Quantity(3))
        );
        assertWith(updatedShoppingCart.findItem(productIdToUpdate),
                i -> assertThat(i.getPrice()).isEqualTo(newProduct1Price),
                i -> assertThat(i.getTotalAmount()).isEqualTo(expectedItemTotalPrice)
        );
        assertWith(updatedShoppingCart.findItem(productIdToNotUpdate),
                i -> assertThat(i.getPrice()).isEqualTo(product2.price()),
                i -> assertThat(i.getTotalAmount()).isEqualTo(shoppingCart.findItem(productIdToNotUpdate).getTotalAmount())
        );
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void shouldUpdateItemAvailabilityAndKeepPersistenceEntityState() {
        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        final Product product1 = ProductTestDataBuilder.anAltProduct().inStock(true).build();
        final Product product2 = ProductTestDataBuilder.anAltProduct().inStock(true).build();
        shoppingCart.addItem(product1, new Quantity(2));
        shoppingCart.addItem(product2, new Quantity(1));
        shoppingCartsPersistenceProvider.add(shoppingCart);
        final ProductId productIdToUpdate = product1.id();
        final ProductId productIdToNotUpdate = product2.id();

        shoppingCartUpdateProvider.changeAvailability(productIdToUpdate, false);

        final ShoppingCart updatedShoppingCart = shoppingCartsPersistenceProvider.ofId(shoppingCart.getId()).orElseThrow();
        assertWith(updatedShoppingCart.findItem(productIdToUpdate),
                i -> assertThat(i.getAvailable()).isFalse()
        );
        assertWith(updatedShoppingCart.findItem(productIdToNotUpdate),
                i -> assertThat(i.getAvailable()).isTrue()
        );
    }

}