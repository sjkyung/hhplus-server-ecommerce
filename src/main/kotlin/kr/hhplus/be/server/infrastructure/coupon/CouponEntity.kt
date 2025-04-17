package kr.hhplus.be.server.infrastructure.coupon

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "coupon")
class CouponEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long = 0,
    val name: String,
    val discountAmount: Long,
    var quantity : Long,
    val expiredAt : LocalDateTime,
) {
}