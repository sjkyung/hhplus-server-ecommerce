package kr.hhplus.be.server.interfaces.payment

import kr.hhplus.be.server.ApiResponse
import kr.hhplus.be.server.application.payment.PaymentService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class PaymentController(
    private val paymentService: PaymentService,
) : PaymentApiSpec {

    @PostMapping("/payments")
    override fun pay(
        @RequestBody paymentRequest: PaymentRequest
    ): ApiResponse<PaymentResponse> {
        val paymentCommand = paymentRequest.toCommand()
        val payment = paymentService.create(paymentCommand)
        val response = PaymentResponse.from(payment)
        return ApiResponse.success(response)
    }
}