package kr.hhplus.be.server.application.product

import kr.hhplus.be.server.domain.product.ProductRepository
import kr.hhplus.be.server.domain.stat.StatRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.cache.annotation.CachePut

@Component
class ProductScheduler(
    private val statRepository: StatRepository,
    private val productRepository: ProductRepository
) {

    // 매일 23:59에 캐시 갱신 (TTL 무관하게 최신 데이터로 덮어쓰기)
    @Scheduled(cron = "0 59 23 * * *")
    @CachePut(cacheNames = ["popularProducts"], key = "'ranking'")
    fun updatePopularProducts(): List<ProductResult> {
        val stats = statRepository.findAllOrderBySalesDesc()
        val ids = stats.map { it.productId }.distinct()
        val products = productRepository.findByIds(ids)
        val productMap = products.associateBy { it.id }
        return ProductResult.from(productMap, stats)
    }
}