package kr.hhplus.be.server.application.event

import kr.hhplus.be.server.domain.stat.StatRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields

@Component
class ProductSaleEventListener(
    private val statRepository: StatRepository
) {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onProductSaleCompleted(event: ProductSaleCompletedEvent) {
        val now = LocalDate.now()
        val dailyKey = "ranking:daily:${now.format(DateTimeFormatter.BASIC_ISO_DATE)}"

        val week = now.get(WeekFields.ISO.weekOfWeekBasedYear())
        val year = now.get(WeekFields.ISO.weekBasedYear())
        val weeklyKey = "ranking:weekly:$year-W${week.toString().padStart(2, '0')}"

        statRepository.increaseScore(dailyKey, event.productId.toString(), event.quantity.toLong())
        statRepository.increaseScore(weeklyKey, event.productId.toString(), event.quantity.toLong())
    }
}