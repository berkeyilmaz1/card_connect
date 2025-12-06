package com.berkeyilmaz.cardapp.data.remote

import com.berkeyilmaz.cardapp.core.common.ResponseState
import com.berkeyilmaz.cardapp.domain.home.HomeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : HomeRepository {

    override suspend fun getCurrentUser(): ResponseState<FirebaseUser?> {
        return try {
            val currentUser = firebaseAuth.currentUser
            ResponseState.Success(currentUser)
        } catch (e: Exception) {
            ResponseState.Error(e)
        }
    }
}