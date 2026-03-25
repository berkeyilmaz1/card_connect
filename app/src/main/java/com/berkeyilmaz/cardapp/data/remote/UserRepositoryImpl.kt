package com.berkeyilmaz.cardapp.data.remote

import com.berkeyilmaz.cardapp.core.common.ResponseState
import com.berkeyilmaz.cardapp.core.util.recordNonFatal
import com.berkeyilmaz.cardapp.domain.user.UserInfo
import com.berkeyilmaz.cardapp.domain.user.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val crashlytics: FirebaseCrashlytics,
    private val firebaseAuth: FirebaseAuth
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

    override suspend fun updateDisplayName(displayName: String): ResponseState<Unit> {
        return try {
            val user = firebaseAuth.currentUser ?: return ResponseState.Error("No user signed in")
            val request = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            user.updateProfile(request).await()
            ResponseState.Success(Unit)
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            ResponseState.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun savePhotoUrl(userId: String, photoUrl: String): ResponseState<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .set(mapOf("photoUrl" to photoUrl), SetOptions.merge())
                .await()
            ResponseState.Success(Unit)
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            ResponseState.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getPhotoUrl(userId: String): ResponseState<String?> {
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            ResponseState.Success(doc.getString("photoUrl"))
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            ResponseState.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun saveUserInfo(
        userId: String,
        displayName: String,
        phone: String
    ): ResponseState<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .set(mapOf("displayName" to displayName, "phone" to phone), SetOptions.merge())
                .await()
            val request = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            firebaseAuth.currentUser?.updateProfile(request)?.await()
            ResponseState.Success(Unit)
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            ResponseState.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getUserInfo(userId: String): ResponseState<UserInfo> {
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            ResponseState.Success(
                UserInfo(
                    displayName = doc.getString("displayName") ?: "",
                    phone = doc.getString("phone") ?: ""
                )
            )
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            ResponseState.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun deleteUserData(userId: String): ResponseState<Unit> {
        return try {
            val batch = firestore.batch()
            val userDocRef = firestore.collection("users").document(userId)
            
            // Alt koleksiyonları temizle
            val collections = listOf("contacts", "scans", "notifications")
            for (collectionPath in collections) {
                val snapshots = userDocRef.collection(collectionPath).get().await()
                for (doc in snapshots.documents) {
                    batch.delete(doc.reference)
                }
            }
            
            // Ana kullanıcı dökümanını sil
            batch.delete(userDocRef)
            
            // Tüm işlemleri tek seferde çalıştır
            batch.commit().await()
            
            ResponseState.Success(Unit)
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            // Hata mesajını daha anlaşılır kılalım
            val errorMessage = if (e.message?.contains("PERMISSION_DENIED") == true) {
                "Firebase Firestore Rules error: Please check your security rules for delete permission."
            } else {
                e.message ?: "Unknown error"
            }
            ResponseState.Error(errorMessage)
        }
    }

}
