package kr.hhplus.be.server.product

import kr.hhplus.be.server.domain.product.Product
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class ProductTest {

    @Test
    fun `validateStock은 잔고가 충분하고 요청 수량이 양수면 예외를 발생시키지 않아야 한다`() {
        // given
        val product = Product(id = 1, name = "테스트 상품", price = 100L, quantity = 10)

        // when & then
        assertThatCode {
            product.validateStock(5)
        }.doesNotThrowAnyException()
    }

    @Test
    fun `validateStock은 요청 수량이 0 이하이면 IllegalArgumentException을 발생시켜야 한다`() {
        // given
        val product = Product(id = 1, name = "테스트 상품", price = 100L, quantity = 10)

        // when & then
        assertThatThrownBy {
            product.validateStock(0)
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("요청 수량은 0보다 커야 합니다")
    }

    @Test
    fun `validateStock은 재고가 부족할 경우 IllegalStateException을 발생시켜야 한다`() {
        // given
        val product = Product(id = 1, name = "테스트 상품", price = 100L, quantity = 10)

        // when & then
        assertThatThrownBy {
            product.validateStock(15)
        }.isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("재고가 부족합니다")
    }

    @Test
    fun `decrease는 재고가 충분하면 정상적으로 재고를 차감해야 한다`() {
        // given
        val product = Product(id = 1, name = "테스트 상품", price = 100L, quantity = 10)

        // when
        product.decrease(4)

        // then
        assertThat(product.quantity).isEqualTo(6)
    }

    @Test
    fun `decrease는 재고가 부족하면 IllegalStateException을 발생시켜야 한다`() {
        // given
        val product = Product(id = 1, name = "테스트 상품", price = 100L, quantity = 10)

        // when & then
        assertThatThrownBy {
            product.decrease(15)
        }.isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("재고가 부족합니다")
    }
}