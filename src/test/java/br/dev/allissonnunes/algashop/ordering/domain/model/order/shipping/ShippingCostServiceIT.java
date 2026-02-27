package br.dev.allissonnunes.algashop.ordering.domain.model.order.shipping;

import br.dev.allissonnunes.algashop.ordering.MapStructTestConfiguration;
import br.dev.allissonnunes.algashop.ordering.TestcontainersConfiguration;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Money;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.ZipCode;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@EnableWireMock({
        @ConfigureWireMock(
                name = "rapidex",
                filesUnderClasspath = "wiremock/rapidex",
                baseUrlProperties = "spring.http.serviceclient.rapidex.base-url"
        )
})
@Import({ TestcontainersConfiguration.class, MapStructTestConfiguration.class })
@SpringBootTest(
        properties = {
                "algashop.integrations.shipping.provider=RAPIDEX"
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class ShippingCostServiceIT {

    @Autowired
    private ShippingCostService shippingCostService;

    @Autowired
    private OriginAddressService originAddressService;

    @Test
    void shouldCalculate() {
        final Money expectedCost = new Money("35.00");
        final LocalDate expectedDeliveryDate = LocalDate.now().plusDays(7);
        final ZipCode origin = originAddressService.originAddress().zipCode();
        final ZipCode destination = new ZipCode("12345");

        final var calculate = shippingCostService
                .calculate(new CalculationRequest(origin, destination));

        assertThat(calculate.cost()).isEqualTo(expectedCost);
        assertThat(calculate.estimatedDeliveryDate()).isEqualTo(expectedDeliveryDate);
    }

}