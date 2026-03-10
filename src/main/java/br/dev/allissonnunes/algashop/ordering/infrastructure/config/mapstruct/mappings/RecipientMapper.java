package br.dev.allissonnunes.algashop.ordering.infrastructure.config.mapstruct.mappings;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.order.Recipient;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.order.RecipientData;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.extensions.spring.DelegatingConverter;
import org.springframework.core.convert.converter.Converter;

@Mapper(config = MapStructConfiguration.class)
public interface RecipientMapper extends Converter<Recipient, RecipientData> {

    @Mapping(target = "firstName", source = "fullName.firstName")
    @Mapping(target = "lastName", source = "fullName.lastName")
    @Mapping(target = "document", source = "document.value")
    @Mapping(target = "phone", source = "phone.value")
    @Override
    RecipientData convert(Recipient source);

    @InheritInverseConfiguration
    @DelegatingConverter
    Recipient inverseConvert(RecipientData source);

}
