package kr.hhplus.be.server.interfaces.coupon

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.parameters.RequestBody
import kr.hhplus.be.server.ApiResponse


@Tag(
    name = "쿠폰 API",
    description = "쿠폰 서비스를 제공하는 API 입니다."
)
interface CouponApiSpec {

    @Operation(
        summary = "쿠폰 조회",
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "쿠폰 조회 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "쿠폰 조회 성공 예시",
                                summary = "쿠폰이 성공적으로 조회된 경우",
                                value = """{
                                    "code": 200,
                                    "message": "성공",
                                    "data": [
                                        {
                                            "couponId": 1,
                                            "name": "선착순 쿠폰",
                                            "discountAmount": 1000,
                                            "expiredAt": "2025-04-04T00:11:18.263621"
                                        },
                                        {
                                            "couponId": 2,
                                            "name": "회원가입 쿠폰",
                                            "discountAmount": 2000,
                                            "expiredAt": "2025-04-04T00:11:18.263739"
                                        },
                                        {
                                            "couponId": 3,
                                            "name": "1주년 쿠폰",
                                            "discountAmount": 5000,
                                            "expiredAt": "2025-04-04T00:11:18.263756"
                                        }
                                    ]
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    fun getCoupons(
        @Parameter(description = "사용자 ID", example = "1")
        userId: Long,
    ): ApiResponse<List<CouponFindResponse>>

    @Operation(
        summary = "선착순 쿠폰 발급",
        description = "쿠폰 ID와 사용자 ID를 받아 쿠폰을 발급합니다.",
        requestBody = RequestBody(
            description = "쿠폰 발급 요청 바디",
            required = true,
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "쿠폰 발급 요청 예시",
                            summary = "사용자 1번에게 쿠폰 10번 발급",
                            value = """
                            {
                              "userId": 1
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
                description = "쿠폰 발급 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "쿠폰 발급 성공 예시",
                                summary = "쿠폰이 성공적으로 발급된 경우",
                                value = """{
                                  "code": 200,
                                  "message": "성공",
                                  "data": {
                                    "couponId": 10,
                                    "discountAmount": 1000,
                                    "status": "AVAILABLE",
                                    "usedAt": null,
                                    "createdAt": "2025-04-03T23:50:00",
                                    "expiredAt": "2025-04-04T23:50:00"
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
    fun issue(
        @Parameter(description = "쿠폰 ID", example = "10")
        couponId: Long,
        couponIssueRequest: CouponIssueRequest
    ): ApiResponse<CouponIssueResponse>
}