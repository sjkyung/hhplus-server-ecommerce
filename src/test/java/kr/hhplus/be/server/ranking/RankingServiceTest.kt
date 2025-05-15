package kr.hhplus.be.server.ranking


import kr.hhplus.be.server.application.ranking.RankingService
import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.product.ProductRepository
import kr.hhplus.be.server.domain.stat.StatRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.BDDMockito.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RankingServiceTest {


    private lateinit var productRepository: ProductRepository
    private lateinit var statRepository: StatRepository
    private lateinit var rankingService: RankingService

    @BeforeEach
    fun init() {
        productRepository = mock(ProductRepository::class.java)
        statRepository = mock(StatRepository::class.java)
        rankingService = RankingService(
            statRepository,
            productRepository,
        )
    }

    @Test
    fun `일간 랭킹 조회 시 Redis 점수를 기반으로 Top 5 결과를 반환한다`() {
        val today = LocalDate.now()
        println(today)
        val formatter = DateTimeFormatter.BASIC_ISO_DATE
        val keys = (0..2).map { today.minusDays(it.toLong()).format(formatter)
        }.map { "ranking:daily:$it" }
        println(" Redis keys: $keys")

        val rank = listOf("1" to 100.0, "2" to 50.0)
        // given
        given(statRepository.getTopN("ranking:daily:20250515", 5)).willReturn(
            rank
        )
        given(statRepository.getTopN("ranking:daily:20250514", 5)).willReturn(
            rank
        )
        given(statRepository.getTopN("ranking:daily:20250513", 5)).willReturn(
            rank
        )

        val products = listOf(
            Product(id = 1L, name = "콜드브루", price = 3000L,10),
            Product(id = 2L, name = "라떼", price = 3500L, 10),
            Product(id = 2L, name = "라떼", price = 3500L, 10),
            Product(id = 2L, name = "라떼", price = 3500L, 10),
            Product(id = 2L, name = "라떼", price = 3500L, 10)
        )
        `when`(productRepository.findByIds(anyList())).thenReturn(products)

        // when
        val result = rankingService.getTop5DailyRanking()

        // then
        assertAll( {
            assertThat(result).hasSize(2)
            assertThat(result[0].name).isEqualTo("콜드브루")
            assertThat(result[0].salesCount).isEqualTo(300) // 누적 점수

            assertThat(result[1].name).isEqualTo("라떼")
            assertThat(result[1].salesCount).isEqualTo(150) // 누적 점수
        })
    }


    @Test
    fun `주간 랭킹 조회 시 Redis 점수를 기반으로 Top 5 결과를 반환한다`() {
        val today = LocalDate.now() // 또는 LocalDate.now()
        val week = today.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear())
        val year = today.get(java.time.temporal.WeekFields.ISO.weekBasedYear())
        val weeklyKey = "ranking:weekly:%04d-W%02d".format(year, week)

        println("오늘 날짜: $today")
        println("주간 키: $weeklyKey")

        val rank = listOf("1" to 100.0, "2" to 50.0)
        // given
        given(statRepository.getTopN("ranking:weekly:2025-W20", 5)).willReturn(
            rank
        )

        val products = listOf(
            Product(id = 1L, name = "콜드브루", price = 3000L,10),
            Product(id = 2L, name = "라떼", price = 3500L, 10),
            Product(id = 2L, name = "라떼", price = 3500L, 10),
            Product(id = 2L, name = "라떼", price = 3500L, 10),
            Product(id = 2L, name = "라떼", price = 3500L, 10)
        )
        `when`(productRepository.findByIds(anyList())).thenReturn(products)

        // when
        val result = rankingService.getTop5WeeklyRanking()

        // then
        assertAll( {
            assertThat(result).hasSize(2)
            assertThat(result[0].name).isEqualTo("콜드브루")
            assertThat(result[0].salesCount).isEqualTo(100) // 누적 점수

            assertThat(result[1].name).isEqualTo("라떼")
            assertThat(result[1].salesCount).isEqualTo(50) // 누적 점수
        })
    }
}