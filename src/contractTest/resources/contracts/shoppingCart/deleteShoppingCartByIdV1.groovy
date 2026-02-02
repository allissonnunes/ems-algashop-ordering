package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method(DELETE())
        headers {
            accept('application/problem+json')
        }
        urlPath("/api/v1/shopping-carts/019c1ba8-2713-77d7-94a0-9ce447ca9e89")
    }
    response {
        status(NO_CONTENT())
    }
}