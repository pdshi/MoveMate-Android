package com.stevennt.movemate.data

sealed class Resource<out T: Any>(val data: T? = null, val message: String? = null) {
    class Success<T : Any>(data: T, message: String? = null) : Resource<T>(data, message)
    object Loading : Resource<Nothing>()
    class Error<T : Any>(message: String, data: T? = null) : Resource<T>(data, message)
}