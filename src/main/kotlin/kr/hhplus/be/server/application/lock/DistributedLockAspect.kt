package kr.hhplus.be.server.application.lock

import kr.hhplus.be.server.infrastructure.lock.DistributedLockExecutor
import kr.hhplus.be.server.infrastructure.lock.RedisSpinLock
import kr.hhplus.be.server.infrastructure.lock.RedissonLock
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class DistributedLockAspect(
    private val redisTemplate: StringRedisTemplate,
    private val redissonClient: RedissonClient
){
    private val log = LoggerFactory.getLogger(javaClass)

    @Around("@annotation(kr.hhplus.be.server.application.lock.DistributedLock)")
    fun around(joinPoint: ProceedingJoinPoint): Any? {
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        val lock = method.getAnnotation(DistributedLock::class.java)

        val lockKey = resolveKey(
            lock.key,
            method,
            joinPoint.args,
            methodSignature.parameterNames
        )

        val distributedLock: DistributedLockExecutor = when (lock.lockType) {
            LockType.REDIS_SPIN -> RedisSpinLock(
                redisTemplate,
                lockKey,
                lock.waitTime,
                lock.leaseTime,
                lock.timeUnit
            )
            LockType.REDIS_PUB_SUB -> RedissonLock(
                redissonClient,
                lockKey,
                lock.waitTime,
                lock.leaseTime,
                lock.timeUnit
            )
        }
        log.info("[AOP] Lock 시도 - key=$lockKey, type=${lock.lockType}")
        if (!distributedLock.lock()) {
            log.warn("[AOP] Lock 실패 - key=$lockKey")
            throw IllegalStateException("분산락 획득 실패: key=$lockKey")
        }

        try {
            log.info("[AOP] Lock 성공 - key=$lockKey")
            return joinPoint.proceed()
        } finally {
            distributedLock.unlock()
            log.info("[AOP] Lock 해제 - key=$lockKey")
        }
    }

    private fun resolveKey(keyExpression: String, method: Method, args: Array<Any?>, parameterNames: Array<String>): String {
        val parser: ExpressionParser = SpelExpressionParser()
        val context = StandardEvaluationContext()

        parameterNames.forEachIndexed { index, name ->
            context.setVariable(name, args[index])
        }

        val evaluatedKey = parser.parseExpression(keyExpression).getValue(context, String::class.java)
        return "lock:$evaluatedKey"
    }
}
