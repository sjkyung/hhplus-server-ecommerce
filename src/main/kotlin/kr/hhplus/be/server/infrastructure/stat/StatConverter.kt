package kr.hhplus.be.server.infrastructure.stat

import kr.hhplus.be.server.domain.stat.Stat

object StatConverter {

    fun toDomainList(productStatDtos: List<ProductStatDto>): List<Stat> {
        return productStatDtos.mapIndexed { index, dto ->
            Stat(
                ranking = index + 1,
                productId = dto.productId,
                salesCount = dto.totalSales
            )
        }
    }
}