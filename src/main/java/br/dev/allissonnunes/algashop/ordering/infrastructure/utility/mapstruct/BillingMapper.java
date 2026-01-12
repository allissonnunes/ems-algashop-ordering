package br.dev.allissonnunes.algashop.ordering.infrastructure.utility.mapstruct;

import br.dev.allissonnunes.algashop.ordering.application.order.query.BillingData;
import br.dev.allissonnunes.algashop.ordering.domain.model.order.Billing;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.extensions.spring.DelegatingConverter;
import org.springframework.core.convert.converter.Converter;

@Mapper(config = MapStructConfiguration.class)
public interface BillingMapper extends Converter<Billing, BillingData> {

    @Mapping(target = "firstName", source = "fullName.firstName")
    @Mapping(target = "lastName", source = "fullName.lastName")
    @Mapping(target = "document", source = "document.value")
    @Mapping(target = "phone", source = "phone.value")
    @Mapping(target = "email", source = "email.value")
    @Override
    BillingData convert(Billing source);

    @InheritInverseConfiguration
    @DelegatingConverter
    Billing inverseConvert(BillingData source);

}
