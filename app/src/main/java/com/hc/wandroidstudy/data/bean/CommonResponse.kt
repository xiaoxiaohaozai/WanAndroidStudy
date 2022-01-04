package com.hc.wandroidstudy.data.bean


data class CommonResponse<T>(val data: T, val errorCode: Int, val errorMsg: String)
