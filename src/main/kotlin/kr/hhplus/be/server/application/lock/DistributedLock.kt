package kr.hhplus.be.server.application.lock

import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLock(
    val key: String,
    val timeUnit: TimeUnit = TimeUnit.SECONDS, // 락의 시간 단위
    val waitTime: Long = 3L, //락 획득 시도 시간
    val leaseTime: Long = 5L, //락 유지 시간
    val lockType: LockType = LockType.REDIS_SPIN// 기본 값 SPIN lock
) {
}