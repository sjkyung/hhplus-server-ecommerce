package kr.hhplus.be.server.domain.product

interface ProductRepository {
    fun findAll() : List<Product>
    fun findByIds(ids: List<Long>) : List<Product>
    fun findById(id: Long): Product
    fun save(product: Product): Product
}