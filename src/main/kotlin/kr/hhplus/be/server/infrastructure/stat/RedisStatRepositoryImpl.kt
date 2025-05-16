package kr.hhplus.be.server.infrastructure.stat

import kr.hhplus.be.server.domain.stat.Stat
import kr.hhplus.be.server.domain.stat.StatRepository
import org.redisson.api.RedissonClient
import org.redisson.client.codec.StringCodec
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class RedisStatRepositoryImpl(
    private val redissonClient : RedissonClient
) : StatRepository{
    override fun findAllOrderBySalesDesc(): List<Stat> {
        TODO("Not yet implemented")
    }

    override fun increaseScore(key: String, productId: String, quantity: Long) {
        val scoredSortedSet = redissonClient.getScoredSortedSet<String>(key)
        scoredSortedSet.addScore(productId, quantity.toDouble())
    }

    override fun getTopN(
        key: String,
        n: Int
    ): List<Pair<String, Double>> {
        val set = redissonClient.getScoredSortedSet<String>(key, StringCodec.INSTANCE)
        return set.entryRangeReversed(0, n - 1).map { it.value to it.score }
    }

    override fun setKeyTtl(key: String, ttl: Duration) {
        val set = redissonClient.getScoredSortedSet<String>(key)
        set.expire(ttl)
    }
}