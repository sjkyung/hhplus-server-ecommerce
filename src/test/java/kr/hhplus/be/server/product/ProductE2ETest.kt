package kr.hhplus.be.server.product


import kr.hhplus.be.server.ApiResponse
import kr.hhplus.be.server.domain.order.OrderStatus
import kr.hhplus.be.server.infrastructure.order.JpaOrderItemRepository
import kr.hhplus.be.server.infrastructure.order.JpaOrderRepository
import kr.hhplus.be.server.infrastructure.order.OrderEntity
import kr.hhplus.be.server.infrastructure.order.OrderItemEntity
import kr.hhplus.be.server.infrastructure.product.JpaProductRepository
import kr.hhplus.be.server.infrastructure.product.ProductEntity
import kr.hhplus.be.server.interfaces.product.ProductFindResponse
import kr.hhplus.be.server.interfaces.product.ProductRankingResponse
import kr.hhplus.be.server.support.E2ETestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient
import java.time.LocalDateTime

class ProductE2ETest @Autowired constructor(
    private val jpaProductRepository: JpaProductRepository,
    private val jpaOrderRepository: JpaOrderRepository,
    private val jpaOrderItemRepository: JpaOrderItemRepository,
) : E2ETestBase(){

    lateinit var restClient: RestClient

    @BeforeEach
    fun init() {
        restClient = RestClient.builder()
            .baseUrl("http://localhost:$port")
            .build()
    }

    @Test
    fun `상품 목록 조회 요청 API - 200 OK`() {
        // given
        jpaProductRepository.save(ProductEntity(0,"상품1",1000,10))
        jpaProductRepository.save(ProductEntity(0,"상품2",2000,5))

        // when
        val response = restClient.get()
            .uri("/api/v1/products")
            .retrieve()
            .body(object : ParameterizedTypeReference<ApiResponse<List<ProductFindResponse>>>() {})

        // then
        val data = requireNotNull(response?.data) { "응답의 data가 null입니다" }
        assertAll({
            assertThat(response.code).isEqualTo(200)
            assertThat(data).hasSize(2)
            assertThat(data[0].name).isEqualTo("상품1")
            assertThat(data[1].name).isEqualTo("상품2")
        })
    }

    @Test
    fun `상품 랭킹 조회 요청 API - 200 OK`() {
        // given
        jpaProductRepository.save(ProductEntity(0,"상품1",1000,15))
        jpaProductRepository.save(ProductEntity(0,"상품2",2000,5))
        jpaOrderRepository.save(OrderEntity(id = 0, userId = 1, userCouponId = null, totalPrice = 3000, status = OrderStatus.COMPLETED))
        jpaOrderRepository.save(OrderEntity(id = 0, userId = 1, userCouponId = null, totalPrice = 8000, status = OrderStatus.COMPLETED))
        jpaOrderRepository.save(OrderEntity(id = 0, userId = 1, userCouponId = null, totalPrice = 1000, status = OrderStatus.PENDING))
        jpaOrderRepository.save(OrderEntity(id = 0, userId = 1, userCouponId = null, totalPrice = 1000, status = OrderStatus.COMPLETED))

        jpaOrderItemRepository.saveAll(
            listOf(
                OrderItemEntity(id = 0, orderId = 1, productId = 1, price = 1000, quantity = 1, createdAt = LocalDateTime.now()),
                OrderItemEntity(id = 0, orderId = 1, productId = 2, price = 2000, quantity = 1, createdAt = LocalDateTime.now()),
                OrderItemEntity(id = 0, orderId = 2, productId = 1, price = 1000, quantity = 8, createdAt = LocalDateTime.now()),
                OrderItemEntity(id = 0, orderId = 3, productId = 1, price = 1000, quantity = 1, createdAt = LocalDateTime.now()),
                OrderItemEntity(id = 0, orderId = 4, productId = 1, price = 1000, quantity = 1, createdAt = LocalDateTime.now().minusDays(4))
            )
        )

        // when
        val response = restClient.get()
            .uri("/api/v1/products/rank")
            .retrieve()
            .body(object : ParameterizedTypeReference<ApiResponse<List<ProductRankingResponse>>>() {})

        // then
        val data = requireNotNull(response?.data) { "응답의 data가 null입니다" }
        assertAll ({
            assertThat(response.code).isEqualTo(200)
            assertThat(data[0].rank).isEqualTo(1)
            assertThat(data[0].name).isEqualTo("상품1")
        })
    }


    @Test
    fun `상품 랭킹 조회 요청 API cache 적용 - 200 OK`() {
        // given
        jpaProductRepository.save(ProductEntity(0, "상품1", 1000, 15))
        jpaProductRepository.save(ProductEntity(0, "상품2", 2000, 5))
        jpaOrderRepository.save(
            OrderEntity(
                id = 0,
                userId = 1,
                userCouponId = null,
                totalPrice = 3000,
                status = OrderStatus.COMPLETED
            )
        )
        jpaOrderRepository.save(
            OrderEntity(
                id = 0,
                userId = 1,
                userCouponId = null,
                totalPrice = 8000,
                status = OrderStatus.COMPLETED
            )
        )
        jpaOrderRepository.save(
            OrderEntity(
                id = 0,
                userId = 1,
                userCouponId = null,
                totalPrice = 1000,
                status = OrderStatus.PENDING
            )
        )
        jpaOrderRepository.save(
            OrderEntity(
                id = 0,
                userId = 1,
                userCouponId = null,
                totalPrice = 1000,
                status = OrderStatus.COMPLETED
            )
        )

        jpaOrderItemRepository.saveAll(
            listOf(
                OrderItemEntity(
                    id = 0,
                    orderId = 1,
                    productId = 1,
                    price = 1000,
                    quantity = 1,
                    createdAt = LocalDateTime.now()
                ),
                OrderItemEntity(
                    id = 0,
                    orderId = 1,
                    productId = 2,
                    price = 2000,
                    quantity = 1,
                    createdAt = LocalDateTime.now()
                ),
                OrderItemEntity(
                    id = 0,
                    orderId = 2,
                    productId = 1,
                    price = 1000,
                    quantity = 8,
                    createdAt = LocalDateTime.now()
                ),
                OrderItemEntity(
                    id = 0,
                    orderId = 3,
                    productId = 1,
                    price = 1000,
                    quantity = 1,
                    createdAt = LocalDateTime.now()
                ),
                OrderItemEntity(
                    id = 0,
                    orderId = 4,
                    productId = 1,
                    price = 1000,
                    quantity = 1,
                    createdAt = LocalDateTime.now().minusDays(4)
                )
            )
        )

        //  1. 최초 요청 → 캐시 미적중
        val startMiss = System.currentTimeMillis()
        restClient.get()
            .uri("/api/v1/products/rank/cache")
            .retrieve()
            .body(object : ParameterizedTypeReference<ApiResponse<List<ProductRankingResponse>>>() {})
        val endMiss = System.currentTimeMillis()
        println("첫 요청 (캐시 미적중): ${endMiss - startMiss}ms")

        //  2. 두 번째 요청 → 캐시 적중
        val startHit = System.currentTimeMillis()
        restClient.get()
            .uri("/api/v1/products/rank/cache")
            .retrieve()
            .body(object : ParameterizedTypeReference<ApiResponse<List<ProductRankingResponse>>>() {})
        val endHit = System.currentTimeMillis()
        println("두 번째 요청 (캐시 적중): ${endHit - startHit}ms")

        // then
        assertThat((endMiss - startMiss)).isGreaterThan((endHit - startHit))
    }
}