package com.berkeyilmaz.cardapp.data.remote

import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.common.ResponseState
import com.berkeyilmaz.cardapp.domain.home.HomeRepository
import com.berkeyilmaz.cardapp.core.util.recordNonFatal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val crashlytics: FirebaseCrashlytics
) : HomeRepository {

    override suspend fun getCurrentUser(): ResponseState<FirebaseUser?> {
        return try {
            val currentUser = firebaseAuth.currentUser
            ResponseState.Success(currentUser)
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            ResponseState.Error("todo: handle error message")
        }
    }
}