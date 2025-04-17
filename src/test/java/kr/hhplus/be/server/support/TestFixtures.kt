package kr.hhplus.be.server.support

import kr.hhplus.be.server.domain.coupon.Coupon
import kr.hhplus.be.server.domain.point.UserPoint
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch

object TestFixtures {

    fun userPoint(
        userId: Long = 1L,
        point: Long = 1000L
    ) = UserPoint(
        id = 0L,
        userId = userId,
        point = point
    )

    fun coupon(
        name: String = "선착순 쿠폰",
        discountAmount: Long = 1000L,
        quantity: Long = 100L,
        expiredAt: LocalDateTime = LocalDateTime.now(),
    ) = Coupon(
        couponId = 0L,
        name = name,
        discountAmount = discountAmount,
        quantity = quantity,
        expiredAt = expiredAt,
    )


    fun runConcurrently(count: Int, task: Runnable) {
        val latch = CountDownLatch(count) // 카운트를 `count`로 설정
        val threads = (1..count).map {
            Thread {
                task.run()  //실제 작업 실행
                latch.countDown()  //작업 완료 후 카운트 감소
            }
        }
        //모든 스레드를 시작
        threads.forEach { it.start() }
        //모든 스레드가 완료될 때까지 대기
        latch.await()
    }

    fun runTaskConcurrently(count: Int, vararg tasks: Runnable) {
        val latch = CountDownLatch(tasks.size)  //`tasks.size`만큼 카운트를 설정
        val threads = tasks.map { task ->  //여러 작업을 병렬로 실행
            Thread {
                task.run()  //실제 작업 실행
                latch.countDown()  //작업 완료 후 카운트 감소
            }
        }
        //모든 스레드를 시작
        threads.forEach { it.start() }
        //모든 스레드가 완료될 때까지 대기
        latch.await()
    }
}