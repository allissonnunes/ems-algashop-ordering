package br.dev.allissonnunes.algashop.ordering.infrastructure.config.mapstruct.mappings;

import org.mapstruct.MapperConfig;
import org.mapstruct.extensions.spring.SpringMapperConfig;

@MapperConfig(uses = ConversionServiceAdapter.class)
@SpringMapperConfig
public interface MapStructConfiguration {

}
