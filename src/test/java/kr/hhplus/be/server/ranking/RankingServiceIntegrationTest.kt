package kr.hhplus.be.server.ranking

import kr.hhplus.be.server.application.ranking.RankingService
import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.product.ProductRepository
import kr.hhplus.be.server.support.IntegrationTestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.redisson.api.RedissonClient
import org.redisson.client.codec.StringCodec
import org.springframework.beans.factory.annotation.Autowired

class RankingServiceIntegrationTest @Autowired constructor(
    private val rankingService: RankingService,
    private val productRepository: ProductRepository,
    private val redissonClient: RedissonClient
) : IntegrationTestBase() {


    @BeforeEach
    fun init() {
        val scores = listOf(
            100.0 to "1",
            50.0 to "2",
            90.0 to "3",
            80.0 to "4",
            70.0 to "5"
        )

        val today = java.time.LocalDate.of(2025, 5, 15)
        val formatter = java.time.format.DateTimeFormatter.BASIC_ISO_DATE
        (0..2).map { today.minusDays(it.toLong()) }.forEach { date ->
            val key = "ranking:daily:${date.format(formatter)}"
            println("key: $key")
            val zset = redissonClient.getScoredSortedSet<String>(key, StringCodec.INSTANCE)
            scores.forEach { (score, member) ->
                val added = zset.add(score, member)
                println(" 추가 여부: $member,$score → $added")
            }
        }

        val weeklyKey = "ranking:weekly:2025-W20"
        val weeklyZSet = redissonClient.getScoredSortedSet<String>(weeklyKey,StringCodec.INSTANCE)
        scores.forEach { (score, member) ->
            weeklyZSet.add(score, member)
        }

        val products = listOf(
            Product(id = 0L, name = "콜드브루", price = 3000L,10),
            Product(id = 0L, name = "라떼", price = 3500L, 10),
            Product(id = 0L, name = "아아", price = 200L, 10),
            Product(id = 0L, name = "아이스티", price = 4000L, 10),
            Product(id = 0L, name = "녹차라떼", price = 4500L, 10)
        )

        products.map { productRepository.save(it)}
    }

    @Test
    fun `일간 랭킹 조회 시 Redis 점수를 기반으로 Top 5 결과를 반환한다`() {
        val results = rankingService.getTop5DailyRanking()

        assertThat(results).hasSize(5)
        assertThat(results.map { it.salesCount }).containsExactly(300, 270, 240, 210, 150) // 3일 누적 점수 기준
    }

    @Test
    fun `주간 랭킹 조회 시 Redis 점수를 기반으로 Top 5 결과를 반환한다`() {
        val results = rankingService.getTop5WeeklyRanking()

        assertThat(results).hasSize(5)
        assertThat(results.map { it.name }).containsExactly("콜드브루", "아아", "아이스티", "녹차라떼", "라떼") // 3일 누적 점수 기준
        assertThat(results.map { it.salesCount }).containsExactly(100, 90, 80, 70, 50) // 3일 누적 점수 기준
    }




}