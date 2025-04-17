package kr.hhplus.be.server.infrastructure.product

import kr.hhplus.be.server.domain.product.Product

object ProductConverter {

    fun toProductEntity(product: Product): ProductEntity {
        return ProductEntity(
            product.id,
            name = product.name,
            price = product.price,
            quantity = product.quantity,
        )
    }


    fun toDomain(productEntity: ProductEntity): Product {
        return Product(
            id = productEntity.id,
            name = productEntity.name,
            price = productEntity.price,
            quantity = productEntity.quantity
        )
    }

    fun toEntityList(products: List<Product>): List<ProductEntity> {
        return products.map {
            ProductEntity(
                id = it.id,
                name = it.name,
                price = it.price,
                quantity = it.quantity
            )
        }
    }

    fun toDomainList(products: List<ProductEntity>): List<Product> {
        return products.mapNotNull { product ->
                toDomain(product)
            }
    }

}
