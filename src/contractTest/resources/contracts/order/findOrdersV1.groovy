package contracts.order


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        headers {
            accept(applicationJson())
        }
        urlPath("/api/v1/orders") {
            queryParameters {
                parameter('page', "0")
                parameter('size', "10")
            }
        }
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body([
                content      : [
                        [
                                id           : "01226N0640J7Q",
                                customer     : [
                                        id       : anyUuid(),
                                        firstName: "John",
                                        lastName : "Doe",
                                        document : "12345",
                                        email    : "johndoe@email.com",
                                        phone    : "1191234564"
                                ],
                                totalItems   : 2,
                                totalAmount  : 41.98,
                                placedAt     : anyIso8601WithOffset(),
                                canceledAt   : null,
                                paidAt       : null,
                                readyAt      : null,
                                status       : "PLACED",
                                paymentMethod: "GATEWAY_BALANCE"
                        ]
                ],
                number       : 0,
                size         : 10,
                totalPages   : 1,
                totalElements: 1
        ])
    }
}