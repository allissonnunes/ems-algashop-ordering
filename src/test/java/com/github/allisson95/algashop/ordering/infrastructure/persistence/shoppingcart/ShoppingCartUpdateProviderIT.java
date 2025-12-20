package com.github.allisson95.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.github.allisson95.algashop.ordering.DataJpaCleanUpExtension;
import com.github.allisson95.algashop.ordering.DataSourceProxyQueryCountConfiguration;
import com.github.allisson95.algashop.ordering.domain.model.commons.Money;
import com.github.allisson95.algashop.ordering.domain.model.commons.Quantity;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.github.allisson95.algashop.ordering.domain.model.customer.Customers;
import com.github.allisson95.algashop.ordering.domain.model.product.Product;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductId;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.github.allisson95.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.github.allisson95.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.SpringDataJpaConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

@Import({ SpringDataJpaConfiguration.class, DataSourceProxyQueryCountConfiguration.class })
@DataJpaTest(
        showSql = false,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*(Persistence)?(Provider|EntityAssembler|EntityDisassembler)"),
        }
)
@ExtendWith(DataJpaCleanUpExtension.class)
class ShoppingCartUpdateProviderIT {

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
        final Product product1 = ProductTestDataBuilder.aProduct().price(new Money("2000")).build();
        final Product product2 = ProductTestDataBuilder.aProduct().price(new Money("200")).build();
        shoppingCart.addItem(product1, new Quantity(2));
        shoppingCart.addItem(product2, new Quantity(1));
        shoppingCartsPersistenceProvider.add(shoppingCart);
        final ProductId productIdToUpdate = product1.id();
        final ProductId productIdToNotUpdate = product2.id();
        final Money newProduct1Price = new Money("1500");
        final Money expectedItemTotalPrice = newProduct1Price.multiply(new Quantity(2));
        final Money expectedCartTotalAmount = expectedItemTotalPrice.add(product2.price());

        shoppingCartUpdateProvider.adjustPrice(productIdToUpdate, newProduct1Price);

        final ShoppingCart updatedShoppingCart = shoppingCartsPersistenceProvider.ofId(shoppingCart.id()).orElseThrow();
        assertWith(updatedShoppingCart,
                sc -> assertThat(sc.totalAmount()).isEqualTo(expectedCartTotalAmount),
                sc -> assertThat(sc.totalItems()).isEqualTo(new Quantity(3))
        );
        assertWith(updatedShoppingCart.findItem(productIdToUpdate),
                i -> assertThat(i.price()).isEqualTo(newProduct1Price),
                i -> assertThat(i.totalAmount()).isEqualTo(expectedItemTotalPrice)
        );
        assertWith(updatedShoppingCart.findItem(productIdToNotUpdate),
                i -> assertThat(i.price()).isEqualTo(product2.price()),
                i -> assertThat(i.totalAmount()).isEqualTo(shoppingCart.findItem(productIdToNotUpdate).totalAmount())
        );
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void shouldUpdateItemAvailabilityAndKeepPersistenceEntityState() {
        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        final Product product1 = ProductTestDataBuilder.aProduct().inStock(true).build();
        final Product product2 = ProductTestDataBuilder.aProduct().inStock(true).build();
        shoppingCart.addItem(product1, new Quantity(2));
        shoppingCart.addItem(product2, new Quantity(1));
        shoppingCartsPersistenceProvider.add(shoppingCart);
        final ProductId productIdToUpdate = product1.id();
        final ProductId productIdToNotUpdate = product2.id();

        shoppingCartUpdateProvider.changeAvailability(productIdToUpdate, false);

        final ShoppingCart updatedShoppingCart = shoppingCartsPersistenceProvider.ofId(shoppingCart.id()).orElseThrow();
        assertWith(updatedShoppingCart.findItem(productIdToUpdate),
                i -> assertThat(i.isAvailable()).isFalse()
        );
        assertWith(updatedShoppingCart.findItem(productIdToNotUpdate),
                i -> assertThat(i.isAvailable()).isTrue()
        );
    }

}