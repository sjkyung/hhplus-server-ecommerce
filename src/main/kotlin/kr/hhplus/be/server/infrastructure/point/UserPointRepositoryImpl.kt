package kr.hhplus.be.server.infrastructure.point

import kr.hhplus.be.server.domain.point.UserPoint
import kr.hhplus.be.server.domain.point.UserPointRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class UserPointRepositoryImpl(
    private val jpaUserPointRepository: JpaUserPointRepository
): UserPointRepository {

    override fun findByUserId(userId: Long): UserPoint {
        return jpaUserPointRepository.findById(userId).orElseThrow().let {
            UserPointConverter.toDomain(it)
        }
    }

    @Transactional
    override fun save(userPoint: UserPoint): UserPoint {
        val userPointEntity = jpaUserPointRepository.save(
            UserPointConverter.toEntity(userPoint)
        )
        return UserPointConverter.toDomain(userPointEntity)
    }

}