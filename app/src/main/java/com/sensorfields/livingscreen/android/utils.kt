package com.sensorfields.livingscreen.android

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun producer(crossinline producer: () -> ViewModel) = object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return producer() as T
    }
}

inline fun <T> MutableLiveData<T>.reduceValue(operation: T.() -> T) {
    postValue(value!!.operation())
}

class ActionLiveData<T> : MutableLiveData<T>() {

    override fun setValue(value: T) {
        require(value != null) { "Cannot set null value" }
        super.setValue(value)
        super.setValue(null)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer<T> { value -> if (value != null) observer.onChanged(value) })
    }
}
