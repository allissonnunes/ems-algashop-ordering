package com.github.allisson95.algashop.ordering.domain.model.shoppingcart;

import com.github.allisson95.algashop.ordering.DataJpaCleanUpExtension;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerId;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.github.allisson95.algashop.ordering.domain.model.customer.Customers;
import com.github.allisson95.algashop.ordering.infrastructure.persistence.SpringDataJpaConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Import(SpringDataJpaConfiguration.class)
@DataJpaTest(
        showSql = false,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*Persistence(Provider|EntityAssembler|EntityDisassembler)"),
        }
)
@ExtendWith(DataJpaCleanUpExtension.class)
class ShoppingCartsIT {

    @Autowired
    private ShoppingCarts shoppingCarts;

    @Autowired
    private Customers customers;

    @BeforeEach
    void setUp() {
        customers.add(CustomerTestDataBuilder.existingCustomer().build());
    }

    @Test
    void shouldPersistAndFindShoppingCart() {
        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        shoppingCarts.add(shoppingCart);

        final Optional<ShoppingCart> possibleShoppingCart = shoppingCarts.ofId(shoppingCart.getId());

        assertThat(possibleShoppingCart).isPresent();
        final ShoppingCart actual = possibleShoppingCart.get();

        assertWith(actual,
                sc -> assertThat(sc).usingRecursiveComparison().ignoringFields("createdAt", "items").isEqualTo(shoppingCart),
                sc -> assertThatCollection(sc.getItems())
                        .containsExactlyInAnyOrderElementsOf(shoppingCart.getItems()),
                sc -> assertThatCollection(sc.getItems())
                        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("version")
                        .containsExactlyInAnyOrderElementsOf(shoppingCart.getItems())
        );
//        var actualById = actual.items().stream().collect(toMap(ShoppingCartItem::id, identity()));
//        var expectedById = shoppingCart.items().stream().collect(toMap(ShoppingCartItem::id, identity()));
//
//        assertThat(actualById).hasSameSizeAs(expectedById);
//
//        expectedById.forEach((id, expectedItem) ->
//                assertThat(actualById.get(id))
//                        .as("ShoppingCartItem id=%s", id)
//                        .usingRecursiveComparison()
//                        .isEqualTo(expectedItem)
//        );

        assertThat(shoppingCarts.ofId(new ShoppingCartId())).isEmpty();
    }

    @Test
    void shouldFindShoppingCartByCustomerId() {
        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        shoppingCarts.add(shoppingCart);

        Optional<ShoppingCart> possibleShoppingCart = shoppingCarts.ofCustomer(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID);

        assertThat(possibleShoppingCart).isPresent();
        assertWith(possibleShoppingCart.get(),
                sc -> assertThat(sc).isEqualTo(shoppingCart),
                sc -> assertThatCollection(sc.getItems()).isNotEmpty(),
                sc -> assertThatCollection(sc.getItems()).isEqualTo(shoppingCart.getItems())
        );

        assertThat(shoppingCarts.ofCustomer(new CustomerId())).isEmpty();
    }

    @Test
    void shouldVerifyIfShoppingCartExistsByCustomerId() {
        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        shoppingCarts.add(shoppingCart);

        boolean exists = shoppingCarts.existsByCustomer(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID);
        assertThat(exists).isTrue();

        exists = shoppingCarts.existsByCustomer(new CustomerId());
        assertThat(exists).isFalse();
    }

    @Test
    void shouldRemoveShoppingCart() {
        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        shoppingCarts.add(shoppingCart);
        assertThat(shoppingCarts.ofId(shoppingCart.getId())).isPresent();

        shoppingCarts.remove(shoppingCart.getId());

        assertThat(shoppingCarts.ofId(shoppingCart.getId())).isEmpty();
    }

    @Test
    void shouldUpdateShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        shoppingCarts.add(shoppingCart);

        shoppingCart = shoppingCarts.ofId(shoppingCart.getId()).orElseThrow();
        assertWith(shoppingCart,
                sc -> assertThatCollection(sc.getItems()).isNotEmpty()
        );

        shoppingCart.empty();

        shoppingCarts.add(shoppingCart);

        assertWith(shoppingCarts.ofId(shoppingCart.getId()).orElseThrow(),
                sc -> assertThatCollection(sc.getItems()).isEmpty()
        );
    }

}
