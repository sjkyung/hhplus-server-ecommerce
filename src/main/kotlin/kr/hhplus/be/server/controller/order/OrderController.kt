package kr.hhplus.be.server.controller.order

import kr.hhplus.be.server.ApiResponse
import kr.hhplus.be.server.controller.order.request.OrderRequest
import kr.hhplus.be.server.controller.order.response.OrderResponse
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1")
class OrderController: OrderApiSpec {

    @PostMapping("/users/{userId}/orders")
    override fun order(
        @PathVariable("userId") userId: Long,
        @RequestBody orderRequest: OrderRequest
    ):ApiResponse<OrderResponse> {
        val response = OrderResponse(1,1,50000, LocalDateTime.now())
        return ApiResponse.success(response)
    }
}