package kr.hhplus.be.server.infrastructure.product

import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.product.ProductRepository
import kr.hhplus.be.server.domain.stats.ProductStat
import kr.hhplus.be.server.domain.stats.ProductStatsRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ProductRepositoryImpl(): ProductRepository,ProductStatsRepository {

    override fun findAll() : List<Product> {
        TODO("Not yet implemented")
    }

    override fun findByIds(ids: List<Long>): List<Product> {
        TODO("Not yet implemented")
    }

    override fun findById(id: Long): Product {
        TODO("Not yet implemented")
    }

    override fun save(product: Product): Product {
        TODO("Not yet implemented")
    }

    override fun findAllOrderBySalesDesc(): List<ProductStat> {
        TODO("Not yet implemented")
    }

}