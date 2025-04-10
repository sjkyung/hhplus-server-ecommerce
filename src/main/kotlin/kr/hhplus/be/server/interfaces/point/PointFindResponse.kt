package kr.hhplus.be.server.interfaces.point

import kr.hhplus.be.server.domain.point.UserPoint

data class PointFindResponse(
    val userId: Long,
    val point: Long
){
    companion object{
        fun from(point: UserPoint): PointFindResponse {
            return PointFindResponse(
                userId = point.id,
                point = point.point,
            )
        }

    }
}
