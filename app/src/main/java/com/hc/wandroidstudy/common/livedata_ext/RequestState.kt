package com.hc.wandroidstudy.common.livedata_ext


sealed class RequestState<out T> {
    object Loading : RequestState<Nothing>()
    data class Success<out T>(val data: T?) : RequestState<T>()
    data class Error(val error: Throwable? = null) : RequestState<Nothing>()
}