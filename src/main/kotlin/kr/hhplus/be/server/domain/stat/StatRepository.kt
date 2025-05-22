package kr.hhplus.be.server.domain.stat

import java.time.Duration


interface StatRepository {
    fun findAllOrderBySalesDesc() : List<Stat>
    fun increaseScore(key: String, productId: String, quantity: Long)
    fun getTopN(key: String, n : Int) : List<Pair<String, Double>>
    fun setKeyTtl(key: String, ttl: Duration)
}