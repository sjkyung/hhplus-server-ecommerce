package kr.hhplus.be.server.support

import kr.hhplus.be.server.domain.coupon.Coupon
import kr.hhplus.be.server.domain.point.UserPoint
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

object TestFixtures {

    fun userPoint(
        userId: Long = 1L,
        point: Long = 1000L
    ) = UserPoint(
        id = 0L,
        userId = userId,
        point = point,
        version = 0L
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
        val executor = Executors.newFixedThreadPool(count)
        val latch = CountDownLatch(count) // 카운트를 `count`로 설정

        repeat(count){
            executor.submit { //쓰레드 풀에 제출
                try {
                    task.run()  //실제 작업 실행
                }finally {
                    latch.countDown()  //작업 완료 후 카운트 감소
                }
            }
        }

        //모든 스레드가 완료될 때까지 대기
        latch.await()
        //스레드 풀 종료
        executor.shutdown()
    }

    fun runTaskConcurrently(vararg tasks: Runnable) {
        val tasksCount = tasks.size
        val executor = Executors.newFixedThreadPool(tasksCount)
        val latch = CountDownLatch(tasksCount)  //`tasks.size`만큼 카운트를 설정

        tasks.map { task ->
            executor.submit { //각 작업을 스레드 풀에 제출
                try {
                    task.run()  //실제 작업 실행
                }finally {
                    latch.countDown()  //작업 완료 후 카운트 감소
                }
            }
        }
        //모든 스레드가 완료될 때까지 대기
        latch.await()
        //스레드 풀 종료
        executor.shutdown()
    }
}