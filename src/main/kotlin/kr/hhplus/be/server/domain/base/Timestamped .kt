package kr.hhplus.be.server.domain.base

import java.time.LocalDateTime

abstract class Timestamped(
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
}