package kr.hhplus.be.server.payment

import kr.hhplus.be.server.application.payment.PaymentCommand
import kr.hhplus.be.server.application.payment.PaymentService
import kr.hhplus.be.server.domain.coupon.*
import kr.hhplus.be.server.domain.order.*
import kr.hhplus.be.server.domain.point.UserPoint
import kr.hhplus.be.server.domain.point.UserPointRepository
import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.product.ProductRepository
import kr.hhplus.be.server.support.IntegrationTestBase
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

class PaymentServiceIntegrationTest @Autowired constructor(
    private val paymentService: PaymentService,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val userPointRepository: UserPointRepository,
    private val productRepository: ProductRepository,
    private val userCouponRepository: UserCouponRepository,
    private val couponRepository: CouponRepository
) : IntegrationTestBase() {


    @Test
    fun `존재하지 않는 주문 ID로 결제 시 예외가 발생한다`() {
        // given
        val command = PaymentCommand(orderId = 999L) // 존재하지 않음

        // when&then
        assertThatThrownBy {
            paymentService.create(command)
        }.isInstanceOf(NoSuchElementException::class.java)
    }

    @Test
    fun `존재하지 않는 상품 ID로 결제 시 예외가 발생한다`() {
        //given
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
                    productId = 999L,
                    price = 1000L,
                    quantity = 1,
                    LocalDateTime.now()
                )
            )
        ) //없음
        userPointRepository.save(UserPoint(id = 0, userId = 1L, point = 2000L))

        //when & then
        val command = PaymentCommand(orderId = order.id!!)
        assertThatThrownBy {
            paymentService.create(command)
        }.isInstanceOf(NoSuchElementException::class.java)
    }


    @Test
    fun `재고가 부족하면 결제 시 예외가 발생한다`() {
        //given
        val product = productRepository.save(
            Product(id = 0, name = "상품", price = 1000L, quantity = 0)
        ) // 재고 부족

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
        userPointRepository.save(UserPoint(id = 0, userId = 1L, point = 2000L))

        //when & then
        val command = PaymentCommand(orderId = order.id!!)
        assertThatThrownBy {
            paymentService.create(command)
        }.isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `존재하지 않는 쿠폰 ID로 결제 시 예외가 발생한다`() {
        //given
        val product = productRepository.save(
            Product(id = 0, name = "상품", price = 1000L, quantity = 10)
        )

        val order = orderRepository.save(
            Order(
                id = 0,
                userId = 1L,
                userCouponId = 999L, //없는 쿠폰
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
        userPointRepository.save(UserPoint(id = 0, userId = 1L, point = 2000L))

        //when & then
        val command = PaymentCommand(orderId = order.id!!)
        assertThatThrownBy {
            paymentService.create(command)
        }.isInstanceOf(NoSuchElementException::class.java)
    }


    @Test
    fun `이미 사용된 쿠폰으로 결제 시 예외가 발생한다`() {
        //given
        val product = productRepository.save(
            Product(id = 0, name = "상품", price = 1000L, quantity = 10)
        )

        val userCoupon = userCouponRepository.save(
            UserCoupon(
                userCouponId = 0,
                userId = 1L,
                couponId = 1L,
                couponStatus = CouponStatus.USED, //사용 쿠폰
                issuedAt = LocalDateTime.now().minusDays(1),
                usedAt = LocalDateTime.now()
            )
        )

        val order = orderRepository.save(
            Order(
                id = 0,
                userId = 1L,
                userCouponId = userCoupon.userCouponId,
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
        userPointRepository.save(UserPoint(id = 0, userId = 1L, point = 2000L))

        //when & then
        val command = PaymentCommand(orderId = order.id!!)
        assertThatThrownBy {
            paymentService.create(command)
        }.isInstanceOf(IllegalStateException::class.java)

    }


    @Test
    fun `만료된 쿠폰으로 결제 시 예외가 발생한다`() {
        //given
        val product = productRepository.save(
            Product(id = 0, name = "상품", price = 1000L, quantity = 10)
        )

        val userCoupon = userCouponRepository.save(
            UserCoupon(
                userCouponId = 0,
                userId = 1L,
                couponId = 1L,
                couponStatus = CouponStatus.EXPIRED, //사용 쿠폰
                issuedAt = LocalDateTime.now().minusDays(50),
                usedAt = null
            )
        )

        val order = orderRepository.save(
            Order(
                id = 0,
                userId = 1L,
                userCouponId = userCoupon.userCouponId,
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
        userPointRepository.save(UserPoint(id = 0, userId = 1L, point = 2000L))

        //when & then
        val command = PaymentCommand(orderId = order.id!!)
        assertThatThrownBy {
            paymentService.create(command)
        }.isInstanceOf(IllegalStateException::class.java)

    }


    @Test
    fun `포인트가 부족하면 결제 시 예외가 발생한다`() {
        //given
        val product = productRepository.save(Product(id = 0, name = "상품", price = 2000L, quantity = 1))

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
                couponStatus = CouponStatus.AVAILABLE, //사용 쿠폰
                issuedAt = LocalDateTime.now(),
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
                    price = 2000L, quantity = 1,
                    LocalDateTime.now()
                )
            )
        )
        userPointRepository.save(UserPoint(id = 0, userId = 1L, point = 500L)) //포인트 부족

        //when & then
        val command = PaymentCommand(orderId = order.id!!)
        assertThatThrownBy {
            paymentService.create(command)
        }.isInstanceOf(IllegalStateException::class.java)

    }


    @Test
    fun `정상적으로 결제가 완료된다`() {
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

        userPointRepository.save(UserPoint(id = 0, userId = 1L, point = 1000L))

        // when
        val command = PaymentCommand(orderId = order.id)
        val paymentId = paymentService.create(command) // 성공

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


}