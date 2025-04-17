package kr.hhplus.be.server.infrastructure.product

import jakarta.persistence.*
import kr.hhplus.be.server.domain.product.Product

@Entity
@Table(name = "products")
class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var name: String,
    var price: Long,
    val quantity: Int,
) {
    constructor(product: Product) : this(name = "", price = 0, quantity = 0)
}