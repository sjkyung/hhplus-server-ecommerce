package kr.hhplus.be.server.domain.coupon

interface UserCouponRepository{
    fun findByUserId(userId: Long): List<UserCoupon>
    fun findById(userCouponId: Long): UserCoupon
    fun save(userCoupon: UserCoupon): UserCoupon
}
