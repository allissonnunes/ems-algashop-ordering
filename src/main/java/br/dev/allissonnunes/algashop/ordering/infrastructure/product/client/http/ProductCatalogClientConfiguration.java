package br.dev.allissonnunes.algashop.ordering.infrastructure.product.client.http;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@ImportHttpServices(group = "product-catalog", types = ProductCatalogClient.class)
@Configuration
class ProductCatalogClientConfiguration {

}
