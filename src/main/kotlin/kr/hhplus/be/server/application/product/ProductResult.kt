package kr.hhplus.be.server.application.product

import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.stat.Stat
import java.time.LocalDateTime

data class ProductResult(
    val product: Product,
    val stat: Stat,
    val countedAt: LocalDateTime,
){
    companion object {
        fun from(productMap: Map<Long, Product>, stats: List<Stat>): List<ProductResult> {
            return stats.mapNotNull { stat ->
                val product = productMap[stat.productId]
                product?.let {
                    ProductResult(
                        product = it,
                        stat = stat,
                        LocalDateTime.now(),
                    )
                }
            }
        }
    }
}
