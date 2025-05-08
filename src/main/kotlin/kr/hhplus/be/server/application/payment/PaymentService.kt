package kr.hhplus.be.server.application.payment

import kr.hhplus.be.server.application.lock.DistributedLock
import kr.hhplus.be.server.application.lock.LockType
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
            val updatedProduct = product.decrease(item.quantity)
            productRepository.save(updatedProduct)
        }

        val orderPrice = order.userCouponId?.let{ couponId ->
            val userCoupon = userCouponRepository.findById(couponId)
            val applyUserCoupon = userCoupon.apply()
            userCouponRepository.save(applyUserCoupon)

            val coupon = couponRepository.findById(couponId)
            coupon.calculateDiscountAmount(order.totalPrice)
        } ?: order.totalPrice


        order.userId.let { userId ->
            val userPoint = pointRepository.findByUserId(userId)

            val usedUserPoint = userPoint.use(orderPrice)
            pointRepository.save(usedUserPoint)
        }


        val payment = Payment.create(
            order.id,
            order.totalPrice
        )

        val updatedOrder = order.complete()
        orderRepository.save(updatedOrder)
        return paymentRepository.save(payment)
    }

    @DistributedLock(key = "'payment-lock-' + #paymentCommand.orderId", lockType = LockType.REDIS_SPIN)
    @Transactional
    fun createPessimistic(paymentCommand: PaymentCommand): Payment{
        val order = orderRepository.findById(paymentCommand.orderId)
        val orderItems = orderItemRepository.findByOrderId(order.id)

        orderItems.sortedBy { it.productId }.forEach { item ->
            val product = productRepository.findWithLockById(item.productId)
            val updatedProduct = product.decrease(item.quantity)
            productRepository.save(updatedProduct)
        }

        val orderPrice = order.userCouponId?.let{ couponId ->
            val userCoupon = userCouponRepository.findById(couponId)
            val applyUserCoupon = userCoupon.apply()
            userCouponRepository.save(applyUserCoupon)

            val coupon = couponRepository.findById(couponId)
            coupon.calculateDiscountAmount(order.totalPrice)
        } ?: order.totalPrice


        order.userId.let { userId ->
            val userPoint = pointRepository.findByUserId(userId)

            val usedUserPoint = userPoint.use(orderPrice)
            pointRepository.save(usedUserPoint)
        }


        val payment = Payment.create(
            order.id,
            order.totalPrice
        )

        val updatedOrder = order.complete()
        orderRepository.save(updatedOrder)
        return paymentRepository.save(payment)
    }


    @DistributedLock(key = "'product-lock-' + #productId", lockType = LockType.REDIS_SPIN)
    @Transactional
    fun decreaseWithSpinLock(productId: Long, quantity: Int) {
        val product = productRepository.findWithLockById(productId)
        val updated = product.decrease(quantity)
        productRepository.save(updated)
    }

    @DistributedLock(key = "'product-lock-' + #productId", lockType = LockType.REDIS_PUB_SUB)
    @Transactional
    fun decreaseWithPubSubLock(productId: Long, quantity: Int) {
        val product = productRepository.findWithLockById(productId)
        val updated = product.decrease(quantity)
        productRepository.save(updated)
    }
}