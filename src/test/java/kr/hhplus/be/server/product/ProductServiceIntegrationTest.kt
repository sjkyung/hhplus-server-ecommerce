package kr.hhplus.be.server.product

import kr.hhplus.be.server.application.product.ProductService
import kr.hhplus.be.server.domain.order.OrderItem
import kr.hhplus.be.server.domain.order.OrderItemRepository
import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.product.ProductRepository
import kr.hhplus.be.server.support.IntegrationTestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime


class ProductServiceIntegrationTest @Autowired constructor(
    private val productService: ProductService,
    private val productRepository: ProductRepository,
    private val orderItemRepository: OrderItemRepository,
) : IntegrationTestBase() {

    @Test
    fun `상품 목록을 조회한다`() {
        //given
        val products = (1..100).map {
            Product(
                id = it.toLong(),
                name = "상품$it",
                price = it.toLong(),
                quantity = (it * 1000)
            )
        }
        products.forEach(productRepository::save)

        //when
        val result = productService.findAllProducts()

        //then
        println(result.size)

        assertThat(result).hasSize(100)
    }

    @Test
    fun `인기 상품(판매량 기준 상위 5개 조회 시점)을 조회 한다`() {
        //given
        // 상품 저장
        val products = (1..100).map {
            Product(
                id = it.toLong(),
                name = "상품$it",
                price = it.toLong(),
                quantity = (it * 1000)
            )
        }
        products.forEach(productRepository::save)

        // 최근 3일 내 주문 생성
        val now = LocalDateTime.now()
        val orderItems = listOf(
            1L to 10, 2L to 20, 3L to 30, 4L to 25, 5L to 15, 6L to 5
        ).map { (productId, qty) ->
            OrderItem(
                orderId = 0,
                productId = productId,
                price = 1000,
                quantity = qty,
                createdAt = now.minusDays(1)
            )
        }
        orderItemRepository.saveAll(orderItems)

        //when
        val result = productService.findRankingByProducts()

        //then
        println(result.size)
        assertThat(result).hasSize(5)
    }


}