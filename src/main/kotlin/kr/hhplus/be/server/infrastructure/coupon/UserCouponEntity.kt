package kr.hhplus.be.server.infrastructure.coupon

import jakarta.persistence.*
import kr.hhplus.be.server.domain.coupon.CouponStatus
import java.time.LocalDateTime

@Entity
@Table(name = "user_coupon")
class UserCouponEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val userId: Long,
    val couponId: Long,
    @Enumerated(EnumType.STRING)
    val status: CouponStatus,
    val usedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null,
) {
}