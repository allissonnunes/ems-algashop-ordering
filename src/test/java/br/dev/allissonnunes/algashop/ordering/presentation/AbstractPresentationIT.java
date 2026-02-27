package br.dev.allissonnunes.algashop.ordering.presentation;

import br.dev.allissonnunes.algashop.ordering.DataJpaCleanUpExtension;
import br.dev.allissonnunes.algashop.ordering.RestAssuredMockMvcExtension;
import br.dev.allissonnunes.algashop.ordering.TestcontainersConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

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
@ExtendWith({ DataJpaCleanUpExtension.class, RestAssuredMockMvcExtension.class })
@Import(TestcontainersConfiguration.class)
@SpringBootTest(
        properties = {
                "algashop.integrations.product-catalog.provider=PRODUCT_CATALOG",
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public abstract class AbstractPresentationIT {

}
