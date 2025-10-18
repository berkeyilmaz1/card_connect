package com.berkeyilmaz.cardapp.data.remote

import com.berkeyilmaz.cardapp.data.model.ApiResult
import com.berkeyilmaz.cardapp.domain.home.HomeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : HomeRepository {

    override suspend fun getCurrentUser(): ApiResult<FirebaseUser?> {
        return try {
            val currentUser = firebaseAuth.currentUser
            ApiResult.Success(currentUser)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

}