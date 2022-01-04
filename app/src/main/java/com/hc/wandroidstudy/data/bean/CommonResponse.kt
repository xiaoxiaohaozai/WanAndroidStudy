package com.hc.wandroidstudy.common.data.bean


data class CommonResponse<T>(val data: T, val errorCode: Int, val errorMsg: String)
