package kr.hhplus.be.server.controller.point

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.ApiResponse
import kr.hhplus.be.server.controller.point.request.ChargePointRequest
import kr.hhplus.be.server.controller.point.response.ChargePointResponse
import kr.hhplus.be.server.controller.point.response.PointFindResponse

@Tag(
    name = "포인트 API",
    description = "포인트 서비스를 제공하는 API 입니다."
)
interface PointApiSpec {

    @Operation(
        summary = "포인트 조회",
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "포인트 조회 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "포인트 조회 성공 예시",
                                value = """
                            {
                                "code": 200,
                                "message": "성공",
                                "data": {
                                    "userId": 1,
                                    "point": 5000
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
    fun getPoint(
        @Parameter(description = "사용자 ID", example = "1")
        userId: Long
    ): ApiResponse<PointFindResponse>

    @Operation(
        summary = "포인트 충전",
        requestBody = RequestBody(
            description = "포인트 충전 요청 바디",
            required = true,
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            value = """
                                {
                                    "amount": 10000
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
                description = "포인트 충전 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "포인트 충전 성공 예시",
                                value = """
                            {
                                "code": 200,
                                "message": "성공",
                                "data": {
                                    "userId": 1,
                                    "amount": 3000,
                                    "point": 8000,
                                    "chargedAt": "2025-04-04T01:06:25.806615"
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
    fun charge(
        @Parameter(description = "사용자 ID", example = "1")
        userId: Long,
        charge: ChargePointRequest
    ): ApiResponse<ChargePointResponse>
}