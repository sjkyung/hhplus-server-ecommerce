package kr.hhplus.be.server.controller.product.response

data class ProductFindResponse(
    val id: Long,
    val name: String,
    val price: Long,
    val stock: Long
)
