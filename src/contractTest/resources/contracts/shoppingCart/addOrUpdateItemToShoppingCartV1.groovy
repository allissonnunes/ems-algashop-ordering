package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method(POST())
        headers {
            accept('application/json,application/problem+json')
            contentType(applicationJson())
        }
        urlPath("/api/v1/shopping-carts/019c1bb7-c1ff-759d-83f7-c1d294c09643/items")
        body([
                "productId": "6a6ca34c-e65c-496e-adaf-f872e1784003",
                "quantity" : 2,
        ])
    }
    response {
        status NO_CONTENT()
    }
}

