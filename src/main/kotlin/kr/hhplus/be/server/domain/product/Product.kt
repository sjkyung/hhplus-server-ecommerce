package kr.hhplus.be.server.domain.product

import kr.hhplus.be.server.domain.base.Timestamped

data class Product(
    val id: Long,
    val name: String,
    val price: Long,
    var quantity: Int
): Timestamped() {

    fun validateStock(requestedQuantity: Int) {
        require(requestedQuantity > 0){
            "요청 수량은 0보다 커야 합니다"
        }
        check(this.quantity >= requestedQuantity) {
            "재고가 부족합니다. (요청: $requestedQuantity, 남은: $quantity)"
        }
    }

    fun decrease(decreaseQuantity: Int) {
        check(quantity >= decreaseQuantity) {
            "재고가 부족합니다. (요청: $decreaseQuantity, 남은: $quantity)"
        }
        quantity -= decreaseQuantity
    }
}