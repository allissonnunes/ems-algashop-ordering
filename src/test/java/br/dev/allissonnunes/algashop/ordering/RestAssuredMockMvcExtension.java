package br.dev.allissonnunes.algashop.ordering;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

public class RestAssuredMockMvcExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(final @NonNull ExtensionContext context) throws Exception {
        final ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
        if (applicationContext instanceof WebApplicationContext webApplicationContext) {
            RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
            RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
        }
    }

    @Override
    public void afterEach(final @NonNull ExtensionContext context) throws Exception {
        final ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
        if (applicationContext instanceof WebApplicationContext) {
            RestAssuredMockMvc.reset();
        }
    }

}
