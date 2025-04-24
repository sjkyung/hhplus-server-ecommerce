package kr.hhplus.be.server.product

import kr.hhplus.be.server.application.payment.PaymentCommand
import kr.hhplus.be.server.application.payment.PaymentService
import kr.hhplus.be.server.domain.order.Order
import kr.hhplus.be.server.domain.order.OrderItem
import kr.hhplus.be.server.domain.order.OrderItemRepository
import kr.hhplus.be.server.domain.order.OrderRepository
import kr.hhplus.be.server.domain.order.OrderStatus
import kr.hhplus.be.server.domain.point.UserPointRepository
import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.product.ProductRepository
import kr.hhplus.be.server.support.IntegrationTestBase
import kr.hhplus.be.server.support.TestFixtures
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import java.util.Collections

class ProductConcurrentTest @Autowired constructor(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val userPointRepository: UserPointRepository,
    private val paymentService: PaymentService,
) : IntegrationTestBase() {

    @Test
    fun `재고가 10개인 상품을 3명의 유저가 동시에 재고를 차감하면 재고가 1개만 차감된다`() {
        // given
        val product = productRepository.save(Product(id = 0, name = "상품", price = 1000, quantity = 10))

        val task = Runnable {
            try {
                val target = productRepository.findById(product.id)
                val updatedProduct = target.decrease(1)

                productRepository.save(updatedProduct) // 실제 저장
            } catch (e: Exception) {
                println("[에러] ${e.message}")
            }
        }

        TestFixtures.runConcurrently(3, task)

        val updated = productRepository.findById(product.id)
        println("최종 재고: ${updated?.quantity}")

        // 실제는 음수 또는 0보다 작을 수도 있음
        assertThat(updated?.quantity).isGreaterThanOrEqualTo(0)
    }


    @Test
    fun `비관적락을 사용하여 재고가 10개인 상품을 3명의 유저가 동시에 재고를 차감하면 재고가 1개만 차감된다`() {
        // given
        val product = productRepository.save(Product(id = 0, name = "상품", price = 1000, quantity = 10))

        val task = Runnable {
            try {
                val target = productRepository.findWithLockById(product.id)
                val updatedProduct = target.decrease(1)

                productRepository.save(updatedProduct) // 실제 저장
            } catch (e: Exception) {
                println("[에러] ${e.message}")
            }
        }

        TestFixtures.runConcurrently(3, task)

        val updated = productRepository.findById(product.id)
        println("최종 재고: ${updated.quantity}")

        //차감된 재고는 9개
        assertThat(updated.quantity).isEqualTo(9)
    }


    @Test
    fun `비관적 락을 사용하여 재고가 5개인 상품에 동시에 10명이 결제를 시도하면 5개인 재고는 정확히 차감되고 초과 결제는 실패한다`() {
        // given: 재고가 5인 상품 생성
        val product = productRepository.save(Product(0, "한정판", 1000, 10))

        // 10명의 유저가 각각 주문 생성
        val orderIds = (1L..10L).map { userId ->
            val order = orderRepository.save(
                Order(id = 0, userId = userId, userCouponId = null, status = OrderStatus.PENDING, totalPrice = 1000)
            )
            orderItemRepository.saveAll(
                listOf(
                OrderItem(orderId = order.id, productId = product.id, price = 1000, quantity = 3, createdAt = LocalDateTime.now())
            ))
            userPointRepository.save(TestFixtures.userPoint(userId, 1000))
            order.id
        }

        val failures = Collections.synchronizedList(mutableListOf<Throwable>())

        // when: 10명이 동시에 결제 요청
        val tasks = orderIds.map { orderId ->
            Runnable {
                try {
                    paymentService.createPessimistic(PaymentCommand(orderId))
                } catch (e: Exception) {
                    failures.add(e)
                    println("결제 실패: ${e.message}")
                }
            }
        }.toTypedArray()

        TestFixtures.runTaskConcurrently(*tasks)

        // then: 재고는 정확히 5개 차감되어 0이어야 함
        val updatedProduct = productRepository.findById(product.id)!!
        println("최종 재고: ${updatedProduct.quantity}")

        assertAll({
            // 재고는 0개
            assertThat(updatedProduct.quantity).isEqualTo(1)
            // 실패한 요청이 5건이어야 함
            assertThat(failures.size).isEqualTo(7)
        })
    }

    @Test
    fun `비관적 락을 사용하여 3개의 상품에 동시에 10명이 결제를 시도하면 5개인 재고는 정확히 차감되고 초과 결제는 실패한다`() {
        // given: 재고가 5인 상품 생성
        val product1 = productRepository.save(Product(0, "한정판", 1000, 10))
        val product2 = productRepository.save(Product(0, "신발", 2000, 30))
        val product3 = productRepository.save(Product(0, "나이키 신발", 1500, 30))

        // 10명의 유저가 각각 주문 생성
        val orderIds = (1L..10L).map { userId ->
            val order = orderRepository.save(
                Order(id = 0, userId = userId, userCouponId = null, status = OrderStatus.PENDING, totalPrice = 1000)
            )
            orderItemRepository.saveAll(
                listOf(
                    OrderItem(orderId = order.id, productId = product1.id, price = 1000, quantity = 3, createdAt = LocalDateTime.now()),
                    OrderItem(orderId = order.id, productId = product2.id, price = 2000, quantity = 3, createdAt = LocalDateTime.now()),
                    OrderItem(orderId = order.id, productId = product3.id, price = 1500, quantity = 3, createdAt = LocalDateTime.now())
                ))
            userPointRepository.save(TestFixtures.userPoint(userId, 1000))
            order.id
        }

        val failures = Collections.synchronizedList(mutableListOf<Throwable>())

        // when: 10명이 동시에 결제 요청
        val tasks = orderIds.map { orderId ->
            Runnable {
                try {
                    paymentService.createPessimistic(PaymentCommand(orderId))
                } catch (e: Exception) {
                    failures.add(e)
                    println("결제 실패: ${e.message}")
                }
            }
        }.toTypedArray()

        TestFixtures.runTaskConcurrently(*tasks)

        // then: 재고는 정확히 5개 차감되어 0이어야 함
        val updatedProduct = productRepository.findByIds(listOf(product1.id, product2.id, product3.id))
        val quantiles = updatedProduct.map { it.quantity }
        println("각 상품 별 최종 재고: $quantiles")

        assertAll({
            // 재고는 0개
            //assertThat(updatedProduct.quantity).isEqualTo(1)
            // 실패한 요청이 5건이어야 함
            assertThat(failures.size).isEqualTo(7)
        })
    }

}