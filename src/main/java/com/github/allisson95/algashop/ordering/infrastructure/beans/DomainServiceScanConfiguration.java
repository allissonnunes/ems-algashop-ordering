package com.github.allisson95.algashop.ordering.infrastructure.beans;

import com.github.allisson95.algashop.ordering.domain.model.utility.DomainService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
        basePackages = "com.github.allisson95.algashop.ordering.domain.model.service",
        includeFilters = {
                @ComponentScan.Filter(DomainService.class)
        }
)
class DomainServiceScanConfiguration {

}
