package kr.hhplus.be.server.application.product

import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.product.ProductRepository
import kr.hhplus.be.server.domain.stats.ProductStat
import kr.hhplus.be.server.domain.stats.ProductStatsRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*

class ProductServiceTest {

    private lateinit var productRepository: ProductRepository
    private lateinit var productStatsRepository: ProductStatsRepository
    private lateinit var productService: ProductService

    @BeforeEach
    fun init() {
        productRepository = mock(productRepository::class.java)
        productStatsRepository = mock(productStatsRepository::class.java)
        productService = ProductService(
            productRepository,
            productStatsRepository
        )
    }

    @Test
    fun `전체 상품 조회 테스트`() {
        // given
        val products = listOf(
            Product(1, "나이키 신발", 1000, 200),
            Product(2, "아디다스 신발", 2000, 250)
        )
        `when`(productRepository.findAll()).thenReturn(products)

        // when
        val result = productService.findAllProducts()

        // then
        assertThat(result).isEqualTo(products)
        verify(productRepository).findAll()
    }

    @Test
    fun `상품 랭킹 결과 반환 테스트`() {
        // given
        val stats = listOf(
            ProductStat(1, 1, 100),
            ProductStat(2, 2, 80)
        )
        `when`(productStatsRepository.findAllOrderBySalesDesc()).thenReturn(stats)

        val ids = stats.map { it.productId }.distinct()
        val product1 = Product(1, "나이키신발", 1000, 200)
        val product2 = Product(2, "아디다스신발", 2000, 50)
        `when`(productRepository.findByIds(ids)).thenReturn(listOf(product1, product2))

        // when
        val result = productService.findRankingByProducts()

        // then
        assertThat(result).hasSize(2)

        val result1 = result.find { it.product.id == 1L }
        assertThat(result1).isNotNull
        assertThat(result1!!.productStat.salesCount).isEqualTo(100)

        val result2 = result.find { it.product.id == 2L }
        assertThat(result2).isNotNull
        assertThat(result2!!.productStat.salesCount).isEqualTo(80L)

        verify(productStatsRepository).findAllOrderBySalesDesc()
        verify(productRepository).findByIds(ids)
    }
}