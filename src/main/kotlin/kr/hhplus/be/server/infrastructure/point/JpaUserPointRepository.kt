package kr.hhplus.be.server.infrastructure.point

import org.springframework.data.jpa.repository.JpaRepository

interface JpaUserPointRepository: JpaRepository<UserPointEntity,Long> {
}