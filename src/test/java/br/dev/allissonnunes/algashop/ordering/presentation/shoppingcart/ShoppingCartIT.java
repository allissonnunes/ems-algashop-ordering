package br.dev.allissonnunes.algashop.ordering.presentation.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.DataJpaCleanUpExtension;
import br.dev.allissonnunes.algashop.ordering.RestAssuredMockMvcExtension;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import br.dev.allissonnunes.algashop.ordering.utils.JsonFileSource;
import br.dev.allissonnunes.algashop.ordering.utils.hamcrest.AlgaShopMatchers;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;

@EnableWireMock({
        @ConfigureWireMock(
                name = "product-catalog",
                filesUnderClasspath = "wiremock/product-catalog",
                baseUrlProperties = "spring.http.serviceclient.product-catalog.base-url"
        )
})
@SpringBootTest(
        properties = {
                "algashop.integrations.product-catalog.provider=PRODUCT_CATALOG",
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ExtendWith({ RestAssuredMockMvcExtension.class, DataJpaCleanUpExtension.class })
class ShoppingCartIT {

    @Autowired
    private ShoppingCartPersistenceEntityRepository shoppingCartRepository;

    @Autowired
    private CustomerPersistenceEntityRepository customerRepository;

    @BeforeEach
    void setUp() {
        final CustomerPersistenceEntity customer = CustomerPersistenceEntityTestDataBuilder.aCustomer()
                .id(UUID.fromString("6a6ca34c-e65c-496e-adaf-f872e1784003"))
                .build();
        customerRepository.saveAndFlush(customer);
    }

    @ParameterizedTest
    @JsonFileSource(resources = "json/create-shopping-cart.json")
    void shouldCreateShoppingCart(final String body) {
        // given:
        assertThat(shoppingCartRepository.count()).isEqualTo(0);
        final MockMvcRequestSpecification request = given()
                .contentType("application/json")
                .body(body);

        // when:
        final MockMvcResponse response = given().spec(request)
                .post("/api/v1/shopping-carts");

        // then:
        final UUID shoppingCartId = response.then()
                .statusCode(201)
                .header("Content-Type", AlgaShopMatchers.contentType(MediaType.APPLICATION_JSON))
                .body(
                        "id", Matchers.not(Matchers.blankString()),
                        "id", AlgaShopMatchers.uuid()
                )
                .extract().jsonPath().getUUID("id");

        // and:
        assertThat(shoppingCartRepository.existsById(shoppingCartId)).isTrue();
        assertThat(shoppingCartRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldThrowExceptionWhenTryToCreateShoppingCartWithInvalidData() {
        // given:
        assertThat(shoppingCartRepository.count()).isEqualTo(0);
        final MockMvcRequestSpecification request = given()
                .contentType("application/json")
                .body("""
                        {
                            "customerId": null
                        }
                        """);

        // when:
        final MockMvcResponse response = given().spec(request)
                .post("/api/v1/shopping-carts");

        // then:
        response.then()
                .statusCode(400)
                .header("Content-Type", AlgaShopMatchers.contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .body(
                        "status", Matchers.equalTo(400),
                        "type", Matchers.equalTo("/errors/invalid-fields"),
                        "title", Matchers.equalTo("Invalid fields"),
                        "detail", Matchers.equalTo("One or more fields are invalid."),
                        "instance", Matchers.equalTo("/api/v1/shopping-carts"),
                        "fields", Matchers.notNullValue(),
                        "fields.customerId", Matchers.not(Matchers.blankOrNullString())
                );

        // and:
        assertThat(shoppingCartRepository.count()).isEqualTo(0);
    }

    @Test
    void shouldAddItemToExistingShoppingCart() {
        // given:
        final UUID shoppingCartId = given()
                .contentType("application/json")
                .body("""
                        {
                          "customerId": "6a6ca34c-e65c-496e-adaf-f872e1784003"
                        }
                        """)
                .post("/api/v1/shopping-carts")
                .then()
                .extract().jsonPath().getUUID("id");

        final MockMvcRequestSpecification request = given()
                .contentType("application/json")
                .body("""
                        {
                            "productId": "019bb3a0-5c32-7685-b712-9dd8373525d3",
                            "quantity": 1
                        }
                        """);

        // when:
        final MockMvcResponse response = given().spec(request)
                .post("/api/v1/shopping-carts/{shoppingCartId}/items", shoppingCartId);

        // then:
        response.then()
                .statusCode(204);

        assertThat(shoppingCartRepository.existsById(shoppingCartId)).isTrue();
        assertThat(shoppingCartRepository.findById(shoppingCartId)).hasValueSatisfying(shoppingCart -> {
            assertThat(shoppingCart.getItems()).hasSize(1);
            assertThatCollection(shoppingCart.getItems()).first().satisfies(item -> {
                assertThat(item.getProductId()).isEqualTo(UUID.fromString("019bb3a0-5c32-7685-b712-9dd8373525d3"));
            });
        });
    }

    @Test
    void shouldThrowExceptionWhenTryAddItemToInexistentShoppingCart() {
        // given:
        final MockMvcRequestSpecification request = given()
                .contentType("application/json")
                .body("""
                        {
                            "productId": "019bb3a0-5c32-7685-b712-9dd8373525d3",
                            "quantity": 1
                        }
                        """);

        // when:
        final MockMvcResponse response = given().spec(request)
                .post("/api/v1/shopping-carts/{shoppingCartId}/items", UUID.randomUUID());

        // then:
        response.then()
                .statusCode(404);
    }

}
