package kr.hhplus.be.server.controller.coupon

import kr.hhplus.be.server.ApiResponse
import kr.hhplus.be.server.controller.coupon.request.CouponIssueRequest
import kr.hhplus.be.server.controller.coupon.response.CouponFindResponse
import kr.hhplus.be.server.controller.coupon.response.CouponIssueResponse
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1")
class CouponController : CouponApiSpec {

    @GetMapping("/users/{userId}/coupons")
    override fun getCoupons(
        @PathVariable("userId") userId: Long,
    ): ApiResponse<List<CouponFindResponse>> {
        val response =
            listOf(
                CouponFindResponse(1, "선착순 쿠폰", 1000, LocalDateTime.now()),
                CouponFindResponse(2, "회원가입 쿠폰", 2000, LocalDateTime.now()),
                CouponFindResponse(3, "1주년 쿠폰", 5000, LocalDateTime.now()),
            )
        return ApiResponse.success(response)
    }

    @PostMapping("/coupon/{couponId}/issue")
    override fun issue(
        @PathVariable("couponId") couponId: Long,
        @RequestBody couponIssueRequest: CouponIssueRequest
    ): ApiResponse<CouponIssueResponse> {
        val response =
            CouponIssueResponse(1, 1000, "AVAILABLE", null, LocalDateTime.now(), LocalDateTime.now().plusHours(1))
        return ApiResponse.success(response)
    }


}