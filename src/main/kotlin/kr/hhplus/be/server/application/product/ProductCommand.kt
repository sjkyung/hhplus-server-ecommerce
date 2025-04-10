package com.example.demo.application.product

import kr.hhplus.be.server.domain.product.Product

data class ProductCommand(
    val products: List<Product>
)
