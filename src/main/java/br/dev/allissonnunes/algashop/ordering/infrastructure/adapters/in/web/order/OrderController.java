package br.dev.allissonnunes.algashop.ordering.infrastructure.adapters.in.web.order;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.DomainEntityNotFoundException;
import br.dev.allissonnunes.algashop.ordering.core.ports.in.order.*;
import br.dev.allissonnunes.algashop.ordering.infrastructure.adapters.in.web.PageModel;
import br.dev.allissonnunes.algashop.ordering.infrastructure.config.errorhandling.UnprocessableContentException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequestUri;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final ForQueryingOrders orderQueryService;

    private final ForBuyingWithShoppingCart checkoutApplicationService;

    private final ForBuyingProduct buyNowApplicationService;

    @GetMapping
    ResponseEntity<PageModel<OrderSummaryOutput>> getAllOrders(final OrderFilter filter) {
        return ResponseEntity.ok(PageModel.of(orderQueryService.filter(filter)));
    }

    @GetMapping("/{orderId}")
    ResponseEntity<OrderDetailOutput> getOrderById(@PathVariable final String orderId) {
        final OrderDetailOutput orderDetailOutput = orderQueryService.findById(orderId);
        return ResponseEntity.ok(orderDetailOutput);
    }

    @PostMapping(consumes = "application/vnd.order-with-shopping-cart.v1+json")
    ResponseEntity<OrderDetailOutput> checkout(@RequestBody final @Valid CheckoutInput input) {
        final String orderId;
        try {
            orderId = checkoutApplicationService.checkout(input);
        } catch (final DomainEntityNotFoundException e) {
            throw new UnprocessableContentException(e.getMessage(), e);
        }
        return retrieveOrderDetail(orderId);
    }

    @PostMapping(consumes = "application/vnd.order-with-product.v1+json")
    ResponseEntity<OrderDetailOutput> buyNow(@RequestBody final @Valid BuyNowInput input) {
        final String orderId;
        try {
            orderId = buyNowApplicationService.buyNow(input);
        } catch (final DomainEntityNotFoundException e) {
            throw new UnprocessableContentException(e.getMessage(), e);
        }
        return retrieveOrderDetail(orderId);
    }

    private @NonNull ResponseEntity<OrderDetailOutput> retrieveOrderDetail(final String orderId) {
        final OrderDetailOutput orderDetail = orderQueryService.findById(orderId);

        final var location = fromCurrentRequestUri().path("/{orderId}")
                .buildAndExpand(orderId)
                .toUri();

        return ResponseEntity.created(location).body(orderDetail);
    }

}
