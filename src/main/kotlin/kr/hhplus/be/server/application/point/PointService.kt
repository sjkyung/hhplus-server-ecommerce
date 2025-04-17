package kr.hhplus.be.server.application.point

import kr.hhplus.be.server.domain.point.UserPoint
import kr.hhplus.be.server.domain.point.UserPointRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PointService(
    private val pointRepository: UserPointRepository
) {

    fun getPoint(userId: Long): UserPoint {
        return pointRepository.findByUserId(userId)
    }


    @Transactional
    fun charge(pointChargeCommand: PointChargeCommand): UserPoint{
        val point = pointRepository.findByUserId(pointChargeCommand.userId)
        val chargedPoint = point.charge(pointChargeCommand.amount)
        return pointRepository.save(chargedPoint)
    }

}