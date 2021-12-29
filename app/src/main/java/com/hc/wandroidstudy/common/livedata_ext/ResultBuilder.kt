package com.hc.wandroidstudy.common.livedata_ext


class ResultBuilder<T>() {
    var onLoading: () -> Unit = {

    }
    var onSuccess: (data: T?) -> Unit = {

    }
    var onError: (Throwable?) -> Unit = {

    }

    fun onLoading(block: () -> Unit = {}) {
        onLoading = block
    }

    fun onSuccess(block:  (data: T?) -> Unit = {}) {
        onSuccess = block
    }

    fun onError(block: (Throwable?) -> Unit = {}) {
        onError = block
    }
}