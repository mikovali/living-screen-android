package com.sensorfields.livingscreen.android.account.create

import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import javax.inject.Inject

class AccountCreateViewModel @Inject constructor(
    val googleSignInOptions: GoogleSignInOptions
) : ViewModel()
