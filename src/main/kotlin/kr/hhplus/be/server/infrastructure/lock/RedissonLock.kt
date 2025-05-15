package kr.hhplus.be.server.infrastructure.lock

import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class RedissonLock(
    private val redissonClient: RedissonClient,
    private val lockKey: String,
    private val waitTime: Long,
    private val leaseTime: Long,
    private val timeUnit: TimeUnit,
): DistributedLockExecutor {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun lock(): Boolean {
        log.info("REDISSON LOCK: 락 획득 시도 - key=$lockKey")
        val lock = redissonClient.getLock(lockKey)
        val success = lock.tryLock(waitTime, leaseTime, timeUnit)

        if(success) {
            log.info("REDISSON LOCK: 락 획득 성공 - key=$lockKey")
        }else{
            log.warn("REDISSON LOCK: 락 획득 실패 - key=$lockKey")
        }

        return success
    }


    override fun unlock() {
        val lock = redissonClient.getLock(lockKey)
        if(lock.isHeldByCurrentThread){
            lock.unlock()
            log.info("REDISSON LOCK 락 해제 완료 - key=$lockKey")
        }
    }

}