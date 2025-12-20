package com.github.allisson95.algashop.ordering.infrastructure.client;

import com.github.allisson95.algashop.ordering.infrastructure.client.rapidex.RapidexClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@ImportHttpServices(group = "rapidex", types = RapidexClient.class)
@Configuration
class ClientConfiguration {

}
