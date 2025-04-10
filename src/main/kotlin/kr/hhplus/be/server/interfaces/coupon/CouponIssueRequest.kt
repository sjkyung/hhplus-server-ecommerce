package kr.hhplus.be.server.interfaces.coupon

import kr.hhplus.be.server.application.coupon.CouponCommand

data class CouponIssueRequest(
    val userId: Long
){
    fun toCommand(couponId: Long) : CouponCommand {
        return CouponCommand(
            couponId,
            userId
        )
    }
}
