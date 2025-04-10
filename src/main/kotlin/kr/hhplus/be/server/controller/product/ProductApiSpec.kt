package kr.hhplus.be.server.controller.product

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.ApiResponse
import kr.hhplus.be.server.controller.product.response.ProductFindResponse
import kr.hhplus.be.server.controller.product.response.ProductRankingResponse

@Tag(
    name = "상품 API",
    description = "상품 서비스를 제공 하는 API 입니다."
)
interface ProductApiSpec {

    @Operation(
        summary = "상품 목록 조회",
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "상품 목록 조회 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "상품 목록 조회 성공 예시",
                                summary = "상품 목록이 성공적으로 조회된 경우",
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
    fun getProducts(): ApiResponse<List<ProductFindResponse>>


    @Operation(
        summary = "최근 3일간 판매 랭킹 상위 5건 상품 조회",
        responses = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "최근 3일간 판매 랭킹 상위 5건 상품 조회 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "최근 3일간 판매 랭킹 상위 5건 상품 조회 예시",
                                summary = "최근 3일간 판매 랭킹 상위 5건 상품이 성공적으로 조회된 경우",
                                value = """
                                    {
                                    "code": 200,
                                    "message": "성공",
                                    "data": [
                                    {
                                        "rank": 1,
                                        "name": "뉴발란스 신발",
                                        "price": 40000,
                                        "salesCount": 250,
                                        "countedAt": "2025-04-04T00:28:35.565452"
                                    },
                                    {
                                        "rank": 2,
                                        "name": "나이키신발",
                                        "price": 10000,
                                        "salesCount": 200,
                                        "countedAt": "2025-04-04T00:28:35.565528"
                                    },
                                    {
                                        "rank": 3,
                                        "name": "아디다스 신발",
                                        "price": 5000,
                                        "salesCount": 150,
                                        "countedAt": "2025-04-04T00:28:35.565532"
                                    },
                                    {
                                        "rank": 4,
                                        "name": "푸마 신발",
                                        "price": 4000,
                                        "salesCount": 70,
                                        "countedAt": "2025-04-04T00:28:35.565535"
                                    },
                                    {
                                        "rank": 5,
                                        "name": "리복 신발",
                                        "price": 2000,
                                        "salesCount": 30,
                                        "countedAt": "2025-04-04T00:28:35.565538"
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
    fun getProductRanking(): ApiResponse<List<ProductRankingResponse>>
}