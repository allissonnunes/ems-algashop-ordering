package br.dev.allissonnunes.algashop.ordering.presentation.order;

import br.dev.allissonnunes.algashop.ordering.DataJpaCleanUpExtension;
import br.dev.allissonnunes.algashop.ordering.RestAssuredMockMvcExtension;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderId;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityRepository;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntity;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.utils.JsonFileSource;
import br.dev.allissonnunes.algashop.ordering.utils.hamcrest.AlgaShopMatchers;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;

@EnableWireMock({
        @ConfigureWireMock(
                name = "product-catalog",
                filesUnderClasspath = "wiremock/product-catalog",
                baseUrlProperties = "spring.http.serviceclient.product-catalog.base-url"
        ),
        @ConfigureWireMock(
                name = "rapidex",
                filesUnderClasspath = "wiremock/rapidex",
                baseUrlProperties = "spring.http.serviceclient.rapidex.base-url"
        )
})
@SpringBootTest(
        properties = {
                "algashop.integrations.product-catalog.provider=PRODUCT_CATALOG",
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ExtendWith({ DataJpaCleanUpExtension.class, RestAssuredMockMvcExtension.class })
class OrderControllerIT {

    @Autowired
    private CustomerPersistenceEntityRepository customerRepository;

    @Autowired
    private ShoppingCartPersistenceEntityRepository shoppingCartRepository;

    @Autowired
    private OrderPersistenceEntityRepository orderRepository;

    private final UUID customerId = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

    @BeforeEach
    void setUp() {
        final CustomerPersistenceEntity customer = CustomerPersistenceEntityTestDataBuilder.aCustomer()
                .id(customerId)
                .build();
        customerRepository.saveAndFlush(customer);
    }

    @ParameterizedTest
    @JsonFileSource(resources = "json/create-order-with-product.json")
    void shouldCreateOrderUsingProduct(final String json) {
        final MockMvcRequestSpecification request = given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json);

        final MockMvcResponse response = given().spec(request)
                .post("/api/v1/orders");

        final String createdOrderId = response.then()
                .contentType(AlgaShopMatchers.contentType(MediaType.APPLICATION_JSON))
                .status(HttpStatus.CREATED)
                .body(
                        "id", Matchers.not(Matchers.blankString()),
                        "customer.id", Matchers.equalTo(customerId.toString())
                )
                .extract()
                .jsonPath().getString("id");

        final boolean isOrderPersisted = orderRepository.existsById(new OrderId(createdOrderId).value().toLong());
        assertThat(isOrderPersisted).isTrue();
    }

    @ParameterizedTest
    @JsonFileSource(resources = "json/create-order-with-product-connection-error.json")
    void shouldNotCreateOrderUsingProductWhenProductAPIIsUnavailable(final String json) {
        final MockMvcRequestSpecification request = given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json);

        final MockMvcResponse response = given()
                .spec(request)
                .post("/api/v1/orders");

        response.then()
                .contentType(AlgaShopMatchers.contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .status(HttpStatus.GATEWAY_TIMEOUT);
    }

    @ParameterizedTest
    @JsonFileSource(resources = "json/create-order-with-product-and-invalid-customer.json")
    void shouldNotCreateOrderUsingProductWhenCustomerWasNotFound(final String json) {
        final MockMvcRequestSpecification request = given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json);

        final MockMvcResponse response = given()
                .spec(request)
                .post("/api/v1/orders");

        response.then()
                .contentType(AlgaShopMatchers.contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .status(HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @ParameterizedTest
    @JsonFileSource(resources = "json/create-order-with-not-found-product.json")
    void shouldNotCreateOrderUsingProductWhenProductWasNotFound(final String json) {
        final MockMvcRequestSpecification request = given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json);

        final MockMvcResponse response = given()
                .spec(request)
                .post("/api/v1/orders");

        response.then()
                .contentType(AlgaShopMatchers.contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .status(HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @ParameterizedTest
    @JsonFileSource(resources = "json/create-order-with-shopping-cart.json")
    void shouldCreateOrderUsingShoppingCart(final String json) {
        final ShoppingCartPersistenceEntity shoppingCart = ShoppingCartPersistenceEntityTestDataBuilder.existingShoppingCart()
                .id(UUID.fromString("28fcd9fb-4ce7-44d6-9583-14d8b3dc5aff"))
                .customer(customerRepository.getReferenceById(customerId))
                .build();
        shoppingCartRepository.saveAndFlush(shoppingCart);

        final MockMvcRequestSpecification request = given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-shopping-cart.v1+json")
                .body(json);

        final MockMvcResponse response = given()
                .spec(request)
                .post("/api/v1/orders");

        final String createdOrderId = response.then()
                .contentType(AlgaShopMatchers.contentType(MediaType.APPLICATION_JSON))
                .status(HttpStatus.CREATED)
                .body(
                        "id", Matchers.not(Matchers.blankString()),
                        "customer.id", Matchers.equalTo(customerId.toString())
                )
                .extract()
                .jsonPath().getString("id");

        final boolean isOrderPersisted = orderRepository.existsById(new OrderId(createdOrderId).value().toLong());
        assertThat(isOrderPersisted).isTrue();
    }

}
