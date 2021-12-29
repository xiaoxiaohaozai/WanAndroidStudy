package com.hc.wandroidstudy.common.base


import androidx.lifecycle.ViewModel
import com.hc.wandroidstudy.common.livedata_ext.EventMutableLiveData
import com.hc.wandroidstudy.common.livedata_ext.setEventValue

/**
 * @author ace
 * @createDate 2021/12/29
 * @explain
 */
abstract class BaseViewModel : ViewModel() {
    //全局显示loading
    private val loadingLiveData = EventMutableLiveData<Boolean>()
    //全局显示错误提示
    private val errorLiveData = EventMutableLiveData<String>()

    fun showLoading() {
        loadingLiveData.setEventValue(true)
    }

    fun hideLoading() {
        loadingLiveData.setEventValue(false)
    }

    fun showError(msg: String) {
        errorLiveData.setEventValue(msg)
    }

}