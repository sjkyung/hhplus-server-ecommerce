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
}