package kr.hhplus.be.server.application.dataPlatform

import org.springframework.stereotype.Service

@Service
class DataPlatformService {

    fun send(command: DataPlatformCommand){
        println("[DataPlatform] 전송됨  - orderId=${command.orderId}, userId=${command.userId}, totalPrice=${command.totalPrice}")
        println("[DataPlatform] 전송시간  - ${command.sendTime}")
    }

}