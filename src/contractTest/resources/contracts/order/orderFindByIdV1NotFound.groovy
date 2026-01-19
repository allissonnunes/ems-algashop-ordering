package contracts.order

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        headers {
            accept(applicationJson())
        }
        url('/api/v1/orders/01226N0693HDH')
    }
    response {
        status NOT_FOUND()
        body([
                "status"  : 404,
                "type"    : "/errors/not-found",
                "title"   : "Not Found",
                "detail"  : "Order ${fromRequest().path(3)} not found",
                "instance": fromRequest().path()
        ])
    }
}
