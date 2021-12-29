package com.hc.wandroidstudy.common.livedata_ext

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData



typealias StateLiveData<T> = LiveData<RequestState<T>>

typealias StateMutableLiveData<T> = MutableLiveData<RequestState<T>>

@MainThread
inline fun <T> StateLiveData<T>.observeState(
        owner: LifecycleOwner,
        init: ResultBuilder<T>.() -> Unit
) {
    val result = ResultBuilder<T>().apply(init)

    observe(owner) { state ->
        when (state) {
            is RequestState.Loading -> {
                result.onLoading.invoke()
            }
            is RequestState.Success -> {
                result.onSuccess(state.data)
            }
            is RequestState.Error -> {
                result.onError(state.error)
            }
        }
    }
}