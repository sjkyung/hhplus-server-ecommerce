package kr.hhplus.be.server.application.ranking

import kr.hhplus.be.server.domain.product.ProductRepository
import kr.hhplus.be.server.domain.stat.StatRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class RankingService(
    private val statRepository: StatRepository,
    private val productRepository: ProductRepository
) {


    fun getTop5DailyRanking() : List<RankingResult>{
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.BASIC_ISO_DATE

        val keys = (0..2).map{
            today.minusDays(it.toLong()).format(formatter)
        }.map { "ranking:daily:$it" }

        val scoreMap = mutableMapOf<String, Double>()

        keys.forEach { key ->
            val entries = statRepository.getTopN(key, 5)
            println("Redis 키: $key")
            println("조회된 entry: $entries")
            entries.forEach {(productId, score) ->
                scoreMap[productId] = scoreMap.getOrDefault(productId, 0.0) + score
            }
        }


        val top5ProductIds = scoreMap.entries
            .sortedByDescending { it.value }
            .take(5).map { it.key.toLong() }


        val products = productRepository.findByIds(top5ProductIds.map { it })
        val productMap = products.associateBy { it.id }


        return top5ProductIds.mapIndexed { index, productId ->
            val product = productMap[productId] ?: throw IllegalStateException("Product not found id=$productId")
            RankingResult.from(index + 1, product, scoreMap[productId.toString()]
            )
        }


    }

    fun getTop5WeeklyRanking() : List<RankingResult>{
        val now = LocalDate.now()
        val week = now.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear())
        val year = now.get(java.time.temporal.WeekFields.ISO.weekBasedYear())
        val key = "ranking:weekly:$year-W${week.toString().padStart(2, '0')}"

        val entries = statRepository.getTopN(key, 5)

        val productIds = entries.map { it.first.toLong() }
        val scoreMap = entries.toMap()

        val products = productRepository.findByIds(productIds.map { it })
        val productMap = products.associateBy { it.id }

        return productIds.mapIndexed { index, productId ->
            val product =
                productMap[productId] ?: throw IllegalStateException("Product not found id=$productId")
            RankingResult.from(index + 1, product, scoreMap[productId.toString()]
            )
        }
    }
}