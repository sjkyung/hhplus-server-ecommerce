package kr.hhplus.be.server.application.ranking

import kr.hhplus.be.server.domain.product.Product

data class RankingResult(
    val rank: Int,
    val name: String,
    val price: Long,
    val salesCount: Int,
){
    companion object {
        fun from(rank: Int, product: Product, score: Double?): RankingResult {
            return RankingResult(
                rank = rank,
                name = product.name,
                price = product.price,
                salesCount = score?.toInt() ?: 0,
            )
        }
    }
}
