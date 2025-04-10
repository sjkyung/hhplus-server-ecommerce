package kr.hhplus.be.server.interfaces.order

import kr.hhplus.be.server.ApiResponse
import kr.hhplus.be.server.application.order.OrderService
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1")
class OrderController(
    private val orderService: OrderService,
): OrderApiSpec {

    @PostMapping("/users/{userId}/orders")
    override fun order(
        @PathVariable("userId") userId: Long,
        @RequestBody orderRequest: OrderRequest
    ):ApiResponse<OrderResponse> {
        val orderCommand = orderRequest.toCommand(userId)
        val order = orderService.create(orderCommand)
        val response = OrderResponse.from(order)
        return ApiResponse.success(response)
    }
}