package com.berkeyilmaz.cardapp.domain.home

import com.berkeyilmaz.cardapp.core.common.ResponseState
import com.google.firebase.auth.FirebaseUser

interface HomeRepository {
    suspend fun getCurrentUser(): ResponseState<FirebaseUser?>
}