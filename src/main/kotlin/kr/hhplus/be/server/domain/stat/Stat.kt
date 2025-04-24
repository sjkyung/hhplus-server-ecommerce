package kr.hhplus.be.server.domain.stat

data class Stat(
    val ranking: Int,
    val productId: Long,
    val salesCount: Long
) {
}