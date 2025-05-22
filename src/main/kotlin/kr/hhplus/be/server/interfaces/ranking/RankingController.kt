package kr.hhplus.be.server.interfaces.ranking

import kr.hhplus.be.server.ApiResponse
import kr.hhplus.be.server.application.product.ProductResult
import kr.hhplus.be.server.application.ranking.RankingService
import kr.hhplus.be.server.interfaces.product.ProductRankingResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class RankingController(
    private val rankingsService: RankingService
) {

    @GetMapping("/rank/daily")
    fun getRankingDaily(): ApiResponse<List<RankingResponse>> {
        val rankingProducts = rankingsService.getTop5DailyRanking()
        val response = rankingProducts.map { RankingResponse.from(it) }
        return ApiResponse.success(response)
    }

    @GetMapping("/rank/weekly")
    fun getRankingWeekly(): ApiResponse<List<RankingResponse>> {
        val rankingProducts = rankingsService.getTop5WeeklyRanking()
        val response = rankingProducts.map { RankingResponse.from(it) }
        return ApiResponse.success(response)
    }
}