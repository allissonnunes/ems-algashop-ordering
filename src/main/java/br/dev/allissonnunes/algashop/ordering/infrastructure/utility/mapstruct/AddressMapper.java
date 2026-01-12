package br.dev.allissonnunes.algashop.ordering.infrastructure.utility.mapstruct;

import br.dev.allissonnunes.algashop.ordering.application.commons.AddressData;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Address;
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
