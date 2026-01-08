package com.github.allisson95.algashop.ordering.presentation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.hamcrest.Matchers.*;

class CustomerControllerContractTest {

    @BeforeAll
    static void setUpAll() {
        standaloneSetup(new CustomerController());
        enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void createCustomerContract() {
        given()
                .accept(MediaType.APPLICATION_JSON)
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
                )
        ;
    }

}