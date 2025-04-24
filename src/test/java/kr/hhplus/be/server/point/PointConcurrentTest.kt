package kr.hhplus.be.server.point

import kr.hhplus.be.server.application.point.PointChargeCommand
import kr.hhplus.be.server.application.point.PointService
import kr.hhplus.be.server.domain.point.UserPoint
import kr.hhplus.be.server.domain.point.UserPointRepository
import kr.hhplus.be.server.support.IntegrationTestBase
import kr.hhplus.be.server.support.TestFixtures
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.orm.ObjectOptimisticLockingFailureException

class PointConcurrentTest @Autowired constructor(
    private val pointService: PointService,
    private val pointRepository: UserPointRepository
) : IntegrationTestBase() {

    @Test
    fun `동시에 포인트를 충전하면 정합성 문제 발생한다`() {
        val userId = 1L
        //pointRepository.save(UserPoint(id = 0, userId = userId, point = 0, version = 0))
        pointRepository.save(UserPoint(id = 0, userId = userId, point = 0))

        TestFixtures.runConcurrently(3, Runnable {
            pointService.charge(PointChargeCommand(userId, 1000))
        })

        val finalPoint = pointRepository.findByUserId(userId).point
        println("최종 포인트: $finalPoint")

        //의도적으로 실패: 기대값은 1000 * 3 = 3000
        assertThat(finalPoint).isEqualTo(3000)
    }


    @Test
    fun `비관적 락을 사용하여 동시에 3건의 포인트를 충전하면 성공한다`() {
        val userId = 1L
        //pointRepository.save(UserPoint(id = 0, userId = userId, point = 0, version = 0))
        pointRepository.save(UserPoint(id = 0, userId = userId, point = 0))

        TestFixtures.runConcurrently(3, Runnable {
            pointService.chargePessimistic(PointChargeCommand(userId, 1000))
        })

        val finalPoint = pointRepository.findByUserId(userId).point
        println("최종 포인트: $finalPoint")

        //기대값은 1000 * 3 = 3000
        assertThat(finalPoint).isEqualTo(3000)
    }


    @Test
    fun `낙관적 락을 사용하여 동시에 3건의 포인트를 충전하면 1건은 성공하고, 2건은 실패한다`() {
        val userId = 1L
        pointRepository.save(UserPoint(id = 0, userId = userId, point = 0,version = 0))


        val errorCount = java.util.concurrent.atomic.AtomicInteger(0)

        TestFixtures.runConcurrently(3, Runnable {
            try {
                pointService.chargeOptimistic(PointChargeCommand(userId, 1000))
            } catch (e: ObjectOptimisticLockingFailureException) {
                println("낙관적 락 충돌 발생: ${e::class.simpleName} - ${e.message}")
                errorCount.incrementAndGet()
            }
        })

        val finalPoint = pointRepository.findByUserId(userId).point
        println("최종 포인트: $finalPoint")

        //기대값은 1000 * 3 = 3000
        assertAll({
            assertThat(finalPoint).isEqualTo(3000)
            assertThat(errorCount.get()).isEqualTo(2)
        })
    }



    @Test
    fun `ReentrantLock을 사용하여 동시에 포인트를 충전하면 성공한다`() {
        val userId = 1L
        pointRepository.save(UserPoint(id = 0, userId = userId, point = 0))

        TestFixtures.runConcurrently(3, Runnable {
            pointService.chargeWithLock(PointChargeCommand(userId, 1000))
        })

        val finalPoint = pointRepository.findByUserId(userId).point
        println("최종 포인트: $finalPoint")

        //기대값은 1000 * 3 = 3000
        assertThat(finalPoint).isEqualTo(3000)
    }


    @Test
    fun `serializable를 사용하여 동시에 포인트를 충전하면 데드락이 발생할 수 있다`() {
        val userId = 1L
        pointRepository.save(UserPoint(id = 0, userId = userId, point = 0))

        TestFixtures.runConcurrently(3, Runnable {
            pointService.chargeSerializable(PointChargeCommand(userId, 1000))
        })

        val finalPoint = pointRepository.findByUserId(userId).point
        println("최종 포인트: $finalPoint")

        //기대값은 1000 * 3 = 3000
        assertThat(finalPoint).isEqualTo(3000)
    }

}