package com.berkeyilmaz.cardapp.data.remote

import com.berkeyilmaz.cardapp.core.common.ResponseState
import com.berkeyilmaz.cardapp.core.util.recordNonFatal
import com.berkeyilmaz.cardapp.domain.user.UserRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val crashlytics: FirebaseCrashlytics
) : UserRepository {

    override suspend fun saveAnalyticsConsent(userId: String, consent: Boolean): ResponseState<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .set(mapOf("analyticsConsent" to consent), SetOptions.merge())
                .await()
            ResponseState.Success(Unit)
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            ResponseState.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getAnalyticsConsent(userId: String): ResponseState<Boolean> {
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            ResponseState.Success(doc.getBoolean("analyticsConsent") ?: false)
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            ResponseState.Error(e.message ?: "Unknown error")
        }
    }
}
