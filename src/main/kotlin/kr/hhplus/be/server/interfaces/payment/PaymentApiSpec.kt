package kr.hhplus.be.server.interfaces.payment

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.ApiResponse

@Tag(
    name = "결제 API",
    description = "결제 서비스를 제공하는 API 입니다."
)
interface PaymentApiSpec {

    @Operation(
        summary = "결제 요청",
        requestBody = RequestBody(
            description = "결제 요청 바디",
            required = true,
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            value = """
                                {
                                    "orderId": 1
                                }
                                """
                        )
                    ]
                )
            ]
        ),
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "결제 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "결제 성공 예시",
                                value = """
                            {
                                "code": 200,
                                "message": "성공",
                                "data": {
                                    "orderId": 1,
                                    "status": "COMPLETED",
                                    "amount": 50000,
                                    "paidAt": "2025-04-04T01:01:55.635185"
                                }
                            }
                            """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    fun pay(
        paymentRequest: PaymentRequest,
    ): ApiResponse<PaymentResponse>
}