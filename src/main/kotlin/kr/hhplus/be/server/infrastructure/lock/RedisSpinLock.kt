package kr.hhplus.be.server.infrastructure.lock

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import java.util.UUID
import java.util.concurrent.TimeUnit


class RedisSpinLock(
    private val redisTemplate: StringRedisTemplate,
    private val lockKey: String,
    private val waitTime: Long,
    private val leaseTime: Long,
    private val timeUnit: TimeUnit,
): DistributedLockExecutor {
    private val log = LoggerFactory.getLogger(javaClass)
    private val lockValue = UUID.randomUUID().toString()

    override fun lock(): Boolean {
        val deadline = System.currentTimeMillis() + timeUnit.toMillis(waitTime)

        while (System.currentTimeMillis() < deadline) {
            log.info("SPIN LOCK: 락 획득 시도 - key=$lockKey (value=$lockValue)")

            val success = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, leaseTime, timeUnit)

            if (success == true) {
                log.info("SPIN LOCK: 락 획득 성공 - key=$lockKey (value=$lockValue)")
                return true
            }

            log.info("SPIN LOCK:  락 획득 대기중 - key=$lockKey (value=$lockValue)")
            Thread.sleep(100)
        }
        log.warn("SPIN LOCK: 락 획득 실패, 대기 시간 초과 - key=$lockKey (value=$lockValue)")
        return false
    }


    override fun unlock() {
        if(redisTemplate.opsForValue().get(lockKey) == lockValue) {
            redisTemplate.delete(lockKey)
        }
    }
}