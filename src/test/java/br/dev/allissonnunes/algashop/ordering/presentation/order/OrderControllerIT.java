package br.dev.allissonnunes.algashop.ordering.presentation.order;

import br.dev.allissonnunes.algashop.ordering.DataJpaCleanUpExtension;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.OrderId;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityRepository;
import br.dev.allissonnunes.algashop.ordering.utils.JsonFileSource;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(DataJpaCleanUpExtension.class)
class OrderControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private CustomerPersistenceEntityRepository customerRepository;

    @Autowired
    private OrderPersistenceEntityRepository orderRepository;

    private final UUID customerId = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        final JsonConfig jsonConfig = JsonConfig.jsonConfig()
                .numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL);
        final EncoderConfig encoderConfig = EncoderConfig.encoderConfig()
                .defaultContentCharset(StandardCharsets.UTF_8);

        RestAssured.config = RestAssured.config()
                .encoderConfig(encoderConfig)
                .and()
                .jsonConfig(jsonConfig);

        initializeDatabase();
    }

    private void initializeDatabase() {
        customerRepository.saveAndFlush(CustomerPersistenceEntityTestDataBuilder.aCustomer().id(customerId).build());
    }

    @ParameterizedTest
    @JsonFileSource(resources = "json/create-order-with-product.json")
    void shouldCreateOrderUsingProduct(final String json) {
        final RequestSpecification request = RestAssured.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json);

        final Response response = RestAssured.given()
                .spec(request)
                .post("/api/v1/orders");

        final String createdOrderId = response.then()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
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
        final RequestSpecification request = RestAssured.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json);

        final Response response = RestAssured.given()
                .spec(request)
                .post("/api/v1/orders");

        response.then()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.GATEWAY_TIMEOUT.value());
    }

    @ParameterizedTest
    @JsonFileSource(resources = "json/create-order-with-product-and-invalid-customer.json")
    void shouldNotCreateOrderUsingProductWhenCustomerWasNotFound(final String json) {
        final RequestSpecification request = RestAssured.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json);

        final Response response = RestAssured.given()
                .spec(request)
                .post("/api/v1/orders");

        response.then()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_CONTENT.value());
    }

    @ParameterizedTest
    @JsonFileSource(resources = "json/create-order-with-not-found-product.json")
    void shouldNotCreateOrderUsingProductWhenProductWasNotFound(final String json) {
        final RequestSpecification request = RestAssured.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json);

        final Response response = RestAssured.given()
                .spec(request)
                .post("/api/v1/orders");

        response.then()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_CONTENT.value());
    }

}
