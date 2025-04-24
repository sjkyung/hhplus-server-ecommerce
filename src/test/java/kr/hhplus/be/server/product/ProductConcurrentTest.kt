package kr.hhplus.be.server.product

import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.product.ProductRepository
import kr.hhplus.be.server.support.IntegrationTestBase
import kr.hhplus.be.server.support.TestFixtures
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ProductConcurrentTest @Autowired constructor(
    private val productRepository: ProductRepository,
) : IntegrationTestBase() {

    @Test
    fun `동시에 재고를 차감하면 정합성이 깨질 수 있다`() {
        // given
        val product = productRepository.save(Product(id = 0, name = "상품", price = 1000, quantity = 2))

        val task = Runnable {
            try {
                val target = productRepository.findById(product.id)!!
                target.decrease(1)

                productRepository.save(target) // 실제 저장
            } catch (e: Exception) {
                println("[에러] ${e.message}")
            }
        }

        TestFixtures.runConcurrently(20, task)

        val updated = productRepository.findById(product.id)
        println("최종 재고: ${updated?.quantity}")

        // 실제는 음수 또는 0보다 작을 수도 있음
        assertThat(updated?.quantity).isGreaterThanOrEqualTo(0)
    }
}