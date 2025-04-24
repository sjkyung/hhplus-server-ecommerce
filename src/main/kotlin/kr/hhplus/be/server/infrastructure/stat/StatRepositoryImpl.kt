package kr.hhplus.be.server.infrastructure.stat

import kr.hhplus.be.server.domain.stat.Stat
import kr.hhplus.be.server.domain.stat.StatRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class StatRepositoryImpl(
    private val jpaStatQueryRepository: JpaStatQueryRepository
): StatRepository {
    override fun findAllOrderBySalesDesc(): List<Stat> {
        val threeDaysAgo = LocalDateTime.now().minusDays(3)
        val size = PageRequest.of(0, 5)
        val statDto = jpaStatQueryRepository.findProductSales(threeDaysAgo,size);
        return StatConverter.toDomainList(statDto)
    }
}