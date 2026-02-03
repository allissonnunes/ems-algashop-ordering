package br.dev.allissonnunes.algashop.ordering.presentation.order;

import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.utils.JsonFileSource;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerIT {

    @LocalServerPort
    private int port;

    private static boolean databaseInitialized = false;

    @Autowired
    private CustomerPersistenceEntityRepository customerRepository;

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
        if (databaseInitialized) {
            return;
        }

        customerRepository.saveAndFlush(CustomerPersistenceEntityTestDataBuilder.aCustomer().id(customerId).build());

        databaseInitialized = true;
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

        response.then()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value());
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

}
