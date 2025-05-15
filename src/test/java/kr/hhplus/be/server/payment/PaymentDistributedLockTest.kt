package kr.hhplus.be.server.payment

import kr.hhplus.be.server.application.facade.PaymentFacade
import kr.hhplus.be.server.application.payment.PaymentCommand
import kr.hhplus.be.server.application.payment.PaymentService
import kr.hhplus.be.server.domain.coupon.Coupon
import kr.hhplus.be.server.domain.coupon.CouponRepository
import kr.hhplus.be.server.domain.coupon.CouponStatus
import kr.hhplus.be.server.domain.coupon.UserCoupon
import kr.hhplus.be.server.domain.coupon.UserCouponRepository
import kr.hhplus.be.server.domain.order.Order
import kr.hhplus.be.server.domain.order.OrderItem
import kr.hhplus.be.server.domain.order.OrderItemRepository
import kr.hhplus.be.server.domain.order.OrderRepository
import kr.hhplus.be.server.domain.order.OrderStatus
import kr.hhplus.be.server.domain.payment.Payment
import kr.hhplus.be.server.domain.point.UserPoint
import kr.hhplus.be.server.domain.point.UserPointRepository
import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.product.ProductRepository
import kr.hhplus.be.server.support.IntegrationTestBase
import kr.hhplus.be.server.support.TestFixtures
import org.springframework.context.ApplicationContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import java.util.Collections

