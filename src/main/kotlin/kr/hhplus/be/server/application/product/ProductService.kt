package kr.hhplus.be.server.application.product

import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.product.ProductRepository
import kr.hhplus.be.server.domain.stats.ProductStatsRepository
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val productStatsRepository: ProductStatsRepository,
) {

    fun findAllProducts(
    ): List<Product> {
        return productRepository.findAll();
    }

    fun findRankingByProducts(
    ): List<ProductResult> {
        val stats = productStatsRepository.findAllOrderBySalesDesc();
        val ids = stats.map { it.productId }.distinct()
        val products = productRepository.findByIds(ids)

        val productMap = products.associateBy { it.id }

        return ProductResult.from(productMap,stats)
    }
}
