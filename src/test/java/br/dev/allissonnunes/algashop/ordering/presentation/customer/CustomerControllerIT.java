package br.dev.allissonnunes.algashop.ordering.presentation.customer;

import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import br.dev.allissonnunes.algashop.ordering.presentation.AbstractPresentationIT;
import br.dev.allissonnunes.algashop.ordering.utils.JsonFileSource;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;

class CustomerControllerIT extends AbstractPresentationIT {

    @Autowired
    private CustomerPersistenceEntityRepository repository;

    @ParameterizedTest
    @JsonFileSource(resources = "json/create-customer.json")
    void shouldCreateCustomer(final String body) {
        // given:
        final MockMvcRequestSpecification request = given()
                .header("Content-Type", "application/json")
                .body(body);

        // when:
        final MockMvcResponse response = given().spec(request)
                .post("/api/v1/customers");

        // then:
        response.then()
                .statusCode(201)
                .header("Content-Type", Matchers.matchesPattern("application/json.*"))
                .header("Location", Matchers.matchesPattern(".+/api/v1/customers/[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"))
                .body(
                        "id", Matchers.notNullValue(),
                        "id", Matchers.matchesPattern("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")
                );

        // and:
        final UUID customerId = response.then().extract().jsonPath().getUUID("id");
        assertThat(repository.existsById(customerId)).isTrue();
    }

    @ParameterizedTest
    @JsonFileSource(resources = "json/create-customer-with-invalid-json.json")
    void shouldNotCreateCustomerWhenInvalidDataIsProvided(final String body) {
        // given:
        final MockMvcRequestSpecification request = given()
                .header("Content-Type", "application/json")
                .body(body);

        // when:
        final MockMvcResponse response = given().spec(request)
                .post("/api/v1/customers");

        // then:
        response.then()
                .statusCode(400)
                .header("Content-Type", Matchers.matchesPattern("application/problem\\+json.*"))
                .body(
                        "status", Matchers.equalTo(400),
                        "type", Matchers.equalTo("/errors/invalid-fields"),
                        "title", Matchers.equalTo("Invalid fields"),
                        "detail", Matchers.equalTo("One or more fields are invalid."),
                        "instance", Matchers.equalTo("/api/v1/customers"),
                        "fields", Matchers.notNullValue(),
                        "fields.firstName", Matchers.matchesPattern("^\\s*\\S[\\S\\s]*"),
                        "fields.lastName", Matchers.matchesPattern("^\\s*\\S[\\S\\s]*"),
                        "fields.birthDate", Matchers.matchesPattern("^\\s*\\S[\\S\\s]*"),
                        "fields.email", Matchers.matchesPattern("^\\s*\\S[\\S\\s]*"),
                        "fields.phone", Matchers.matchesPattern("^\\s*\\S[\\S\\s]*"),
                        "fields.document", Matchers.matchesPattern("^\\s*\\S[\\S\\s]*"),
                        "fields.promotionNotificationsAllowed", Matchers.matchesPattern("^\\s*\\S[\\S\\s]*"),
                        "fields.address", Matchers.matchesPattern("^\\s*\\S[\\S\\s]*")
                );
    }

    @Test
    void shouldArchiveExistingCustomer() {
        // given:
        final CustomerPersistenceEntity existingCustomer = CustomerPersistenceEntityTestDataBuilder.aCustomer().build();
        final UUID customerId = existingCustomer.getId();
        repository.saveAndFlush(existingCustomer);

        // when:
        final MockMvcResponse response = given()
                .delete("/api/v1/customers/{customerId}", customerId);

        // then:
        response.then()
                .statusCode(204);

        // and:
        assertThat(repository.existsById(customerId)).isTrue();
        assertThat(repository.findById(customerId)).
                hasValueSatisfying(customer -> assertThat(customer.getArchived()).isTrue());
    }

    @Test
    void shouldThrowExceptionIfTryToArchiveInexistentCustomer() {
        // given:
        final UUID inexistentCustomer = UUID.randomUUID();

        // when:
        final MockMvcResponse response = given()
                .delete("/api/v1/customers/{customerId}", inexistentCustomer);

        // then:
        response.then()
                .statusCode(404)
                .header("Content-Type", Matchers.matchesPattern("application/problem\\+json.*"));

        // and:
        assertThat(repository.existsById(inexistentCustomer)).isFalse();
        assertThat(repository.findById(inexistentCustomer)).isEmpty();
    }

}
