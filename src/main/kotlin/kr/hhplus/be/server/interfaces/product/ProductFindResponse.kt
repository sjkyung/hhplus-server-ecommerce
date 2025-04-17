package kr.hhplus.be.server.interfaces.product

import kr.hhplus.be.server.domain.product.Product

data class ProductFindResponse(
    val id: Long,
    val name: String,
    val price: Long,
    val stock: Long
){
    companion object {
        fun from(product: Product): ProductFindResponse {
            return ProductFindResponse(
                id = product.id,
                name = product.name,
                price = product.price,
                stock = product.quantity.toLong()
            )
        }
    }
}
