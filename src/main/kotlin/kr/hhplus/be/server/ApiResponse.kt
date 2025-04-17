package kr.hhplus.be.server

import org.springframework.http.ResponseEntity

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null,
){
    companion object{
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(
            200,"성공",data
        )

        fun <T> fail(code: Int, message: String): ApiResponse<T> = ApiResponse(
            code,message,null
        )
    }
}
