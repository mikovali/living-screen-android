package com.sensorfields.livingscreen.android.account.create

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sensorfields.livingscreen.android.ActionLiveData
import com.sensorfields.livingscreen.android.domain.AccountRepository
import com.sensorfields.livingscreen.android.reduceValue
import kotlinx.coroutines.launch
import javax.inject.Inject

class AccountCreateViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _state = MutableLiveData<AccountCreateState>(AccountCreateState())
    val state: LiveData<AccountCreateState> = _state

    private val _action = ActionLiveData<AccountCreateAction>()
    val action: LiveData<AccountCreateAction> = _action

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _state.reduceValue { copy(isInProgress = true) }
            try {
                accountRepository.signInWithGoogle(idToken)
                _action.postValue(AccountCreateAction.NavigateToMain)
            } catch (e: Exception) {
                _state.reduceValue { copy(isInProgress = false) }
                Log.e("AAA", "ERROR ON SIGN IN WITH GOOGLE", e)
                // TODO error
            }
        }
    }
}
