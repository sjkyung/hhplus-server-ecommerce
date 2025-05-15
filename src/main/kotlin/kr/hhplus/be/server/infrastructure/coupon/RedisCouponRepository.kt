package kr.hhplus.be.server.infrastructure.coupon

import org.redisson.api.RedissonClient
import org.redisson.client.codec.StringCodec
import org.springframework.stereotype.Repository

@Repository
class RedisCouponRepository(
    private val redissonClient : RedissonClient
) {

    fun checkDuplicate(userId: Long): Boolean {
        val appliedSet = redissonClient.getSet<String>("coupon:applied", StringCodec.INSTANCE)
        return appliedSet.add(userId.toString())
    }

    fun decreaseStock(userId: Long): Boolean {
        val stockList = redissonClient.getList<String>("coupon:stock", StringCodec.INSTANCE)
        return try {
            stockList.removeAt(0)
            true
        } catch (e: IndexOutOfBoundsException) {
            redissonClient.getSet<String>("coupon:applied", StringCodec.INSTANCE)
                .remove(userId.toString())
            false
        }
    }

    fun saveToPending(userId: Long, couponId: Long): Boolean {
        val pendingList = redissonClient.getList<String>("coupon:issued:pending", StringCodec.INSTANCE)
        return pendingList.add("$userId:$couponId")
    }

    fun popPendingCoupon(): String? {
        val list = redissonClient.getList<String>("coupon:issued:pending", StringCodec.INSTANCE)
        return try {
            list.removeAt(0) // LPOP
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }
}