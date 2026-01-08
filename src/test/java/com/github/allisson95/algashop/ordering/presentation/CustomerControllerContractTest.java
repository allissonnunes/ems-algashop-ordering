package com.github.allisson95.algashop.ordering.presentation;

import com.github.allisson95.algashop.ordering.MapStructTestConfiguration;
import com.github.allisson95.algashop.ordering.application.customer.management.CustomerInput;
import com.github.allisson95.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerOutputTestDataBuilder;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerQueryService;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@Import(MapStructTestConfiguration.class)
@WebMvcTest(CustomerController.class)
class CustomerControllerContractTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private CustomerManagementApplicationService customerManagementApplicationService;

    @MockitoBean
    private CustomerQueryService customerQueryService;

    @Test
    void createCustomerContract() {
        when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
                .thenReturn(new CustomerId().value());
        when(customerQueryService.findById(Mockito.any(UUID.class)))
                .thenReturn(CustomerOutputTestDataBuilder.existing().build());

        given()
                .webAppContextSetup(context)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_PROBLEM_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "firstName": "John",
                          "lastName": "Doe",
                          "email": "john.doe@example.com",
                          "document": "12345",
                          "phone": "+1234567890",
                          "birthDate": "1980-01-01",
                          "promotionNotificationsAllowed": true,
                          "address": {
                            "street": "123 Main Street",
                            "number": "123",
                            "complement": "Apt 4B",
                            "neighborhood": "Central Park",
                            "city": "New York",
                            "state": "NY",
                            "zipCode": "10001"
                          }
                        }
                        """)
                .when()
                .post("/api/v1/customers")
                .then()
                .log().ifValidationFails()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .body(
                        "id", notNullValue(),
                        "firstName", equalTo("John"),
                        "lastName", equalTo("Doe"),
                        "email", equalTo("john.doe@example.com"),
                        "document", equalTo("12345"),
                        "phone", equalTo("+1234567890"),
                        "birthDate", equalTo("1980-01-01"),
                        "loyaltyPoints", equalTo(0),
                        "registeredAt", notNullValue(),
                        "archivedAt", nullValue(),
                        "promotionNotificationsAllowed", equalTo(true),
                        "archived", equalTo(false),
                        "address", notNullValue(),
                        "address.street", equalTo("123 Main Street"),
                        "address.number", equalTo("123"),
                        "address.complement", equalTo("Apt 4B"),
                        "address.neighborhood", equalTo("Central Park"),
                        "address.city", equalTo("New York"),
                        "address.state", equalTo("NY"),
                        "address.zipCode", equalTo("10001")
                );
    }

    @Test
    void createCustomerErrorContract() {
        when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
                .thenReturn(new CustomerId().value());
        when(customerQueryService.findById(Mockito.any(UUID.class)))
                .thenReturn(CustomerOutputTestDataBuilder.existing().build());

        given()
                .webAppContextSetup(context)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_PROBLEM_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "firstName": null,
                          "lastName": "Doe",
                          "email": "john.doe@example.com",
                          "document": " ",
                          "phone": "+1234567890",
                          "birthDate": "1980-01-01",
                          "promotionNotificationsAllowed": true,
                          "address": {
                            "street": "123 Main Street",
                            "number": "123",
                            "complement": "Apt 4B",
                            "neighborhood": "Central Park",
                            "city": "New York",
                            "state": "NY",
                            "zipCode": "10001"
                          }
                        }
                        """)
                .when()
                .post("/api/v1/customers")
                .then()
                .log().ifValidationFails()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(
                        "detail", equalTo("One or more fields are invalid."),
                        "instance", equalTo("/api/v1/customers"),
                        "status", equalTo(400),
                        "title", equalTo("Invalid fields"),
                        "type", equalTo("/errors/invalid-fields"),
                        "fields", hasEntry("firstName", "must not be blank"),
                        "fields", hasEntry("document", "must not be blank")
                );
    }

}