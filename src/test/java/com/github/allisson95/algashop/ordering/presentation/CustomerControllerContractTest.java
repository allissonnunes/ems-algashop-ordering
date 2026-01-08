package com.github.allisson95.algashop.ordering.presentation;

import com.github.allisson95.algashop.ordering.application.customer.management.CustomerInput;
import com.github.allisson95.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerOutputTestDataBuilder;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerQueryService;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CustomerControllerContractTest {

    private static CustomerManagementApplicationService customerManagementApplicationService;

    private static CustomerQueryService customerQueryService;

    @BeforeAll
    static void setUpAll() {
        customerManagementApplicationService = mock(CustomerManagementApplicationService.class);
        customerQueryService = mock(CustomerQueryService.class);

        standaloneSetup(new CustomerController(customerManagementApplicationService, customerQueryService));
        enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void createCustomerContract() {
        Mockito.when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
                .thenReturn(new CustomerId().value());
        Mockito.when(customerQueryService.findById(Mockito.any(UUID.class)))
                .thenReturn(CustomerOutputTestDataBuilder.existing().build());

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
                );
    }

}