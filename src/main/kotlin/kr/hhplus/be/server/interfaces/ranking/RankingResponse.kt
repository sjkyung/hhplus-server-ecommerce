package kr.hhplus.be.server.interfaces.ranking

import kr.hhplus.be.server.application.ranking.RankingResult


data class RankingResponse(
    val rank: Int,
    val name: String,
    val price: Long,
    val salesCount: Int,
){
    companion object {
        fun from(
            rankingResult: RankingResult
        ): RankingResponse {
            return RankingResponse(
                rank = rankingResult.rank,
                name = rankingResult.name,
                price = rankingResult.price,
                salesCount = rankingResult.salesCount
            )
        }
    }
}
