package com.berkeyilmaz.cardapp.domain.home

import com.berkeyilmaz.cardapp.data.model.ApiResult
import com.google.firebase.auth.FirebaseUser

interface HomeRepository {
    suspend fun getCurrentUser(): ApiResult<FirebaseUser?>
}