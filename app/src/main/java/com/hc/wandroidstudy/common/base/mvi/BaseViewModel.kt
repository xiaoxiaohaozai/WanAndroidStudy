package com.hc.wandroidstudy.common.base.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @author ace
 * @createDate 2021/12/31
 * @explain
 *  mvi viewmodel 完整基类
 *  核心三个部分
 *  1 状态
 *  2 事件
 *  3 一次性事件(副作用),例如提示,dialog,跳转等
 */
interface UIState
interface UIEvent
interface UIEffect

abstract class BaseViewModel<State : UIState, Event : UIEvent, Effect : UIEffect> : ViewModel() {
    /**
     * 界面的初始状态
     */
    private val initialState by lazy {
        providerInitialState()
    }

    abstract fun providerInitialState(): State

    /**
     * 包含UI所有的状态
     * StateFlow和协程结合使用可以达到LiveData的效果
     */
     val _state = MutableStateFlow(initialState)

    val state = _state.asStateFlow()


    /**
     * 接收用户意图
     * SharedFlow特点
     * 1.的数据会以流的形式发送，不会丢失，新事件不会覆盖旧事件
     * 2.数据不是粘性的，消费一次就不会再次出现
     * 3.无法接收到 collect 之前发送的事件
     */
    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()


    /**
     * 事件带来的副作用，通常是 一次性事件 且 一对一的订阅关系
     * 例如：弹Toast、导航Fragment等
     * Channel特点
     *  1.每个消息只有一个订阅者可以收到，用于一对一的通信
     *  2.第一个订阅者可以收到 collect 之前的事件
     */
    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        subscribeEvents()
    }

    /**
     * 订阅事件
     */
    private fun subscribeEvents() {
        viewModelScope.launch {
            event.collect {
                handleEvent(it)
            }
        }
    }


    abstract fun handleEvent(event: Event)

    /**
     * 发送意图
     */
    fun sendEvent(event: Event) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    /**
     * 发送状态
     */
    protected fun setState(reduce: State.() -> State) {
        val newState = _state.value.reduce()
        _state.value = newState
    }

    /**
     * 发送副作用
     */
    protected fun setEffect(builder: () -> Effect) {
        val newEffect = builder()
        viewModelScope.launch {
            _effect.send(newEffect)
        }
    }


}