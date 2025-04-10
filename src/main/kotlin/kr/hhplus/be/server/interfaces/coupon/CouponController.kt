package kr.hhplus.be.server.interfaces.coupon

import kr.hhplus.be.server.ApiResponse
import kr.hhplus.be.server.application.coupon.CouponService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class CouponController(
    private val couponService: CouponService,
) : CouponApiSpec {

    @GetMapping("/coupons/users/{userId}")
    override fun getCoupons(
        @PathVariable("userId") userId: Long,
    ): ApiResponse<List<CouponFindResponse>> {
        val userCoupons = couponService.getCoupons(userId)
        val response = userCoupons.map { CouponFindResponse.from(it) }
        return ApiResponse.success(response)
    }

    @PostMapping("/coupons/{couponId}/issue")
    override fun issue(
        @PathVariable("couponId") couponId: Long,
        @RequestBody couponIssueRequest: CouponIssueRequest
    ): ApiResponse<CouponIssueResponse> {
        val couponCommand = couponIssueRequest.toCommand(couponId)
        val userCoupon = couponService.issue(couponCommand)
        val response = CouponIssueResponse.from(userCoupon)
        return ApiResponse.success(response)
    }


}