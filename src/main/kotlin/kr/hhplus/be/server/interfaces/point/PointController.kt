package kr.hhplus.be.server.interfaces.point


import kr.hhplus.be.server.ApiResponse
import kr.hhplus.be.server.application.point.PointService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class PointController(
    private val pointService: PointService
): PointApiSpec {

    @GetMapping("/users/{userId}/point")
    override fun getPoint(
        @PathVariable("userId") userId: Long,
    ): ApiResponse<PointFindResponse>{
        val point = pointService.getPoint(userId)
        val response = PointFindResponse.from(point)
        return ApiResponse.success(response)
    }

    @PostMapping("/users/{userId}/point/charge")
    override fun chargePoint(
        @PathVariable("userId") userId: Long,
        @RequestBody chargePointRequest: ChargePointRequest
    ): ApiResponse<ChargePointResponse>{
        val pointChargeCommand = chargePointRequest.toCommand(userId)
        val point = pointService.charge(pointChargeCommand)
        val response = ChargePointResponse.from(point,chargePointRequest.amount)
        return ApiResponse.success(response)
    }
}