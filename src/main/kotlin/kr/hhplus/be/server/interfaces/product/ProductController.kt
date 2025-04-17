package kr.hhplus.be.server.interfaces.product

import kr.hhplus.be.server.application.product.ProductService
import kr.hhplus.be.server.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class ProductController(
    private val productService: ProductService
) : ProductApiSpec {

    @GetMapping("/products")
    override fun getProducts(): ApiResponse<List<ProductFindResponse>> {
        val products = productService.findAllProducts()
        val response = products.map { ProductFindResponse.from(it) }
        return ApiResponse.success(response)
    }

    @GetMapping("/products/rank")
    override fun getProductRanking(): ApiResponse<List<ProductRankingResponse>> {
        val rankingProducts = productService.findRankingByProducts()
        val response = rankingProducts.map { ProductRankingResponse.from(it) }
        return ApiResponse.success(response)
    }
}