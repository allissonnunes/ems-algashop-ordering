package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.MediaType

Contract.make {
    request {
        method(DELETE())
        headers {
            accept('application/problem+json')
        }
        urlPath("/api/v1/shopping-carts/019c1bac-f505-7266-8ad0-8889f319e8da")
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