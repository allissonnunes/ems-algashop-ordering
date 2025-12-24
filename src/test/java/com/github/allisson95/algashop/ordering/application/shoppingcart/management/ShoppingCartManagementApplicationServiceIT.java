package com.github.allisson95.algashop.ordering.application.shoppingcart.management;

import com.github.allisson95.algashop.ordering.DataJpaCleanUpExtension;
import com.github.allisson95.algashop.ordering.domain.model.commons.Quantity;
import com.github.allisson95.algashop.ordering.domain.model.customer.*;
import com.github.allisson95.algashop.ordering.domain.model.product.*;
import com.github.allisson95.algashop.ordering.domain.model.shoppingcart.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(DataJpaCleanUpExtension.class)
class ShoppingCartManagementApplicationServiceIT {

    @Autowired
    private ShoppingCartManagementApplicationService service;

    @Autowired
    private Customers customers;

    @Autowired
    private ShoppingCarts shoppingCarts;

    @MockitoBean
    private ProductCatalogService productCatalogService;

    @Nested
    class CreateShoppingCartIT {

        @Test
        void shouldCreateShoppingCart() {
            final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
            customers.add(customer);

            final var shoppingCartId = service.createNew(customer.getId().value());

            assertThat(shoppingCartId).isNotNull();
        }

        @Test
        void shouldThrowExceptionIfTryToCreateShoppingCartWithNonExistingCustomer() {
            assertThatExceptionOfType(CustomerNotFoundException.class)
                    .isThrownBy(() -> service.createNew(new CustomerId().value()));
        }

        @Test
        void shouldThrowExceptionIfTryToCreateShoppingCartWithExistingCartToSameCustomer() {
            final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
            customers.add(customer);
            service.createNew(customer.getId().value());

            assertThatExceptionOfType(CustomerAlreadyHaveShoppingCartException.class)
                    .isThrownBy(() -> service.createNew(customer.getId().value()));
        }

    }

    @Nested
    class AddItemToShoppingCartIT {

        @Test
        void shouldAddItemToShoppingCart() {
            final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
            customers.add(customer);
            final var shoppingCartId = service.createNew(customer.getId().value());
            final Product product = ProductTestDataBuilder.aProduct().build();
            when(productCatalogService.ofId(product.id())).thenReturn(Optional.of(product));

            final ShoppingCartItemInput item = ShoppingCartItemInput.builder()
                    .productId(product.id().value())
                    .quantity(1)
                    .shoppingCartId(shoppingCartId)
                    .build();

            service.addItem(item);

            final ShoppingCart shoppingCart = shoppingCarts.ofId(new ShoppingCartId(shoppingCartId)).orElseThrow();
            assertThat(shoppingCart.getItems()).hasSize(1);
            assertWith(shoppingCart.getItems().iterator().next(),
                    sci -> assertThat(sci.getProductId()).isEqualTo(product.id()),
                    sci -> assertThat(sci.getProductName()).isEqualTo(product.name()),
                    sci -> assertThat(sci.getPrice()).isEqualTo(product.price()),
                    sci -> assertThat(sci.getQuantity()).isEqualTo(new Quantity(1))
            );
        }

        @Test
        void shouldThrowExceptionWhenTryToAddItemToShoppingCartThatDoesNotExist() {
            final ShoppingCartId shoppingCartId = new ShoppingCartId();
            final Product product = ProductTestDataBuilder.aProduct().build();

            final ShoppingCartItemInput item = ShoppingCartItemInput.builder()
                    .productId(product.id().value())
                    .quantity(1)
                    .shoppingCartId(shoppingCartId.value())
                    .build();

            assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                    .isThrownBy(() -> service.addItem(item));
        }

        @Test
        void shouldThrowExceptionWhenTryToAddItemWithProductInexistentToShoppingCart() {
            final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
            customers.add(customer);
            final var shoppingCartId = service.createNew(customer.getId().value());
            final Product product = ProductTestDataBuilder.aProduct().build();

            final ShoppingCartItemInput item = ShoppingCartItemInput.builder()
                    .productId(product.id().value())
                    .quantity(1)
                    .shoppingCartId(shoppingCartId)
                    .build();

            assertThatExceptionOfType(ProductNotFoundException.class)
                    .isThrownBy(() -> service.addItem(item));
        }

        @Test
        void shouldThrowExceptionWhenTryToAddItemWithUnavailableProductToShoppingCart() {
            final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
            customers.add(customer);
            final var shoppingCartId = service.createNew(customer.getId().value());
            final Product product = ProductTestDataBuilder.anOutOfStockProduct().build();
            when(productCatalogService.ofId(product.id())).thenReturn(Optional.of(product));

            final ShoppingCartItemInput item = ShoppingCartItemInput.builder()
                    .productId(product.id().value())
                    .quantity(1)
                    .shoppingCartId(shoppingCartId)
                    .build();

            assertThatExceptionOfType(ProductOutOfStockException.class)
                    .isThrownBy(() -> service.addItem(item));
        }

    }

