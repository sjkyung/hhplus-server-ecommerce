package kr.hhplus.be.server.application.point

data class PointChargeCommand(
    val userId: Long,
    val amount: Long
){
}