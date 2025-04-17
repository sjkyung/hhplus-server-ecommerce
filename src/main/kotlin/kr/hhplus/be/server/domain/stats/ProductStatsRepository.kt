package kr.hhplus.be.server.domain.stats

interface ProductStatsRepository {
    fun findAllOrderBySalesDesc() : List<ProductStat>
}