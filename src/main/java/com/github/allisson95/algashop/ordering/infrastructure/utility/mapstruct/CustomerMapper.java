package com.github.allisson95.algashop.ordering.infrastructure.utility.mapstruct;

import com.github.allisson95.algashop.ordering.application.customer.query.CustomerOutput;
import com.github.allisson95.algashop.ordering.domain.model.customer.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.core.convert.converter.Converter;

@Mapper(config = MapStructConfiguration.class)
public interface CustomerMapper extends Converter<Customer, CustomerOutput> {

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "firstName", source = "fullName.firstName")
    @Mapping(target = "lastName", source = "fullName.lastName")
    @Mapping(target = "birthDate", source = "birthDate.value")
    @Mapping(target = "email", source = "email.value")
    @Mapping(target = "phone", source = "phone.value")
    @Mapping(target = "document", source = "document.value")
    @Mapping(target = "loyaltyPoints", source = "loyaltyPoints.value")
    @Override
    CustomerOutput convert(Customer source);

}
