package com.hc.wandroidstudy.common.livedata_ext

import androidx.annotation.MainThread
import androidx.lifecycle.*

/**
 * @author ace
 * @createDate 2021/12/29
 * @explain 使用 包装类 解决 LiveData 粘性事件的问题
 *
 *  typealias 通过设置别名,将 LiveData<Event<T>>形式简化为XXXLiveData<T>
 *  inline 在编译时，会将此修饰符修饰的函数复制到调用处（称为内联），避免创建 Function 对象，以减少创建对象的内存开销。
 *  crossinline 需要配合 inline 使用，告诉编译器不能使用 return，否则就跳出调用处函数，但是可以使用 return@label 跳出指定外层。
 */
typealias EventMutableLiveData<T> = MutableLiveData<Event<T>>

typealias EventLiveData<T> = LiveData<Event<T>>

/**情况1 不考虑粘性问题,和UI数据一样,每次通知观察者*/
@MainThread
inline fun <T> EventLiveData<T>.observeEvent(owner: LifecycleOwner, crossinline onChange: (T) -> Unit): Observer<Event<T>> {
    val wrappedObserver = Observer<Event<T>> {
        onChange.invoke(it.peekContent())
    }
    observe(owner, wrappedObserver)
    return wrappedObserver
}

/**情况2 整个事件只能被唯一观察者消费*/
@MainThread
inline fun <T> EventLiveData<T>.observeSingleEvent(owner: LifecycleOwner, crossinline onChange: (T) -> Unit): Observer<Event<T>> {
    val wrappedObserver = Observer<Event<T>> {
        it.getContentIfNotHandled()?.let { content ->
            onChange.invoke(content)
        }
    }
    observe(owner, wrappedObserver)
    return wrappedObserver
}

/**情况3 事件可被多个观察者消费，且每个观察者 [viewModelStore] 仅能消费一次*/

@MainThread
inline fun <T> EventLiveData<T>.observeSingleEvent(
        owner: LifecycleOwner,
        viewModelStore: ViewModelStore,
        crossinline onChanged: (T) -> Unit
): Observer<Event<T>> {
    val wrappedObserver = Observer<Event<T>> { t ->
        t.getContentIfNotHandled(viewModelStore)?.let { data ->
            onChanged.invoke(data)
        }
    }
    observe(owner, wrappedObserver)
    return wrappedObserver
}


fun <T> EventMutableLiveData<T>.postEventValue(value: T) {
    postValue(Event(value))
}

fun <T> EventMutableLiveData<T>.setEventValue(value: T) {
    setValue(Event(value))
}