package kr.hhplus.be.server.infrastructure.product

import kr.hhplus.be.server.domain.product.Product
import kr.hhplus.be.server.domain.product.ProductRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProductRepositoryImpl(
    private val jpaProductRepository: JpaProductRepository,
) : ProductRepository {

    //전체 product 전체 조회
    override fun findAll(): List<Product> {
        val products = jpaProductRepository.findAll()
        return ProductConverter.toDomainList(products)
    }

    override fun findByIds(ids: List<Long>): List<Product> {
        val products = jpaProductRepository.findAllById(ids)
        return ProductConverter.toDomainList(products)
    }

    override fun findById(id: Long): Product {
        val product = jpaProductRepository.findById(id).orElseThrow()
        return ProductConverter.toDomain(product)
    }

    @Transactional
    override fun save(product: Product): Product {
        val productEntity = ProductConverter.toProductEntity(product)
        val products = jpaProductRepository.save(productEntity)
        return ProductConverter.toDomain(products)
    }
}