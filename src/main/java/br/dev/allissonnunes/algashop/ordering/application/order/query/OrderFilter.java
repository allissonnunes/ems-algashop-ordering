package br.dev.allissonnunes.algashop.ordering.application.order.query;

import br.dev.allissonnunes.algashop.ordering.application.utility.SortablePageFilter;
import lombok.*;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderFilter extends SortablePageFilter<OrderFilter.SortType> {

    private String status;

    private String orderId;

    private UUID customerId;

    private Instant placedAtFrom;

    private Instant placedAtTo;

    private BigDecimal totalAmountFrom;

    private BigDecimal totalAmountTo;

    public OrderFilter(final int page, final int size) {
        super(page, size);
    }

    @Override
    public SortType getSortByPropertyOrDefault() {
        return getSortByProperty() == null ? SortType.PLACED_AT : getSortByProperty();
    }

    @Override
    public Sort.Direction getSortDirectionOrDefault() {
        return getSortDirection() == null ? Sort.Direction.ASC : getSortDirection();
    }

    @Getter
    @RequiredArgsConstructor
    public enum SortType {
        PLACED_AT("placedAt"),
        PAID_AT("paidAt"),
        READY_AT("readyAt"),
        CANCELED_AT("canceledAt"),
        STATUS("status");

        private final String property;

    }

}
