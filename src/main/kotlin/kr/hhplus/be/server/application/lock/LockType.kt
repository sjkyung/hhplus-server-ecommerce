package kr.hhplus.be.server.application.lock

enum class LockType {
    REDIS_SPIN,
    REDIS_PUB_SUB
}