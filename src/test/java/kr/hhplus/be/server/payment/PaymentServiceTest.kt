package kr.hhplus.be.server.payment

import kr.hhplus.be.server.application.payment.PaymentCommand
import kr.hhplus.be.server.application.payment.PaymentService
import kr.hhplus.be.server.domain.coupon.Coupon
import kr.hhplus.be.server.domain.coupon.CouponRepository
import kr.hhplus.be.server.domain.coupon.UserCoupon
import kr.hhplus.be.server.domain.coupon.UserCouponRepository
import kr.hhplus.be.server.domain.order.Order
import kr.hhplus.be.server.domain.order.OrderItem
import kr.hhplus.be.server.domain.order.OrderItemRepository
import kr.hhplus.be.server.domain.order.OrderRepository
import kr.hhplus.be.server.domain.payment.Payment
import kr.hhplus.be.server.domain.payment.PaymentRepository
import kr.hhplus.be.server.domain.point.UserPoint
import kr.hhplus.be.server.domain.point.UserPointRepository
import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.product.ProductRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.*

class PaymentServiceTest {

    private lateinit var userCouponRepository: UserCouponRepository
    private lateinit var couponRepository: CouponRepository
    private lateinit var pointRepository: UserPointRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var orderRepository: OrderRepository
    private lateinit var orderItemRepository: OrderItemRepository
    private lateinit var paymentRepository: PaymentRepository

    private lateinit var paymentService: PaymentService

    @BeforeEach
    fun init() {
        userCouponRepository = mock(UserCouponRepository::class.java)
        couponRepository = mock(CouponRepository::class.java)
        pointRepository = mock(UserPointRepository::class.java)
        productRepository = mock(ProductRepository::class.java)
        orderRepository = mock(OrderRepository::class.java)
        orderItemRepository = mock(OrderItemRepository::class.java)
        paymentRepository = mock(PaymentRepository::class.java)

        paymentService = PaymentService(
            userCouponRepository,
            couponRepository,
            pointRepository,
            productRepository,
            orderRepository,
            orderItemRepository,
            paymentRepository
        )
    }

    @Test
    fun `정상 결제 생성 테스트`() {
        // given
        val paymentCommand = PaymentCommand(orderId = 123L)
        // 주문 생성 (userCouponId, userId, totalPrice 등 필요한 값 설정)
        val order = Order.create(userId = 10L, totalPrice = 5000L, userCouponId = 200L)
        // 저장 후 id가 부여된 주문 객체
        val savedOrder = order.copy(id = 123L)
        `when`(orderRepository.findById(123L)).thenReturn(order)
        `when`(orderRepository.save(any(Order::class.java))).thenReturn(savedOrder)

        // 주문 항목 목록 (하나의 항목: productId=1L, quantity=2)
        val orderItem = mock(OrderItem::class.java)
        `when`(orderItemRepository.findByOrderId(123L)).thenReturn(listOf(orderItem))

        // 제품 조회: productRepository.findById 호출 시, 재고 감소 정상 동작하도록 처리
        val product = mock(Product::class.java)
        `when`(product.id).thenReturn(1L)
        // 재고 감소 호출 시 아무런 예외도 발생하지 않도록 처리
        doNothing().`when`(product).decrease(anyInt())
        `when`(productRepository.findById(anyLong())).thenReturn(product)

        // 쿠폰 관련: userCouponRepository와 couponRepository 처리
        val userCoupon = mock(UserCoupon::class.java)
        `when`(userCouponRepository.findById(200L)).thenReturn(userCoupon)
        // 쿠폰 사용 가능하면 apply() 아무런 문제 없이 실행
        doNothing().`when`(userCoupon).apply()
        `when`(userCouponRepository.save(userCoupon)).thenReturn(userCoupon)

        val coupon = mock(Coupon::class.java)
        `when`(couponRepository.findById(200L)).thenReturn(coupon)
        // 할인 금액 계산: 예를 들어 총 주문금액에서 할인 후 4000L이 되도록 함
        `when`(coupon.calculateDiscountAmount(order.totalPrice)).thenReturn(4000L)

        // 포인트 관련 처리
        val userPoint = mock(UserPoint::class.java)
        `when`(pointRepository.findByUserId(order.userId)).thenReturn(userPoint)
        doNothing().`when`(userPoint).validateAvailable(4000L)
        doNothing().`when`(userPoint).use(order.totalPrice)
        `when`(pointRepository.save(userPoint)).thenReturn(userPoint)

        // Payment 생성 처리
        val payment = Payment.create(savedOrder.id, savedOrder.totalPrice)
        `when`(paymentRepository.save(any(Payment::class.java))).thenReturn(payment)

        // when
        val result = paymentService.create(paymentCommand)

        // then
        assertThat(result).isEqualTo(payment)
        verify(orderRepository).findById(123L)
        verify(orderItemRepository).findByOrderId(123L)
        verify(productRepository, atLeastOnce()).findById(anyLong())
        verify(userCouponRepository).findById(200L)
        verify(couponRepository).findById(200L)
        verify(paymentRepository).save(any(Payment::class.java))
    }

