package kr.hhplus.be.server.point

import kr.hhplus.be.server.application.point.PointChargeCommand
import kr.hhplus.be.server.application.point.PointService
import kr.hhplus.be.server.domain.point.UserPoint
import kr.hhplus.be.server.domain.point.UserPointRepository
import kr.hhplus.be.server.support.IntegrationTestBase
import kr.hhplus.be.server.support.TestFixtures
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


class PointServiceIntegrationTest @Autowired constructor(
    private val pointService: PointService,
    private val pointRepository: UserPointRepository
) : IntegrationTestBase() {


    @Test
    fun `포인트 충전이 정상적으로 수행된다`() {
        // given
        pointRepository.save(
            TestFixtures.userPoint(userId = 1, point = 1000)
        )
        val command = PointChargeCommand(userId = 1L, amount = 500)

        // when
        val result = pointService.charge(command)

        // then
        assertThat(result.point).isEqualTo(1500)
    }

    @Test
    fun `유저 아이디로 포인트를 조회한다`() {
        //given
        pointRepository.save(
            TestFixtures.userPoint(userId = 1, point = 1000)
        )
        val userId = 1L

        //when
        val result = pointService.getPoint(userId)

        //then
        assertThat(result.userId).isEqualTo(userId)
    }

    @Test
    fun `동시에 포인트를 충전하면 최종 포인트가 예상과 다를 수 있다`() {
        val userId = 1L
        pointRepository.save(UserPoint(id = 0, userId = userId, point = 0))

        TestFixtures.runConcurrently(100, Runnable {
            try {
                pointService.charge(PointChargeCommand(userId, 10))
            } catch (e: Exception) {
                println("에러 발생: ${e.javaClass.simpleName} - ${e.message}")
            }
        })

        val finalPoint = pointRepository.findByUserId(userId)!!.point
        println("최종 포인트: $finalPoint")

        //의도적으로 실패: 기대값은 10 * 100 = 1000
        assertThat(finalPoint).isEqualTo(1000)
    }

}