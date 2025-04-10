package kr.hhplus.be.server.domain.coupon

interface CouponRepository {
    fun findById(couponId: Long): Coupon
    fun save(coupon: Coupon): Coupon
    fun findByIds(couponIds: List<Long>): List<Coupon>
}