class PaymentDistributedLockTest @Autowired constructor(
    private val paymentFacade: PaymentFacade,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val applicationContext: ApplicationContext,
    private val couponRepository: CouponRepository,
    private val userCouponRepository: UserCouponRepository,
    private val userPointRepository: UserPointRepository,
    private val paymentService: PaymentService,
) : IntegrationTestBase() {

    @DisplayName("aop 등록 여부 확인")
    @Test
    fun checkDistributedLockAspect() {
        val beans = applicationContext.getBeansOfType(Class.forName("kr.hhplus.be.server.application.lock.DistributedLockAspect"))
        println("DistributedLockAspect 등록 여부: ${beans.keys}")
    }


    @Test
    fun `spin lock 적용 시 - 2번 동시 요청이 들어왔을때 정상적으로 결제가 완료된다`() {
        // given
        val product = productRepository.save(
            Product(id = 0, name = "상품", price = 2000L, quantity = 10)
        )
        val coupon = couponRepository.save(
            Coupon(
                couponId = 0,
                "선착순 쿠폰",
                1000,
                100,
                LocalDateTime.now().plusDays(1)
            )
        )

        val userCoupon = userCouponRepository.save(
            UserCoupon(
                userCouponId = 0,
                userId = 1L,
                couponId = coupon.couponId,
                couponStatus = CouponStatus.AVAILABLE,
                issuedAt = LocalDateTime.now().minusDays(1),
                usedAt = null
            )
        )

        val order = orderRepository.save(
            Order(
                id = 0,
                userId = 1L,
                userCouponId = userCoupon.userCouponId,
                status = OrderStatus.PENDING,
                totalPrice = 2000L
            )
        )

        orderItemRepository.saveAll(
            listOf(
                OrderItem(
                    orderId = order.id!!,
                    productId = product.id,
                    price = 2000L,
                    quantity = 1,
                    createdAt = LocalDateTime.now()
                )
            )
        )

        userPointRepository.save(UserPoint(id = 0, userId = 1L, point = 1000L,0))

        // when
        val failures = Collections.synchronizedList(mutableListOf<Throwable>())
        var paymentId: Payment? = null
        TestFixtures.runConcurrently(2) {
            try {
                val command = PaymentCommand(orderId = order.id)
                paymentId = paymentService.createPessimistic(command) // 성공
            } catch (e: Exception) {
                failures.add(e)
            }
        }


        // then
        val updatedOrder = orderRepository.findById(order.id)
        val updatedProduct = productRepository.findById(product.id)
        val updatedPoint = userPointRepository.findByUserId(1L)
        val updatedCoupon = userCouponRepository.findById(userCoupon.userCouponId)

        assertThat(paymentId).isNotNull()
        assertThat(updatedOrder.status).isEqualTo(OrderStatus.COMPLETED)
        assertThat(updatedProduct.quantity).isEqualTo(9) // 1개 차감
        assertThat(updatedPoint.point).isEqualTo(0)      // 1000 포인트 사용
        assertThat(updatedCoupon.couponStatus).isEqualTo(CouponStatus.USED)
    }


    @Test
    fun `spin lock 적용시 동일한 productId(재고가 10개)에 대해 동시에 요청이 5개 들어왔을때 5개 성공한다`() {
        // given
        val product = productRepository.save(Product(0, "상품1", 1000, 10))

        val order = orderRepository.save(
            Order(
                id = 0,
                userId = 1L,
                userCouponId = null,
                status = OrderStatus.PENDING,
                totalPrice = 1000L
            )
        )
        orderItemRepository.saveAll(
            listOf(
                OrderItem(
                    orderId = order.id!!,
                    productId = product.id,
                    price = 1000L,
                    quantity = 1,
                    LocalDateTime.now()
                )
            )
        )

        val failures = Collections.synchronizedList(mutableListOf<Throwable>())

        TestFixtures.runConcurrently(5) {
            try {
                val command = PaymentCommand(orderId = order.id)
                paymentFacade.productDecreaseSpinLock(command)
            } catch (e: Exception) {
                failures.add(e)
            }
        }

        // then
        val updatedProduct = productRepository.findById(product.id)
        assertThat(updatedProduct.quantity).isEqualTo(5)
        assertThat(failures.size).isEqualTo(0)
    }


    @Test
    fun `spin lock 적용시 동일한 productId(재고가 10개)에 대해 동시에 요청이 15개 들어왔을때 10개 성공하고 5개는 실패한다`() {
        // given
        val product = productRepository.save(Product(0, "상품1", 1000, 10))

        val order = orderRepository.save(
            Order(
                id = 0,
                userId = 1L,
                userCouponId = null,
                status = OrderStatus.PENDING,
                totalPrice = 1000L
            )
        )
        orderItemRepository.saveAll(
            listOf(
                OrderItem(
                    orderId = order.id!!,
                    productId = product.id,
                    price = 1000L,
                    quantity = 1,
                    LocalDateTime.now()
                )
            )
        )

        val failures = Collections.synchronizedList(mutableListOf<Throwable>())

        TestFixtures.runConcurrently(15) {
            try {
                val command = PaymentCommand(orderId = order.id)
                paymentFacade.productDecreaseSpinLock(command)
            } catch (e: Exception) {
                failures.add(e)
            }
        }

        // then
        val updatedProduct = productRepository.findById(product.id)
        assertThat(updatedProduct.quantity).isEqualTo(0)
        assertThat(failures.size).isEqualTo(5)
    }



    @Test
    fun `pub-sub 락 적용 시 동일한 productId(재고가 10개)에 대해 동시에 요청이 5개 들어왔을때 5개 성공한다`() {
        // given
        val product = productRepository.save(Product(0, "상품1", 1000, 10))

        val order = orderRepository.save(
            Order(
                id = 0,
                userId = 1L,
                userCouponId = null,
                status = OrderStatus.PENDING,
                totalPrice = 1000L
            )
        )
        orderItemRepository.saveAll(
            listOf(
                OrderItem(
                    orderId = order.id!!,
                    productId = product.id,
                    price = 1000L,
                    quantity = 1,
                    LocalDateTime.now()
                )
            )
        )

        val failures = Collections.synchronizedList(mutableListOf<Throwable>())

        TestFixtures.runConcurrently(5) {
            try {
                val command = PaymentCommand(orderId = order.id)
                paymentFacade.productDecreasePubSubLock(command)
            } catch (e: Exception) {
                failures.add(e)
            }
        }

        // then
        val updatedProduct = productRepository.findById(product.id)
        assertThat(updatedProduct.quantity).isEqualTo(5)
        assertThat(failures.size).isEqualTo(0)
    }


    @Test
    fun `pub-sub 락 적용 시 동일한 productId(재고가 10개)에 대해 동시에 요청이 15개 들어왔을때 10개 성공하고 5개는 실패한다`() {
        // given
        val product = productRepository.save(Product(0, "상품1", 1000, 10))

        val order = orderRepository.save(
            Order(
                id = 0,
                userId = 1L,
                userCouponId = null,
                status = OrderStatus.PENDING,
                totalPrice = 1000L
            )
        )
        orderItemRepository.saveAll(
            listOf(
                OrderItem(
                    orderId = order.id!!,
                    productId = product.id,
                    price = 1000L,
                    quantity = 1,
                    LocalDateTime.now()
                )
            )
        )

        val failures = Collections.synchronizedList(mutableListOf<Throwable>())

        TestFixtures.runConcurrently(15) {
            try {
                val command = PaymentCommand(orderId = order.id)
                paymentFacade.productDecreasePubSubLock(command)
            } catch (e: Exception) {
                failures.add(e)
            }
        }

        // then
        val updatedProduct = productRepository.findById(product.id)
        assertThat(updatedProduct.quantity).isEqualTo(0)
        assertThat(failures.size).isEqualTo(5)
    }

}




