package kr.hhplus.be.server.payment

import kr.hhplus.be.server.ApiResponse
import kr.hhplus.be.server.domain.order.OrderStatus
import kr.hhplus.be.server.infrastructure.order.JpaOrderRepository
import kr.hhplus.be.server.infrastructure.order.OrderEntity
import kr.hhplus.be.server.infrastructure.point.JpaUserPointRepository
import kr.hhplus.be.server.infrastructure.point.UserPointEntity
import kr.hhplus.be.server.interfaces.payment.PaymentRequest
import kr.hhplus.be.server.interfaces.payment.PaymentResponse
import kr.hhplus.be.server.support.IntegrationTestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient

class PaymentE2ETest @Autowired constructor(
    private val jpaOrderRepository: JpaOrderRepository,
    private val jpaUserPointRepository: JpaUserPointRepository
): IntegrationTestBase() {

    lateinit var restClient: RestClient

    @BeforeEach
    fun init() {
        restClient = RestClient.builder()
            .baseUrl("http://localhost:8080")
            .build()
    }

    @Test
    fun `결제 요청 API - 200 OK`(){
        //given
        jpaOrderRepository.save(OrderEntity(id = 0, userId = 1, userCouponId = null, totalPrice = 3000, status = OrderStatus.PENDING))
        jpaUserPointRepository.save(UserPointEntity(0,1,3000,0))

        val request = PaymentRequest(orderId = 1)


        //when
        val response = restClient.post()
            .uri("/api/v1/payments")
            .body(request)
            .retrieve()
            .body(object : ParameterizedTypeReference<ApiResponse<PaymentResponse>>() {})

        println(response)
        //then
        val data = requireNotNull(response?.data) { "응답의 data가 null입니다"}
        assertAll(
            {assertThat(data.status).isEqualTo("SUCCESS")},
            {assertThat(data.orderId).isEqualTo(1)},
            {assertThat(data.amount).isEqualTo(3000)}
        )
    }

}