package kr.hhplus.be.server.domain.stat

interface StatRepository {
    fun findAllOrderBySalesDesc() : List<Stat>
}