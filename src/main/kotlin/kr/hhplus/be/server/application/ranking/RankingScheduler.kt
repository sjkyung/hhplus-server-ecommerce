package kr.hhplus.be.server.application.ranking

import kr.hhplus.be.server.domain.stat.StatRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields

@Component
class RankingScheduler(
    private val statRepository: StatRepository
) {

    @Scheduled(cron = "0 1 0 * * *") // 매일 00:01, 일간 키 TTL 3일
    fun expireDailyRankingKey() {
        val todayKey = "ranking:daily:${LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)}"
        statRepository.setKeyTtl(todayKey, Duration.ofDays(1))
    }

    @Scheduled(cron = "0 2 0 * * MON") // 매주 월요일 00:02, 주간 키 TTL 7일
    fun expireWeeklyRankingKey() {
        val now = LocalDate.now()
        val week = now.get(WeekFields.ISO.weekOfWeekBasedYear())
        val year = now.get(WeekFields.ISO.weekBasedYear())
        val key = "ranking:weekly:$year-W${week.toString().padStart(2, '0')}"

        statRepository.setKeyTtl(key, Duration.ofDays(7))
    }
}