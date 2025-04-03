package kr.hhplus.be.server.controller.product.response

import java.time.LocalDateTime

data class ProductRankingResponse(
    val rank: Int,
    val name: String,
    val price: Long,
    val salesCount: Int,
    val countedAt: LocalDateTime,
)
