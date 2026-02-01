package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.MediaType

Contract.make {
    request {
        method(GET())
        headers {
            accept('application/json,application/problem+json')
        }
        urlPath("/api/v1/shopping-carts/019c1b89-6a93-798a-a9ef-8a4e6eb71040")
    }
    response {
        status(NOT_FOUND())
        headers {
            contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        }
        body([
                "status"  : 404,
                "type"    : "/errors/not-found",
                "title"   : "Not Found",
                "detail"  : anyNonBlankString(),
                "instance": fromRequest().path()
        ])
    }
}