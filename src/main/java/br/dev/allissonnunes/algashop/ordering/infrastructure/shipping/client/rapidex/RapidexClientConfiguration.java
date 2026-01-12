package br.dev.allissonnunes.algashop.ordering.infrastructure.shipping.client.rapidex;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@ImportHttpServices(group = "rapidex", types = RapidexClient.class)
@Configuration
class RapidexClientConfiguration {

}
