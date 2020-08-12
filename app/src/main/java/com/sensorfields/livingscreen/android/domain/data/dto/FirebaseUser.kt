package com.sensorfields.livingscreen.android.domain.data.dto

import com.google.firebase.auth.FirebaseUser
import com.sensorfields.livingscreen.android.domain.Account

fun FirebaseUser?.toAccount(): Account? {
    return if (this == null) null else Account()
}
