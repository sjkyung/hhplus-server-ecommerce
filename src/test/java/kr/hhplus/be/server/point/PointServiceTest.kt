package kr.hhplus.be.server.point

import kr.hhplus.be.server.application.point.PointChargeCommand
import kr.hhplus.be.server.application.point.PointService
import kr.hhplus.be.server.domain.point.UserPoint
import kr.hhplus.be.server.domain.point.UserPointRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class PointServiceTest {

    private lateinit var userPointRepository: UserPointRepository
    private lateinit var pointService: PointService

    @BeforeEach
    fun init() {
        pointService = PointService(userPointRepository)
    }

    @Test
    fun `포인트를 정상적으로 조회한다`() {
        // given
        val userId = 1L
        val userPoint = UserPoint(userId, 1L,1000)
        `when`(userPointRepository.findByUserId(userId)).thenReturn(userPoint)

        // when
        val result = pointService.getPoint(userId)

        // then
        assertThat(result).isEqualTo(userPoint)
        verify(userPointRepository).findByUserId(userId)
    }

    @Test
    fun `포인트를 정상적으로 충전한다`() {
        // given
        val command = PointChargeCommand(userId = 1L, amount = 500)
        val initialPoint = UserPoint(1L, 1L,1000)
        // 충전 후 포인트가 1500원이 되는 케이스라고 가정
        val chargedPoint = UserPoint(1L,1L, 1500)
        `when`(userPointRepository.findByUserId(command.userId)).thenReturn(initialPoint)
        `when`(userPointRepository.save(any(UserPoint::class.java))).thenReturn(chargedPoint)

        // when
        val result = pointService.charge(command)

        // then
        assertThat(result.point).isEqualTo(1500)
        verify(userPointRepository).findByUserId(command.userId)
        verify(userPointRepository).save(any(UserPoint::class.java))
    }

    @Test
    fun `음수 포인트 충전 시 예외가 발생한다`() {
        // given
        val command = PointChargeCommand(userId = 1L, amount = -100)
        val initialPoint = UserPoint(1L, 1L,1000)
        `when`(userPointRepository.findByUserId(command.userId)).thenReturn(initialPoint)

        // when & then
        assertThatThrownBy {
            pointService.charge(command)
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("충전 금액은 음수일 수 없습니다.")

        verify(userPointRepository).findByUserId(command.userId)
    }
}