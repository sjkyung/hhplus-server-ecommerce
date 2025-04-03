package kr.hhplus.be.server.controller.point


import kr.hhplus.be.server.ApiResponse
import kr.hhplus.be.server.controller.point.request.ChargePointRequest
import kr.hhplus.be.server.controller.point.response.ChargePointResponse
import kr.hhplus.be.server.controller.point.response.PointFindResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1")
class PointController: PointApiSpec{

    @GetMapping("/users/{userId}/point")
    override fun getPoint(
        @PathVariable("userId") userId: Long,
    ): ApiResponse<PointFindResponse>{
        val response = PointFindResponse(1L,5000)
        return ApiResponse.success(response)
    }

    @PostMapping("/users/{userId}/point/charge")
    override fun charge(
        @PathVariable("userId") userId: Long,
        @RequestBody charge: ChargePointRequest
    ): ApiResponse<ChargePointResponse>{
        val response = ChargePointResponse(
            userId = 1L,
            amount = 3000,
            point = 8000,
            chargedAt = LocalDateTime.now()
        )
        return ApiResponse.success(response)
    }
}