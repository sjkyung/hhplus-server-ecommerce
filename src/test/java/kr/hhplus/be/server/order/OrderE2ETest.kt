package kr.hhplus.be.server.order


import kr.hhplus.be.server.ApiResponse
import kr.hhplus.be.server.infrastructure.product.JpaProductRepository
import kr.hhplus.be.server.infrastructure.product.ProductEntity
import kr.hhplus.be.server.interfaces.order.OrderItemRequest
import kr.hhplus.be.server.interfaces.order.OrderRequest
import kr.hhplus.be.server.interfaces.order.OrderResponse
import kr.hhplus.be.server.support.E2ETestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient

class OrderE2ETest @Autowired constructor(
    private val jpaProductRepository: JpaProductRepository,
) : E2ETestBase() {

    lateinit var restClient: RestClient

    @BeforeEach
    fun init() {
        restClient = RestClient.builder()
            .baseUrl("http://localhost:$port")
            .build()
    }

    @Test
    fun `주문 요청 API - 200 OK`() {
        //given
        jpaProductRepository.save(ProductEntity(0,"테스트 상품",3000,10))
        val request = OrderRequest(
            listOf(
                OrderItemRequest(1, 5)
            ), null
        )


        //when
        val response = restClient.post()
            .uri("/api/v1/users/1/orders")
            .body(request)
            .retrieve()
            .body(object : ParameterizedTypeReference<ApiResponse<OrderResponse>>() {})


        //then
        val data = requireNotNull(response?.data) { "응답의 data가 null입니다" }

        assertAll(
            { assertThat(data.orderId).isEqualTo(1) },
            { assertThat(data.totalPrice).isEqualTo(15000) }
        )
    }


}