package br.dev.allissonnunes.algashop.ordering.presentation.order;

import br.dev.allissonnunes.algashop.ordering.application.checkout.BuyNowApplicationService;
import br.dev.allissonnunes.algashop.ordering.application.checkout.BuyNowInput;
import br.dev.allissonnunes.algashop.ordering.application.checkout.CheckoutApplicationService;
import br.dev.allissonnunes.algashop.ordering.application.checkout.CheckoutInput;
import br.dev.allissonnunes.algashop.ordering.application.order.query.OrderDetailOutput;
import br.dev.allissonnunes.algashop.ordering.application.order.query.OrderFilter;
import br.dev.allissonnunes.algashop.ordering.application.order.query.OrderQueryService;
import br.dev.allissonnunes.algashop.ordering.application.order.query.OrderSummaryOutput;
import br.dev.allissonnunes.algashop.ordering.presentation.PageModel;
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

    private final OrderQueryService orderQueryService;

    private final CheckoutApplicationService checkoutApplicationService;

    private final BuyNowApplicationService buyNowApplicationService;

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
        final String orderId = checkoutApplicationService.checkout(input);
        return retrieveOrderDetail(orderId);
    }

    @PostMapping(consumes = "application/vnd.order-with-product.v1+json")
    ResponseEntity<OrderDetailOutput> buyNow(@RequestBody final @Valid BuyNowInput input) {
        final String orderId = buyNowApplicationService.buyNow(input);
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
