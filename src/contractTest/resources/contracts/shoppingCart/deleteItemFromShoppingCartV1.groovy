package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method(DELETE())
        headers {
            accept('application/json,application/problem+json')
        }
        urlPath("/api/v1/shopping-carts/019c1bb7-c1ff-759d-83f7-c1d294c09643/items/019c1bba-e3f6-720a-96f7-134682ea709d")
    }
    response {
        status NO_CONTENT()
    }
}

