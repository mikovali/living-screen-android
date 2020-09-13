package com.sensorfields.livingscreen.android

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

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

class SignInWithGoogle : ActivityResultContract<GoogleSignInOptions, GoogleSignInAccount?>() {

    override fun createIntent(context: Context, input: GoogleSignInOptions): Intent {
        return GoogleSignIn.getClient(context, input).signInIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): GoogleSignInAccount? {
        return try {
            GoogleSignIn.getSignedInAccountFromIntent(intent).result
        } catch (e: Exception) {
            null
        }
    }
}

suspend fun <T> Task<T>.await(): T = suspendCoroutine { continuation ->
    addOnSuccessListener { result -> continuation.resume(result) }
    addOnFailureListener { e -> continuation.resumeWithException(e) }
}

fun <T> viewState(
    create: (View) -> T,
    destroy: (T.() -> Unit)? = null
): ReadOnlyProperty<Fragment, T> = FragmentOnViewCreatedDelegate(create, destroy)

private class FragmentOnViewCreatedDelegate<T>(
    private val create: (View) -> T,
    private val destroy: (T.() -> Unit)? = null
) : ReadOnlyProperty<Fragment, T> {

    private var value: T? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return value ?: run {
            thisRef.viewLifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        value?.let { currentValue -> destroy?.invoke(currentValue) }
                        value = null
                        thisRef.viewLifecycleOwner.lifecycle.removeObserver(this)
                    }
                }
            })
            create(thisRef.requireView()).also { value = it }
        }
    }
}
