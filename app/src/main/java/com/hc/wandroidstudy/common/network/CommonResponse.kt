package com.hc.wandroidstudy.common.network


data class CommonResponse<T>(val data: T, val errorCode: Int, val errorMsg: String)
