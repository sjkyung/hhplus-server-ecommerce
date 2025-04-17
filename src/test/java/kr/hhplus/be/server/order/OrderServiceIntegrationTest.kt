package kr.hhplus.be.server.order

import kr.hhplus.be.server.application.order.OrderCommand
import kr.hhplus.be.server.application.order.OrderItemCommand
import kr.hhplus.be.server.application.order.OrderService
import kr.hhplus.be.server.domain.coupon.CouponStatus
import kr.hhplus.be.server.domain.coupon.UserCoupon
import kr.hhplus.be.server.domain.coupon.UserCouponRepository
import kr.hhplus.be.server.domain.order.OrderItemRepository
import kr.hhplus.be.server.domain.order.OrderRepository
import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.product.ProductRepository
import kr.hhplus.be.server.support.IntegrationTestBase
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

class OrderServiceIntegrationTest @Autowired constructor(
    private val orderService: OrderService,
    private val productRepository: ProductRepository,
    private val userCouponRepository: UserCouponRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
) : IntegrationTestBase() {


    @Test
    fun `존재하지 않는 상품으로 주문 시 IllegalStateException 예외가 발생한다`() {
        // given
        val nonExistentProductId = 999L // 존재하지 않는 ID

        val orderCommand = OrderCommand(
            userId = 1L,
            items = listOf(
                OrderItemCommand(productId = nonExistentProductId, quantity = 1)
            ),
            userCouponId = null
        )

        // when & then
        assertThatThrownBy {
            orderService.create(orderCommand)
        }.isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("해당 상품을 찾을 수 없습니다")
    }

    @Test
    fun `재고가 부족하면 주문에 실패시 IllegalStateException이 발생한다`() {
        // given
        val product = productRepository.save(
            Product(
                id = 1L,
                name = "품절 상품",
                price = 500L,
                quantity = 1
            )
        )

        val orderCommand = OrderCommand(
            userId = 1L,
            items = listOf(OrderItemCommand(productId = product.id, quantity = 10)),
            userCouponId = null
        )

        // when & then
        assertThatThrownBy {
            orderService.create(orderCommand)
        }.isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("재고가 부족합니다.")
    }


    @Test
    fun `쿠폰이 사용 불가 상태면 주문에 실패시 IllegalStateException이 발생한다`() {
        // given
        val product = productRepository.save(
            Product(
                id = 1L,
                name = "상품",
                price = 500L,
                quantity = 1
            )
        )
        val usedCoupon = userCouponRepository.save(
            UserCoupon(
                userCouponId = 0,
                userId = 1,
                couponId = 10,
                couponStatus = CouponStatus.USED,
                issuedAt = LocalDateTime.now().minusDays(1),
                usedAt = LocalDateTime.now()
            )
        )

        val orderCommand = OrderCommand(
            userId = 1L,
            items = listOf(OrderItemCommand(productId = product.id, quantity = 1)),
            userCouponId = usedCoupon.userCouponId
        )

        // when & then
        assertThatThrownBy {
            orderService.create(orderCommand)
        }.isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("쿠폰이 이미 사용되었거나 만료되었습니다.")
    }


    @Test
    fun `정상 주문이 생성된다`() {
        // given
        val product = productRepository.save(
            Product(
                id = 1L,
                name = "나이키 신발",
                price = 1000L,
                quantity = 10
            )
        )
        val userCoupon = userCouponRepository.save(
            UserCoupon(
                userCouponId = 0,
                userId = 1,
                couponId = 10,
                couponStatus = CouponStatus.AVAILABLE,
                issuedAt = LocalDateTime.now().minusDays(1),
                usedAt = null
            )
        )

        val orderCommand = OrderCommand(
            userId = 1L,
            items = listOf(OrderItemCommand(productId = product.id, quantity = 1)),
            userCouponId = userCoupon.userCouponId
        )

        // when
        val result = orderService.create(orderCommand)

        // then
        val savedOrder = orderRepository.findById(result.id)
        val orderItems = orderItemRepository.findByOrderId(result.id)

        assertAll({
            assertThat(savedOrder).isNotNull
            assertThat(orderItems).hasSize(1)
        })
    }

}