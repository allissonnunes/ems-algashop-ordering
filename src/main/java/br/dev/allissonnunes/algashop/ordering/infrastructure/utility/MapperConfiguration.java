package br.dev.allissonnunes.algashop.ordering.infrastructure.utility;

import br.dev.allissonnunes.algashop.ordering.application.utility.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

@Configuration
@RequiredArgsConstructor
class MapperConfiguration {

    private final ConversionService conversionService;

    @Bean
    Mapper mapper() {
        return conversionService::convert;
    }

}
