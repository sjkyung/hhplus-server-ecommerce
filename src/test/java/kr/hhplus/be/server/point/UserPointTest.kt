package kr.hhplus.be.server.point

import kr.hhplus.be.server.domain.point.UserPoint
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class UserPointTest {

    @Nested
    @DisplayName("포인트 충전 테스트")
    inner class charge {
        @Test
        fun `포인트 충전 금액이 양수 일때 포인트가 증가한다`() {
            // given
            val point = UserPoint(id = 1L,1L, point = 100L)

            // when
            val updatedPoint = point.charge(50L)

            // then
            assertThat(updatedPoint.point).isEqualTo(point)
            assertThat(updatedPoint.id).isEqualTo(1)
        }

        @Test
        fun `0 이하 또는 음수 일 경우 충전 시 예외가 발생해야 한다`() {
            // given
            val point = UserPoint(id = 1L,1L, point = 100L)

            // then
            assertThatThrownBy {
                point.charge(0L)
            }.isInstanceOf(IllegalArgumentException::class.java)
            assertThatThrownBy{
                point.charge(-10L)
            }.isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Nested
    @DisplayName("포인트 사용 테스트")
    inner class use {
        @Test
        fun `사용금액이 양수 이고 잔액이 충분할 경우 포인트가 차감되어야 한다`() {
            // given
            val point = UserPoint(id = 1L,1L, point = 1000)

            // when
            val updatedPoint = point.use(300)

            // then
            assertThat(updatedPoint.point).isEqualTo(point)
        }

        @Test
        fun `0이하 또는 음수일 경우 포인트 사용 시 예외가 발생해야 한다`() {
            // given
            val point = UserPoint(id = 1L,1L, point = 100L)

            // then
            assertThatThrownBy{
                point.use(0L)
            }.isInstanceOf(IllegalArgumentException::class.java)
            assertThatThrownBy{
                point.use(-5L)
            }.isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `포인트 사용 시 잔액이 부족할 경우 사용 시 예외가 발생해야 한다`() {
            // given
            val point = UserPoint(id = 1L,1L, point = 50L)

            // then
            assertThatThrownBy{
                point.use(60L)
            }.isInstanceOf(IllegalStateException::class.java)

        }
    }

    @Nested
    @DisplayName("포인트 잔액 테스트")
    inner class balance {

        @Test
        fun `잔액이 충분할 경우 검증은 예외를 발생시키지 않아야 한다`() {

            assertThatCode {
                UserPoint(id = 1L,1L, point = 100L).validateAvailable(50L)
            }.doesNotThrowAnyException()
        }

        @Test
        fun `잔액이 부족할 경우 검증은 IllegalStateException이 발생시켜야 한다`() {
            // given
            val point = UserPoint(id = 1L,1L, point = 30L)

            // then
            assertThatThrownBy{
                point.validateAvailable(40L)
            }.isInstanceOf(IllegalStateException::class.java)
        }
    }
}