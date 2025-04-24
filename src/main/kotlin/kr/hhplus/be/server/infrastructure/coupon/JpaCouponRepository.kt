package kr.hhplus.be.server.infrastructure.coupon

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock

interface JpaCouponRepository: JpaRepository<CouponEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findWithLockById(id: Long): CouponEntity
}