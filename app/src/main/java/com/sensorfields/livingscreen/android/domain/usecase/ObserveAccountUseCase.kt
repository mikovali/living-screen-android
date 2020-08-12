package com.sensorfields.livingscreen.android.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.sensorfields.livingscreen.android.domain.Account
import com.sensorfields.livingscreen.android.domain.data.dto.toAccount
import dagger.Reusable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@Reusable
class ObserveAccountUseCase @Inject constructor(private val firebaseAuth: FirebaseAuth) {

    operator fun invoke(): Flow<Account?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            offer(auth.currentUser.toAccount())
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }
}
