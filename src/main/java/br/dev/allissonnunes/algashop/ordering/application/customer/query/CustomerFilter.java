package br.dev.allissonnunes.algashop.ordering.application.customer.query;

import br.dev.allissonnunes.algashop.ordering.application.utility.SortablePageFilter;
import lombok.*;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CustomerFilter extends SortablePageFilter<CustomerFilter.SortType> {

    private String email;

    private String firstName;

    public CustomerFilter(int page, int size) {
        super(page, size);
    }

    @Override
    public SortType getSortByPropertyOrDefault() {
        return getSortByProperty() == null ? SortType.REGISTERED_AT : getSortByProperty();
    }

    @Override
    public Sort.Direction getSortDirectionOrDefault() {
        return getSortDirection() == null ? Sort.Direction.ASC : getSortDirection();
    }

    @Getter
    @RequiredArgsConstructor
    public enum SortType {
        FIRST_NAME("firstName"),
        REGISTERED_AT("registeredAt");

        private final String property;
    }

}
