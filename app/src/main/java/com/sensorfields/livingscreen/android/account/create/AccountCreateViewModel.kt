package com.sensorfields.livingscreen.android.account.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.sensorfields.livingscreen.android.ActionLiveData
import com.sensorfields.livingscreen.android.domain.usecase.SignInWithGoogleUseCase
import com.sensorfields.livingscreen.android.reduceValue
import kotlinx.coroutines.launch
import javax.inject.Inject

class AccountCreateViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : ViewModel() {

    private val _state = MutableLiveData<AccountCreateState>(AccountCreateState())
    val state: LiveData<AccountCreateState> = _state

    private val _action = ActionLiveData<AccountCreateAction>()
    val action: LiveData<AccountCreateAction> = _action

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _state.reduceValue { copy(isInProgress = true) }
            when (signInWithGoogleUseCase(idToken)) {
                is Either.Right -> _action.postValue(AccountCreateAction.NavigateToMain)
                is Either.Left -> {
                    _state.reduceValue { copy(isInProgress = false) }
                    // TODO error
                }
            }
        }
    }
}
