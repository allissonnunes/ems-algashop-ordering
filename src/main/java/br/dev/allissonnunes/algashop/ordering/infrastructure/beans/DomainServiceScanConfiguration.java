package br.dev.allissonnunes.algashop.ordering.infrastructure.beans;

import br.dev.allissonnunes.algashop.ordering.domain.model.DomainService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
        basePackageClasses = { DomainService.class },
        includeFilters = {
                @ComponentScan.Filter(DomainService.class)
        }
)
class DomainServiceScanConfiguration {

}
