package com.example.somcustomerbooking.model

sealed interface ApiResult<out T>{
    data class Success<T>(val data: T) : ApiResult<T>
    data class ValidationError(val fieldErrors: Map<String, String>) : ApiResult<Nothing>
    data class Conflict(val message: String) : ApiResult<Nothing>
    data class ServerError(val message: String) : ApiResult<Nothing>
}