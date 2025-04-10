package kr.hhplus.be.server.infrastructure.point

import kr.hhplus.be.server.domain.point.UserPoint
import kr.hhplus.be.server.domain.point.UserPointRepository
import org.springframework.stereotype.Repository

@Repository
class UserPointRepositoryImpl(): UserPointRepository {

    override fun findByUserId(userId: Long): UserPoint {
        TODO("Not yet implemented")
    }

    override fun save(point: UserPoint): UserPoint {
        TODO("Not yet implemented")
    }

}