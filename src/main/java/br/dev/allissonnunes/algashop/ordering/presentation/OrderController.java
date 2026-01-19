package br.dev.allissonnunes.algashop.ordering.presentation;

import br.dev.allissonnunes.algashop.ordering.application.order.query.OrderDetailOutput;
import br.dev.allissonnunes.algashop.ordering.application.order.query.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
class OrderController {

    private final OrderQueryService orderQueryService;

    @GetMapping("/{orderId}")
    ResponseEntity<OrderDetailOutput> getOrderById(@PathVariable final String orderId) {
        final OrderDetailOutput orderDetailOutput = orderQueryService.findById(orderId);
        return ResponseEntity.ok(orderDetailOutput);
    }

}
