package kr.hhplus.be.server.domain.point


interface UserPointRepository {
    fun findByUserId(userId: Long): UserPoint
    fun save(point: UserPoint): UserPoint
}