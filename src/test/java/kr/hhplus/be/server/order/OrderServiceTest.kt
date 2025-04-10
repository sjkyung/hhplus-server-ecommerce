package kr.hhplus.be.server.order

import kr.hhplus.be.server.application.order.OrderCommand
import kr.hhplus.be.server.application.order.OrderItemCommand
import kr.hhplus.be.server.application.order.OrderService
import kr.hhplus.be.server.domain.coupon.UserCoupon
import kr.hhplus.be.server.domain.coupon.UserCouponRepository
import kr.hhplus.be.server.domain.order.Order
import kr.hhplus.be.server.domain.order.OrderItemRepository
import kr.hhplus.be.server.domain.order.OrderRepository
import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.product.ProductRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mockito.*
import java.lang.IllegalStateException


class OrderServiceTest {

    private lateinit var orderRepository: OrderRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var userCouponRepository: UserCouponRepository
    private lateinit var orderItemRepository: OrderItemRepository
    private lateinit var orderService: OrderService

    @BeforeEach
    fun init() {
        orderRepository = mock(OrderRepository::class.java)
        productRepository = mock(ProductRepository::class.java)
        userCouponRepository = mock(UserCouponRepository::class.java)
        orderItemRepository = mock(OrderItemRepository::class.java)
        orderService = OrderService(
            orderRepository,
            productRepository,
            userCouponRepository,
            orderItemRepository
        )
    }

    @Test
    fun `정상적으로 주문 생성 시 올바른 주문 반환 테스트`() {
        // given
        val orderCommand = OrderCommand(
            userId = 10L,
            items = listOf(OrderItemCommand(productId = 1L, quantity = 2)),
            userCouponId = 200L
        )
        val Product = Product(1L, "나이키 신발",1000L,100)
        val productsList = listOf(Product)
        `when`(productRepository.findByIds(orderCommand.items.map { it.productId }))
            .thenReturn(productsList)
        `when`(userCouponRepository.findById(200L))
            .thenReturn(mock(UserCoupon::class.java))
        val dummyOrder = Order.create(orderCommand.userId, productsList.sumOf { it.price }, orderCommand.userCouponId)
        val savedOrder = dummyOrder.copy(id = 100L)
        `when`(orderRepository.save(any(Order::class.java)))
            .thenReturn(savedOrder)
        val dummyOrderItems = orderCommand.toDomain(savedOrder.id, productsList)
        `when`(orderItemRepository.saveAll(anyList()))
            .thenReturn(dummyOrderItems)

        // when
        val result = orderService.create(orderCommand)

        // then
        assertThat(result.id).isEqualTo(100L)
        verify(productRepository).findByIds(orderCommand.items.map { it.productId })
        verify(userCouponRepository).findById(200L)
        verify(orderRepository).save(any(Order::class.java))
        verify(orderItemRepository).saveAll(anyList())
    }


    @Test
    fun `재고 부족으로 인한 주문 생성 시 예외 발생 테스트`() {
        // given
        val orderCommand = OrderCommand(
            userId = 10L,
            items = listOf(OrderItemCommand(productId = 1L, quantity = 5)), // 수량 5개 요청
            userCouponId = null
        )

        val mockProduct = mock(Product::class.java)
        `when`(mockProduct.id).thenReturn(1L)
        `when`(mockProduct.price).thenReturn(1000L)

        doThrow(IllegalStateException("재고 부족"))
            .`when`(mockProduct).validateStock(6)

        `when`(productRepository.findByIds(orderCommand.items.map { it.productId }))
            .thenReturn(listOf(mockProduct))

        // when & then
        assertThatThrownBy {
            orderService.create(orderCommand)
        }.isInstanceOf(IllegalStateException::class.java)

        verify(productRepository,times(1)).findByIds(orderCommand.items.map { it.productId })
    }

    @Test
    fun `쿠폰 사용 불가능한 경우 주문 생성 시 예외 발생 테스트`() {
        // given
        val orderCommand = OrderCommand(
            userId = 10L,
            items = listOf(OrderItemCommand(productId = 1L, quantity = 2)),
            userCouponId = 200L
        )
        val product = Product(1L, "나이키 신발",1000L,100)
        val productsList = listOf(product)
        `when`(productRepository.findByIds(orderCommand.items.map { it.productId }))
            .thenReturn(productsList)
        // 쿠폰 사용 불가능 상황을 시뮬레이션: userCoupon의 isAvailable() 호출 시 예외 발생
        val Coupon = mock(UserCoupon::class.java)
        doThrow(IllegalStateException("사용 불가능한 쿠폰입니다."))
            .`when`(Coupon).isAvailable()
        `when`(userCouponRepository.findById(200L))
            .thenReturn(Coupon)

        // when & then
        assertThatThrownBy {
            orderService.create(orderCommand)
        }.isInstanceOf(IllegalStateException::class.java)

        verify(userCouponRepository,times(1)).findById(200L)
    }
}