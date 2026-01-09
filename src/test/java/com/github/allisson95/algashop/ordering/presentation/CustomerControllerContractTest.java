package com.github.allisson95.algashop.ordering.presentation;

import com.github.allisson95.algashop.ordering.MapStructTestConfiguration;
import com.github.allisson95.algashop.ordering.application.customer.management.CustomerInput;
import com.github.allisson95.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerFilter;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerOutputTestDataBuilder;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerQueryService;
import com.github.allisson95.algashop.ordering.application.customer.query.CustomerSummaryOutputTestDataBuilder;
import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
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
    void registerCustomerContract() {
        final UUID customerId = new CustomerId().value();
        when(customerManagementApplicationService.create(Mockito.any(CustomerInput.class)))
                .thenReturn(customerId);
        when(customerQueryService.findById(Mockito.any(UUID.class)))
                .thenReturn(CustomerOutputTestDataBuilder.existing().id(customerId).build());

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
                .header("Location", containsString("/api/v1/customers/" + customerId))
                .body(
                        "id", equalTo(customerId.toString()),
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
    void registerCustomerErrorContract() {
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

    @Test
    void getAllCustomersContract() {
        final var pageNumber = 0;
        final var pageSize = 10;
        final var c1 = CustomerSummaryOutputTestDataBuilder.existing().build();
        final var c2 = CustomerSummaryOutputTestDataBuilder.existingAlt1().build();

        when(customerQueryService.filter(Mockito.any(CustomerFilter.class)))
                .thenReturn(new PageImpl<>(List.of(c1, c2), PageRequest.of(pageNumber, pageSize), 2));

        given()
                .webAppContextSetup(context)
                .accept(MediaType.APPLICATION_JSON)
                .queryParam("page", pageNumber)
                .queryParam("size", pageSize)
                .when()
                .get("/api/v1/customers")
                .then()
                .log().ifValidationFails()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body(
                        "content", hasSize(2),
                        "number", equalTo(pageNumber),
                        "size", equalTo(pageSize),
                        "totalPages", equalTo(1),
                        "totalElements", equalTo(2)
                )
                .body(
                        "content[0].id", equalTo(c1.id().toString()),
                        "content[0].firstName", equalTo(c1.firstName()),
                        "content[0].lastName", equalTo(c1.lastName()),
                        "content[0].email", equalTo(c1.email()),
                        "content[0].document", equalTo(c1.document()),
                        "content[0].phone", equalTo(c1.phone()),
                        "content[0].birthDate", equalTo(c1.birthDate().toString()),
                        "content[0].loyaltyPoints", equalTo(c1.loyaltyPoints()),
                        "content[0].registeredAt", equalTo(c1.registeredAt().toString()),
                        "content[0].archivedAt", equalTo(c1.archivedAt()),
                        "content[0].promotionNotificationsAllowed", equalTo(c1.promotionNotificationsAllowed()),
                        "content[0].archived", equalTo(c1.archived())
                )
                .body(
                        "content[1].id", equalTo(c2.id().toString()),
                        "content[1].firstName", equalTo(c2.firstName()),
                        "content[1].lastName", equalTo(c2.lastName()),
                        "content[1].email", equalTo(c2.email()),
                        "content[1].document", equalTo(c2.document()),
                        "content[1].phone", equalTo(c2.phone()),
                        "content[1].birthDate", equalTo(c2.birthDate().toString()),
                        "content[1].loyaltyPoints", equalTo(c2.loyaltyPoints()),
                        "content[1].registeredAt", equalTo(c2.registeredAt().toString()),
                        "content[1].archivedAt", equalTo(c2.archivedAt()),
                        "content[1].promotionNotificationsAllowed", equalTo(c2.promotionNotificationsAllowed()),
                        "content[1].archived", equalTo(c2.archived())
                );
    }

    @Test
    void getCustomerByIdContract() {
        final var customerId = new CustomerId().value();
        final var customer = CustomerOutputTestDataBuilder.existing().id(customerId).build();

        when(customerQueryService.findById(Mockito.any(UUID.class)))
                .thenReturn(customer);

        given()
                .webAppContextSetup(context)
                .accept(MediaType.APPLICATION_JSON)
                .pathParam("id", customerId)
                .when()
                .get("/api/v1/customers/{id}")
                .then()
                .log().ifValidationFails()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body(
                        "id", equalTo(customerId.toString()),
                        "firstName", equalTo(customer.firstName()),
                        "lastName", equalTo(customer.lastName()),
                        "email", equalTo(customer.email()),
                        "document", equalTo(customer.document()),
                        "phone", equalTo(customer.phone()),
                        "birthDate", equalTo(customer.birthDate().toString()),
                        "loyaltyPoints", equalTo(customer.loyaltyPoints()),
                        "registeredAt", equalTo(customer.registeredAt().toString()),
                        "archivedAt", equalTo(customer.archivedAt()),
                        "promotionNotificationsAllowed", equalTo(customer.promotionNotificationsAllowed()),
                        "archived", equalTo(customer.archived()),
                        "address", notNullValue(),
                        "address.street", equalTo(customer.address().street()),
                        "address.number", equalTo(customer.address().number()),
                        "address.complement", equalTo(customer.address().complement()),
                        "address.neighborhood", equalTo(customer.address().neighborhood()),
                        "address.city", equalTo(customer.address().city()),
                        "address.state", equalTo(customer.address().state()),
                        "address.zipCode", equalTo(customer.address().zipCode())
                );

    }

}