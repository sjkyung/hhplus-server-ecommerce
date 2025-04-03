package kr.hhplus.be.server.controller.payment

import kr.hhplus.be.server.ApiResponse
import kr.hhplus.be.server.controller.payment.request.PaymentRequest
import kr.hhplus.be.server.controller.payment.response.PaymentResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1")
class PaymentController : PaymentApiSpec {

    @PostMapping("/payments")
    override fun pay(
        @RequestBody paymentRequest: PaymentRequest
    ): ApiResponse<PaymentResponse> {
        val response = PaymentResponse(1, "COMPLETED", 50000, LocalDateTime.now())
        return ApiResponse.success(response)
    }
}