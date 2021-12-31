package com.hc.wandroidstudy.common.base.mvi

import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.flow.*
import kotlin.reflect.KProperty1

/**
 * @author ace
 * @createDate 2021/12/31
 * @explain
 *  局部监听
 */
internal data class StateTuple1<A>(val a: A)
internal data class StateTuple2<A, B>(val a: A, val b: B)
internal data class StateTuple3<A, B, C>(val a: A, val b: B, val c: C)


/**
 * 局部状态,支持单个参数
 * 防抖
 */
suspend fun <T, A> Flow<T>.collectState(prop1: KProperty1<T, A>, action: (A) -> Unit) {
    this.map {
        StateTuple1(prop1.get(it))
    }//获取属性值
        .distinctUntilChanged() //属性值变化
        .collectLatest { (a) -> action.invoke(a) }
}


/**
 * 局部状态,支持两个参数
 */
suspend fun <T, A, B> Flow<T>.collectState(
    prop1: KProperty1<T, A>,
    prop2: KProperty1<T, B>,
    action: (A, B) -> Unit
) {
    this.map { StateTuple2(prop1.get(it), prop2.get(it)) }
        .distinctUntilChanged()
        .collectLatest { (a, b) -> action.invoke(a, b) }
}

/**
 * 局部状态,支持三个参数
 */
suspend fun <T, A, B, C> Flow<T>.collectState(
    prop1: KProperty1<T, A>,
    prop2: KProperty1<T, B>,
    prop3: KProperty1<T, C>,
    action: (A, B, C) -> Unit
) {
    this.map { StateTuple3(prop1.get(it), prop2.get(it), prop3.get(it)) }
        .distinctUntilChanged()
        .collectLatest { (a, b, c) -> action.invoke(a, b, c) }
}

