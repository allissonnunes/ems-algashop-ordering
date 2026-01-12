package br.dev.allissonnunes.algashop.ordering.infrastructure.utility.mapstruct;

import br.dev.allissonnunes.algashop.ordering.application.order.query.RecipientData;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.Recipient;
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
