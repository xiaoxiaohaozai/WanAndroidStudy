package com.hc.wandroidstudy.common.mvi_core

import androidx.annotation.MainThread
import androidx.lifecycle.*

import kotlin.reflect.KProperty1


/**
 * 发送状态
 */
fun <T> MutableLiveData<T>.setState(reducer: T.() -> T) {
    this.value = this.value?.reducer()
}

/**
 * 发送单次事件
 */
fun <T> SingleLiveEvent<T>.setEvent(value: T) {
    this.value = value
}

/**
 * 获取状态
 */
fun <T, R> withState(state: LiveData<T>, block: (T) -> R): R? {
    return state.value?.let(block)
}


internal data class StateTuple1<A>(val a: A)
internal data class StateTuple2<A, B>(val a: A, val b: B)
internal data class StateTuple3<A, B, C>(val a: A, val b: B, val c: C)

/**
 * 全状态监听
 */
@MainThread
inline fun <T> LiveData<T>.observeState(owner: LifecycleOwner, crossinline onChange: (T) -> Unit): Observer<T> {
    val wrappedObserver = Observer<T> {
        onChange.invoke(it)
    }
    observe(owner, wrappedObserver)
    return wrappedObserver
}


/**
 * 局部状态,支持单个参数
 * 防抖
 */
@MainThread
fun <T, A> LiveData<T>.observeState(
        lifecycleOwner: LifecycleOwner,
        prop1: KProperty1<T, A>,
        action: (A) -> Unit
) {
    this.map { StateTuple1(prop1.get(it)) } //获取属性值
            .distinctUntilChanged() // 值发生变化了才会触发回调
            .observe(lifecycleOwner) { (a) ->
                action.invoke(a)
            }
}


/**
 * 局部状态,支持两个参数
 */
@MainThread
fun <T, A, B> LiveData<T>.observeState(
        lifecycleOwner: LifecycleOwner,
        prop1: KProperty1<T, A>,
        prop2: KProperty1<T, B>,
        action: (A, B) -> Unit
) {
    this.map {
        StateTuple2(prop1.get(it), prop2.get(it))
    }.distinctUntilChanged().observe(lifecycleOwner) { (a, b) ->
        action.invoke(a, b)
    }
}

/**
 * 局部状态,支持三个参数
 */
@MainThread
fun <T, A, B, C> LiveData<T>.observeState(
        lifecycleOwner: LifecycleOwner,
        prop1: KProperty1<T, A>,
        prop2: KProperty1<T, B>,
        prop3: KProperty1<T, C>,
        action: (A, B, C) -> Unit
) {
    this.map {
        StateTuple3(prop1.get(it), prop2.get(it), prop3.get(it))
    }.distinctUntilChanged().observe(lifecycleOwner) { (a, b, c) ->
        action.invoke(a, b, c)
    }
}

fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> {
    return this
}