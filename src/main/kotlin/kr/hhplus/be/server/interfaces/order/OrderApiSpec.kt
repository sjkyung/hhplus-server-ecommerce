package kr.hhplus.be.server.interfaces.order

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.media.Content
import kr.hhplus.be.server.ApiResponse


@Tag(
    name = "주문 API",
    description = "주문 서비스를 제공하는 API 입니다."
)
interface OrderApiSpec {

    @Operation(summary = "주문 생성",
        requestBody = RequestBody(
            description = "주문 요청 바디",
            required = true,
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "주문 예시",
                            summary = "쿠폰을 사용하는 주문",
                            value = """
                                {
                                  "products": [
                                    { "productId": 101, "quantity": 2 },
                                    { "productId": 202, "quantity": 1 }
                                  ],
                                  "couponId": 1001
                                }
                                """
                        ),
                        ExampleObject(
                            name = "쿠폰 없이 주문",
                            value = """
                            {
                              "products": [
                                { "productId": 101, "quantity": 2 },
                                { "productId": 202, "quantity": 2 }
                              ]
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
                description = "주문 생성 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                value = """
                            {
                              "code": 200,
                              "message": "성공",
                              "data": {
                                "orderId": 44,
                                "userId": 1,
                                "totalAmount": 25000,
                                "orderedAt": "2025-04-03T23:40:00"
                              }
                            }
                            """
                            )
                        ]
                    )
                ]
            )
        ])
    fun order(
        @Parameter(description = "사용자 ID", example = "1")
        userId: Long,
        orderRequest: OrderRequest
    ): ApiResponse<OrderResponse>
}