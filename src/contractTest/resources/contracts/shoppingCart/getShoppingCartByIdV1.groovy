package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method(GET())
        headers {
            accept('application/json,application/problem+json')
        }
        urlPath("/api/v1/shopping-carts/277297bf-e586-4389-9f21-b3ce0c3f6580")
    }
    response {
        status(OK())
        headers {
            contentType(applicationJson())
        }
        body([
                id         : anyUuid(),
                customerId : anyUuid(),
                totalAmount: anyNumber(),
                totalItems : anyNumber(),
                items      : [
                        [
                                id         : anyUuid(),
                                productId  : anyUuid(),
                                name       : anyNonEmptyString(),
                                price      : anyNumber(),
                                quantity   : anyNumber(),
                                totalAmount: anyNumber(),
                                available  : anyBoolean()
                        ],
                        [
                                id         : anyUuid(),
                                productId  : anyUuid(),
                                name       : anyNonEmptyString(),
                                price      : anyNumber(),
                                quantity   : anyNumber(),
                                totalAmount: anyNumber(),
                                available  : anyBoolean()
                        ]
                ]
        ])
    }
}