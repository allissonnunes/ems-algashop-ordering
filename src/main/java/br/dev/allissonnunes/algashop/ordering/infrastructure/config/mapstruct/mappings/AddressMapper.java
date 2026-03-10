package br.dev.allissonnunes.algashop.ordering.infrastructure.config.mapstruct.mappings;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.commons.Address;
import br.dev.allissonnunes.algashop.ordering.core.ports.commons.AddressData;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.extensions.spring.DelegatingConverter;
import org.springframework.core.convert.converter.Converter;

@Mapper
public interface AddressMapper extends Converter<Address, AddressData> {

    @Mapping(target = "zipCode", source = "zipCode.value")
    @Override
    AddressData convert(Address source);

    @InheritInverseConfiguration
    @DelegatingConverter
    Address inverseConvert(AddressData source);

}
