package kr.hhplus.be.server.interfaces.product

import kr.hhplus.be.server.application.product.ProductResult
import java.time.LocalDateTime

data class ProductRankingResponse(
    val rank: Int,
    val name: String,
    val price: Long,
    val salesCount: Int,
    val countedAt: LocalDateTime,
){
    companion object {
        fun from(
            productResult: ProductResult
        ): ProductRankingResponse {
            return ProductRankingResponse(
                rank = productResult.stat.ranking,
                name = productResult.product.name,
                price = productResult.product.price,
                salesCount = productResult.stat.salesCount.toInt(),
                countedAt = productResult.countedAt
            )
        }
    }
}