    @Nested
    class RemoveItemToShoppingCartIT {

        @Test
        void shouldRemoveItemToShoppingCart() {
            final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
            customers.add(customer);
            final var shoppingCartId = service.createNew(customer.getId().value());
            final Product product = ProductTestDataBuilder.aProduct().build();
            when(productCatalogService.ofId(product.id())).thenReturn(Optional.of(product));
            final ShoppingCartItemInput item = ShoppingCartItemInput.builder()
                    .productId(product.id().value())
                    .quantity(1)
                    .shoppingCartId(shoppingCartId)
                    .build();
            service.addItem(item);
            ShoppingCart shoppingCart = shoppingCarts.ofId(new ShoppingCartId(shoppingCartId)).orElseThrow();
            assertThat(shoppingCart.getItems()).hasSize(1);
            final UUID rawShoppingCartItemId = shoppingCart.getItems().iterator().next().getId().value();

            service.removeItem(shoppingCartId, rawShoppingCartItemId);

            shoppingCart = shoppingCarts.ofId(new ShoppingCartId(shoppingCartId)).orElseThrow();
            assertThat(shoppingCart.getItems()).isEmpty();
        }

        @Test
        void shouldThrowExceptionWhenTryToRemoveItemToShoppingCartThatDoesNotExist() {
            final var rawShoppingCartId = new ShoppingCartId().value();
            final var rawShoppingCartItemId = new ShoppingCartItemId().value();

            assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                    .isThrownBy(() -> service.removeItem(rawShoppingCartId, rawShoppingCartItemId));
        }

        @Test
        void shouldThrowExceptionWhenTryToRemoveItemThatDoesNotExists() {
            final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
            customers.add(customer);
            final var shoppingCartId = service.createNew(customer.getId().value());
            final Product product = ProductTestDataBuilder.aProduct().build();
            when(productCatalogService.ofId(product.id())).thenReturn(Optional.of(product));
            final ShoppingCartItemInput item = ShoppingCartItemInput.builder()
                    .productId(product.id().value())
                    .quantity(1)
                    .shoppingCartId(shoppingCartId)
                    .build();
            service.addItem(item);
            ShoppingCart shoppingCart = shoppingCarts.ofId(new ShoppingCartId(shoppingCartId)).orElseThrow();
            assertThat(shoppingCart.getItems()).hasSize(1);
            final var rawShoppingCartItemId = new ShoppingCartId().value();

            assertThatExceptionOfType(ShoppingCartDoesNotContainItemException.class)
                    .isThrownBy(() -> service.removeItem(shoppingCartId, rawShoppingCartItemId));

            shoppingCart = shoppingCarts.ofId(new ShoppingCartId(shoppingCartId)).orElseThrow();
            assertThat(shoppingCart.getItems()).hasSize(1);
        }

    }

    @Nested
    class EmptyShoppingCartIT {

        @Test
        void shouldEmptyShoppingCart() {
            final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
            customers.add(customer);
            final var rawShoppingCartId = service.createNew(customer.getId().value());
            final Product product = ProductTestDataBuilder.aProduct().build();
            when(productCatalogService.ofId(product.id())).thenReturn(Optional.of(product));
            final ShoppingCartItemInput item = ShoppingCartItemInput.builder()
                    .productId(product.id().value())
                    .quantity(1)
                    .shoppingCartId(rawShoppingCartId)
                    .build();
            service.addItem(item);
            ShoppingCart shoppingCart = shoppingCarts.ofId(new ShoppingCartId(rawShoppingCartId)).orElseThrow();
            assertThat(shoppingCart.getItems()).hasSize(1);

            service.empty(rawShoppingCartId);

            shoppingCart = shoppingCarts.ofId(new ShoppingCartId(rawShoppingCartId)).orElseThrow();
            assertThat(shoppingCart.getItems()).isEmpty();
        }

        @Test
        void shouldThrowExceptionWhenTryToEmptyShoppingCartThatDoesNotExist() {
            final var rawShoppingCartId = new ShoppingCartId().value();

            assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                    .isThrownBy(() -> service.empty(rawShoppingCartId));
        }

    }

    @Nested
    class DeleteShoppingCartIT {

        @Test
        void shouldDeleteShoppingCart() {
            final Customer customer = CustomerTestDataBuilder.existingCustomer().build();
            customers.add(customer);
            final var rawShoppingCartId = service.createNew(customer.getId().value());
            assertThat(shoppingCarts.ofId(new ShoppingCartId(rawShoppingCartId))).isPresent();

            service.delete(rawShoppingCartId);

            assertThat(shoppingCarts.ofId(new ShoppingCartId(rawShoppingCartId))).isNotPresent();
        }

        @Test
        void shouldThrowExceptionWhenTryToDeleteShoppingCartThatDoesNotExist() {
            final var rawShoppingCartId = new ShoppingCartId().value();

            assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                    .isThrownBy(() -> service.delete(rawShoppingCartId));
        }

    }

}