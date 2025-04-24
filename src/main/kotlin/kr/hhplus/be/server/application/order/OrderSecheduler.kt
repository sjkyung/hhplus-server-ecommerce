package kr.hhplus.be.server.application.order

import jakarta.transaction.Transactional
import kr.hhplus.be.server.domain.order.OrderRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OrderScheduler(
    private val orderRepository: OrderRepository
) {

    @Scheduled(cron = "0 */5 * * * *") // 매 5분마다 실행
    @Transactional
    fun cancelExpiredOrders() {
        val threshold = LocalDateTime.now().minusMinutes(30)
        val expiredOrders = orderRepository.findAllPendingOrdersBefore(threshold)

        expiredOrders.forEach { it.cancel() }

        orderRepository.saveAll(expiredOrders)
    }
}