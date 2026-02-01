package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method(POST())
        headers {
            accept('application/json,application/problem+json')
            contentType(applicationJson())
        }
        urlPath("/api/v1/shopping-carts")
        body([
                customerId: value(test("f5ab7a1e-37da-41e1-892b-a1d38275c2f2"), stub(anyUuid()))
        ])
    }
    response {
        status CREATED()
        headers {
            contentType(applicationJson())
            header('Location', value(regex('.+/api/v1/shopping-carts/[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}')))
        }
        body([
                id         : anyUuid(),
                customerId : anyUuid(),
                totalAmount: anyNumber(),
                totalItems : anyNumber(),
                items      : []
        ])
    }
}

