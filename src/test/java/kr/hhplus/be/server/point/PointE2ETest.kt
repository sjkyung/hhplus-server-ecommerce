package kr.hhplus.be.server.point



import kr.hhplus.be.server.ApiResponse
import kr.hhplus.be.server.infrastructure.point.JpaUserPointRepository
import kr.hhplus.be.server.infrastructure.point.UserPointEntity
import kr.hhplus.be.server.interfaces.point.ChargePointRequest
import kr.hhplus.be.server.interfaces.point.ChargePointResponse
import kr.hhplus.be.server.interfaces.point.PointFindResponse
import kr.hhplus.be.server.support.E2ETestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient


class PointE2ETest @Autowired constructor(
    private val jpaUserPointRepository: JpaUserPointRepository,
): E2ETestBase() {

    lateinit var restClient: RestClient

    @BeforeEach
    fun init() {
        restClient = RestClient.builder()
            .baseUrl("http://localhost:$port")
            .build()
    }

    @Test
    fun `포인트 조회 요청 API  - 200 OK`(){
        //given
        jpaUserPointRepository.save(UserPointEntity(0,1,3000,0))

        //when
        val response = restClient.get()
            .uri("/api/v1/users/1/point")
            .retrieve()
            .body(object : ParameterizedTypeReference<ApiResponse<PointFindResponse>>(){})

        // then
        assertAll({
            assertThat(response?.code).isEqualTo(200)
            val data = response!!.data!!
            assertThat(data.userId).isEqualTo(1)
            assertThat(data.point).isEqualTo(3000)
        })
    }


    @Test
    fun `포인트 충전 요청 API - 200 OK`(){
        //given
        jpaUserPointRepository.save(UserPointEntity(0,1,3000,0))
        val request = ChargePointRequest(5000)


        //when
        val response = restClient.post()
            .uri("/api/v1/users/1/point/charge")
            .body(request)
            .retrieve()
            .body(object : ParameterizedTypeReference<ApiResponse<ChargePointResponse>>(){})


        // then
        assertAll({
            assertThat(response?.code).isEqualTo(200)
            val data = requireNotNull(response?.data) { "응답의 data가 null입니다" }
            assertThat(data.userId).isEqualTo(1)
            assertThat(data.point).isEqualTo(8000)
        })
    }


}