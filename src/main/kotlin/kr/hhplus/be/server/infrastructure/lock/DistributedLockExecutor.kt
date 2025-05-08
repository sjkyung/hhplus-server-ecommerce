package kr.hhplus.be.server.infrastructure.lock

interface DistributedLockExecutor {
    fun lock(): Boolean
    fun unlock()
}