package br.dev.allissonnunes.algashop.ordering.infrastructure.utility.mapstruct;

import org.mapstruct.MapperConfig;
import org.mapstruct.extensions.spring.SpringMapperConfig;

@MapperConfig(uses = ConversionServiceAdapter.class)
@SpringMapperConfig
public interface MapStructConfiguration {

}
