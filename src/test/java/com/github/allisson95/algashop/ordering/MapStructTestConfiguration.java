package com.github.allisson95.algashop.ordering;

import com.github.allisson95.algashop.ordering.infrastructure.utility.mapstruct.MapStructConfiguration;
import org.mapstruct.extensions.spring.test.ConverterScan;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
@ConverterScan(basePackageClasses = MapStructConfiguration.class)
public class MapStructTestConfiguration {

}
