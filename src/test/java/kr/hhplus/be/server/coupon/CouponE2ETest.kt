package kr.hhplus.be.server.coupon

import kr.hhplus.be.server.ApiResponse
import kr.hhplus.be.server.domain.coupon.CouponStatus
import kr.hhplus.be.server.infrastructure.coupon.CouponEntity
import kr.hhplus.be.server.infrastructure.coupon.JpaCouponRepository
import kr.hhplus.be.server.infrastructure.coupon.JpaUserCouponRepository
import kr.hhplus.be.server.infrastructure.coupon.UserCouponEntity
import kr.hhplus.be.server.interfaces.coupon.CouponFindResponse
import kr.hhplus.be.server.interfaces.coupon.CouponIssueRequest
import kr.hhplus.be.server.interfaces.coupon.CouponIssueResponse
import kr.hhplus.be.server.support.E2ETestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient
import java.time.LocalDateTime

class CouponE2ETest @Autowired constructor(
    private val jpaCouponRepository: JpaCouponRepository,
    private val jpaUserCouponRepository: JpaUserCouponRepository
): E2ETestBase() {

    lateinit var restClient: RestClient

    @BeforeEach
    fun init() {
        restClient = RestClient.builder()
            .baseUrl("http://localhost:$port")
            .build()
    }

    @Test
    fun `쿠폰 조회 API  - 200 OK`(){
        //given
        jpaCouponRepository.save(CouponEntity(0,"선착순 쿠폰",3000,100, LocalDateTime.now().plusDays(1)))
        jpaUserCouponRepository.save(UserCouponEntity(0, 1,1, CouponStatus.AVAILABLE, null, LocalDateTime.now()))


        //when
        val response = restClient.get()
            .uri("/api/v1/coupons/users/1")
            .retrieve()
            .body(object : ParameterizedTypeReference<ApiResponse<List<CouponFindResponse>>>(){})


        // then
        val data = requireNotNull(response?.data) { "응답의 data가 null입니다" }
        assertAll({
            assertThat(response.code).isEqualTo(200)
            assertThat(data.get(0).name).isEqualTo("선착순 쿠폰")
            assertThat(data.get(0).status).isEqualTo("AVAILABLE")
            assertThat(data.get(0).discountAmount).isEqualTo(3000)
        })
    }


    @Test
    fun `쿠폰 발급 요청 API - 200 OK`(){
        //given
        jpaCouponRepository.save(CouponEntity(0,"선착순 쿠폰",3000,100, LocalDateTime.now().plusDays(1)))
        val request = CouponIssueRequest(1)

        //when
        val response = restClient.post()
            .uri("/api/v1/coupons/1/issue")
            .body(request)
            .retrieve()
            .body(object : ParameterizedTypeReference<ApiResponse<CouponIssueResponse>>(){})


        // then
        val data = requireNotNull(response?.data) { "응답의 data가 null입니다" }
        assertAll({
            assertThat(response.code).isEqualTo(200)
            assertThat(data.couponId).isEqualTo(1)
            assertThat(data.status).isEqualTo("AVAILABLE")
            assertThat(data.discountAmount).isEqualTo(3000)
            assertThat(data.expiredAt.isAfter(java.time.LocalDateTime.now())).isTrue()
        })
    }
}