package kr.hhplus.be.server.application.facade


import kr.hhplus.be.server.application.payment.PaymentCommand
import kr.hhplus.be.server.application.payment.PaymentService
import kr.hhplus.be.server.domain.order.OrderItemRepository
import kr.hhplus.be.server.domain.order.OrderRepository
import org.springframework.stereotype.Service

@Service
class PaymentFacade(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentService: PaymentService
) {

    fun productDecreaseSpinLock(paymentCommand: PaymentCommand){
        val order = orderRepository.findById(paymentCommand.orderId)
        val orderItems = orderItemRepository.findByOrderId(order.id)

        orderItems.sortedBy { it.productId }.forEach { item -> //데드락 방지를 위한 sort
            paymentService.decreaseWithSpinLock(item.productId,item.quantity)
        }
    }

    fun productDecreasePubSubLock(paymentCommand: PaymentCommand){
        val order = orderRepository.findById(paymentCommand.orderId)
        val orderItems = orderItemRepository.findByOrderId(order.id)

        orderItems.sortedBy { it.productId }.forEach { item -> //데드락 방지를 위한 sort
            paymentService.decreaseWithPubSubLock(item.productId,item.quantity)
        }
    }


}