    @Test
    fun `재고 부족으로 인한 결제 생성 실패 테스트`() {
        // given
        val paymentCommand = PaymentCommand(orderId = 123L)
        // userCoupon이 없는 주문 (또는 null로 처리)로 간주
        val order = Order.create(userId = 10L, totalPrice = 5000L, userCouponId = null)
        `when`(orderRepository.findById(123L)).thenReturn(order)

        // 주문 항목: 하나의 항목 (productId=1L, quantity=5)
        val orderItem = mock(OrderItem::class.java)
        `when`(orderItemRepository.findByOrderId(123L)).thenReturn(listOf(orderItem))

        // 제품 조회 시, 재고 부족 예외 발생하도록 설정
        val product = mock(Product::class.java)
        `when`(product.id).thenReturn(1L)
        doThrow(IllegalStateException("재고 부족")).`when`(product).decrease(anyInt())
        `when`(productRepository.findById(anyLong())).thenReturn(product)

        // when & then
        assertThatThrownBy {
            paymentService.create(paymentCommand)
        }.isInstanceOf(IllegalStateException::class.java)
    }

    // 실패 케이스 2: 쿠폰 사용 불가능으로 인한 결제 생성 실패 테스트
    @Test
    fun `쿠폰 사용 불가능으로 인한 결제 생성 시 예외 발생 테스트`() {
        // given
        val paymentCommand = PaymentCommand(orderId = 123L)
        val dummyOrder = Order.create(userId = 10L, totalPrice = 5000L, userCouponId = 200L)
        `when`(orderRepository.findById(123L)).thenReturn(dummyOrder)
        // 주문 항목 처리 정상 가정
        val dummyOrderItem = mock(OrderItem::class.java)
        `when`(orderItemRepository.findByOrderId(123L)).thenReturn(listOf(dummyOrderItem))
        // 제품은 정상 처리
        val dummyProduct = mock(Product::class.java)
        `when`(dummyProduct.id).thenReturn(1L)
        doNothing().`when`(dummyProduct).decrease(anyInt())
        `when`(productRepository.findById(anyLong())).thenReturn(dummyProduct)

        // 쿠폰 처리: userCoupon에서 isAvailable() 호출 시 예외 발생
        val dummyUserCoupon = mock(UserCoupon::class.java)
        `when`(userCouponRepository.findById(200L)).thenReturn(dummyUserCoupon)
        doThrow(IllegalStateException("사용 불가능한 쿠폰입니다.")).`when`(dummyUserCoupon).apply()

        // when & then
        assertThatThrownBy {
            paymentService.create(paymentCommand)
        }.isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("사용 불가능한 쿠폰입니다.")

        verify(userCouponRepository).findById(200L)
    }

