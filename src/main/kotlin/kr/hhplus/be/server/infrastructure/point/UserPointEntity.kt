package kr.hhplus.be.server.infrastructure.point

import jakarta.persistence.*

@Entity
@Table(name = "user_point")
class UserPointEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val userId: Long,
    val point: Long
) {
}