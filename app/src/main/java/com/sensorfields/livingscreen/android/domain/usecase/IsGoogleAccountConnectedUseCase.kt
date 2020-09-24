package com.sensorfields.livingscreen.android.domain.usecase

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class IsGoogleAccountConnectedUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(): Boolean = GoogleSignIn.getLastSignedInAccount(context) != null
}
