package com.sensorfields.livingscreen.android.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    fun observeAccount(): Flow<Account?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            offer(auth.currentUser.toAccount())
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }
}

private fun FirebaseUser?.toAccount(): Account? {
    return if (this == null) null else Account()
}
