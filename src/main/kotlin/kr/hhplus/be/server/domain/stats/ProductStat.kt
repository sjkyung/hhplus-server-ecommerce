package kr.hhplus.be.server.domain.stats

data class ProductStat(
    val ranking: Int,
    val productId: Long,
    val salesCount: Long
) {
}