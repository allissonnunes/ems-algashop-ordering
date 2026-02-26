package br.dev.allissonnunes.algashop.ordering;

import br.dev.allissonnunes.algashop.ordering.infrastructure.utility.mapstruct.MapStructConfiguration;
import org.mapstruct.extensions.spring.test.ConverterScan;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
@ConverterScan(basePackageClasses = MapStructConfiguration.class)
public class MapStructTestConfiguration {

}