    // 실패 케이스 3: 포인트 부족으로 인한 결제 생성 실패 테스트
    @Test
    fun `포인트 부족으로 인한 결제 생성 시 예외 발생 테스트`() {
        // given
        val paymentCommand = PaymentCommand(orderId = 123L)
        val dummyOrder = Order.create(userId = 10L, totalPrice = 5000L, userCouponId = null)
        `when`(orderRepository.findById(123L)).thenReturn(dummyOrder)
        val dummyOrderItem = mock(OrderItem::class.java)
        `when`(orderItemRepository.findByOrderId(123L)).thenReturn(listOf(dummyOrderItem))
        // 제품 정상 처리
        val dummyProduct = mock(Product::class.java)
        `when`(dummyProduct.id).thenReturn(1L)
        doNothing().`when`(dummyProduct).decrease(anyInt())
        `when`(productRepository.findById(anyLong())).thenReturn(dummyProduct)
        // 포인트 처리: 포인트 부족 예외 발생
        val dummyUserPoint = mock(UserPoint::class.java)
        `when`(pointRepository.findByUserId(dummyOrder.userId)).thenReturn(dummyUserPoint)
        doThrow(IllegalStateException("포인트 부족")).`when`(dummyUserPoint).validateAvailable(anyLong())

        // when & then
        assertThatThrownBy {
            paymentService.create(paymentCommand)
        }.isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("포인트 부족")

        verify(pointRepository).findByUserId(dummyOrder.userId)
    }

    // 실패 케이스 4: 할인 금액 계산 오류로 인한 결제 생성 실패 테스트
    @Test
    fun `할인 금액 계산 오류로 인한 결제 생성 시 예외 발생 테스트`() {
        // given
        val paymentCommand = PaymentCommand(orderId = 123L)
        val dummyOrder = Order.create(userId = 10L, totalPrice = 5000L, userCouponId = 200L)
        `when`(orderRepository.findById(123L)).thenReturn(dummyOrder)
        // 주문 항목 처리 정상 가정
        val dummyOrderItem = mock(OrderItem::class.java)
        `when`(orderItemRepository.findByOrderId(123L)).thenReturn(listOf(dummyOrderItem))
        // 제품 정상 처리
        val dummyProduct = mock(Product::class.java)
        `when`(dummyProduct.id).thenReturn(1L)
        doNothing().`when`(dummyProduct).decrease(anyInt())
        `when`(productRepository.findById(anyLong())).thenReturn(dummyProduct)
        // 쿠폰 처리: userCoupon 정상 처리
        val dummyUserCoupon = mock(UserCoupon::class.java)
        `when`(userCouponRepository.findById(200L)).thenReturn(dummyUserCoupon)
        doNothing().`when`(dummyUserCoupon).apply()
        `when`(userCouponRepository.save(dummyUserCoupon)).thenReturn(dummyUserCoupon)
        // 할인 계산 오류 발생
        val dummyCoupon = mock(Coupon::class.java)
        `when`(couponRepository.findById(200L)).thenReturn(dummyCoupon)
        doThrow(IllegalStateException("할인 금액 계산 오류"))
            .`when`(dummyCoupon).calculateDiscountAmount(dummyOrder.totalPrice)

        // 포인트 정상 처리
        val dummyUserPoint = mock(UserPoint::class.java)
        `when`(pointRepository.findByUserId(dummyOrder.userId)).thenReturn(dummyUserPoint)
        doNothing().`when`(dummyUserPoint).validateAvailable(anyLong())
        doNothing().`when`(dummyUserPoint).use(dummyOrder.totalPrice)
        `when`(pointRepository.save(dummyUserPoint)).thenReturn(dummyUserPoint)

        // when & then
        assertThatThrownBy {
            paymentService.create(paymentCommand)
        }.isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("할인 금액 계산 오류")

        verify(couponRepository).findById(200L)
    }
}