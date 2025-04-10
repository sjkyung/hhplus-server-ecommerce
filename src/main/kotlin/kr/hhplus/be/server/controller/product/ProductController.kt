package kr.hhplus.be.server.controller.product

import kr.hhplus.be.server.ApiResponse
import kr.hhplus.be.server.controller.product.response.ProductFindResponse
import kr.hhplus.be.server.controller.product.response.ProductRankingResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1")
class ProductController: ProductApiSpec {

    @GetMapping("/products")
    override fun getProducts(): ApiResponse<List<ProductFindResponse>> {
        val response = listOf(
            ProductFindResponse(1,"나이키 신발",10000, 100),
            ProductFindResponse(2,"아디다스 신발",20000, 200),
            ProductFindResponse(3,"뉴발란스 신발",40000,50)
        )
        return ApiResponse.success(response)
    }

    @GetMapping("/products/rank")
    override fun getProductRanking(): ApiResponse<List<ProductRankingResponse>> {
        val response = listOf(
            ProductRankingResponse(1,"뉴발란스 신발",40000, 250,LocalDateTime.now()),
            ProductRankingResponse(2,"나이키신발",10000, 200, LocalDateTime.now()),
            ProductRankingResponse(3,"아디다스 신발",5000,150, LocalDateTime.now()),
            ProductRankingResponse(4,"푸마 신발",4000,70, LocalDateTime.now()),
            ProductRankingResponse(5,"리복 신발",2000,30, LocalDateTime.now())
        )
        return ApiResponse.success(response)
    }
}