package kr.hhplus.be.server.application.payment

import kr.hhplus.be.server.domain.coupon.CouponRepository
import kr.hhplus.be.server.domain.coupon.UserCouponRepository
import kr.hhplus.be.server.domain.order.OrderItemRepository
import kr.hhplus.be.server.domain.order.OrderRepository
import kr.hhplus.be.server.domain.payment.Payment
import kr.hhplus.be.server.domain.payment.PaymentRepository
import kr.hhplus.be.server.domain.point.UserPointRepository
import kr.hhplus.be.server.domain.product.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val userCouponRepository: UserCouponRepository,
    private val couponRepository: CouponRepository,
    private val pointRepository: UserPointRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository
) {

    @Transactional
    fun create(paymentCommand: PaymentCommand): Payment{
        val order = orderRepository.findById(paymentCommand.orderId)
        val orderItems = orderItemRepository.findByOrderId(paymentCommand.orderId)

        orderItems.forEach { item ->
            val product = productRepository.findById(item.productId)
            product.decrease(item.quantity)
            productRepository.save(product)
        }

        val orderPrice = order.userCouponId?.let{ couponId ->
            val userCoupon = userCouponRepository.findById(couponId)
            userCoupon.apply()
            userCouponRepository.save(userCoupon)

            val coupon = couponRepository.findById(couponId)
            coupon.calculateDiscountAmount(order.totalPrice)
        } ?: order.totalPrice


        order.userId.let { userId ->
            val userPoint = pointRepository.findByUserId(userId)

            userPoint.use(orderPrice)
            pointRepository.save(userPoint)
        }


        val payment = Payment.create(
            order.id,
            order.totalPrice
        )

        order.complete()
        return paymentRepository.save(payment)
    }

}