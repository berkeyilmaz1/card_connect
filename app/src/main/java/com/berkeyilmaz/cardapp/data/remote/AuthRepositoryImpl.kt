package com.berkeyilmaz.cardapp.data.remote


import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.common.ResponseState
import com.berkeyilmaz.cardapp.domain.auth.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.berkeyilmaz.cardapp.core.util.recordNonFatal
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @param:ApplicationContext val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val credentialManager: CredentialManager,
    private val crashlytics: FirebaseCrashlytics
) : AuthRepository {
    override suspend fun getCurrentUser(): ResponseState<FirebaseUser?> {
        val currentUser = firebaseAuth.currentUser
        return if (currentUser != null) {
            ResponseState.Success(currentUser)
        } else {
            ResponseState.Error(context.getString(R.string.no_user_logged_in))
        }
    }

    override suspend fun reloadCurrentUser(): Boolean {
        val result = getCurrentUser()
        if (result is ResponseState.Error) return false

        val user = (result as ResponseState.Success).data
        return try {
            user?.reload()?.await()
            true
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun logInWithEmail(
        email: String, password: String
    ): ResponseState<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            ResponseState.Success(Unit, context.getString(R.string.login_successful))
        } catch (e: FirebaseAuthException) {
            return when (e.errorCode) {
                "ERROR_USER_NOT_FOUND" -> {
                    ResponseState.Error(context.getString(R.string.user_not_found))
                }

                "ERROR_WRONG_PASSWORD" -> {
                    ResponseState.Error(context.getString(R.string.wrong_password))
                }

                else -> {
                    crashlytics.recordNonFatal(e)
                    ResponseState.Error(
                        e.localizedMessage ?: context.getString(R.string.auth_error)
                    )
                }
            }
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            ResponseState.Error(
                e.localizedMessage ?: context.getString(R.string.auth_error)
            )
        }
    }

    override suspend fun signUpWithEmail(
        email: String, password: String
    ): ResponseState<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            sendEmailVerification()
            ResponseState.Success(Unit, context.getString(R.string.registration_successful_welcome))
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            ResponseState.Error(
                e.localizedMessage ?: context.getString(R.string.register_error)
            )
        }
    }

    override suspend fun signInWithGoogle(): ResponseState<Unit> {
        try {
            credentialManager.clearCredentialState(
                ClearCredentialStateRequest()
            )
        } catch (e: Exception) {
            Log.w("BerkeTAG", "Credential state temizlenemedi, devam ediliyor", e)
        }
        return try {
            val googleIdOption = GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setServerClientId(context.getString(R.string.default_web_client_id)).build()

            val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

            val result = credentialManager.getCredential(
                request = request, context = context
            )

            val credential = result.credential

            if (credential !is CustomCredential) {
                return ResponseState.Error(context.getString(R.string.googleInvalidCredentialType))
            }

            if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                return ResponseState.Error(context.getString(R.string.googleUnexpectedCredentialType))
            }

            val googleIdTokenCredential = try {
                GoogleIdTokenCredential.createFrom(credential.data)
            } catch (e: GoogleIdTokenParsingException) {
                return ResponseState.Error(
                    "${context.getString(R.string.googleIdTokenParsingError)}: ${e.message}"
                )
            }

            val firebaseCredential = GoogleAuthProvider.getCredential(
                googleIdTokenCredential.idToken, null
            )
            firebaseAuth.signInWithCredential(firebaseCredential).await()
            ResponseState.Success(Unit, context.getString(R.string.googleSignInSuccessful))
        } catch (e: GetCredentialCancellationException) {
            ResponseState.Error(context.getString(R.string.googleSignInCancelled))
        } catch (e: GetCredentialException) {
            crashlytics.recordNonFatal(e)
            Log.e("BerkeTAG", "Google GetCredentialException", e)
            ResponseState.Error(
                "${context.getString(R.string.googleCredentialError)}: ${e.message}"
            )
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            ResponseState.Error(
                e.message ?: context.getString(R.string.googleSignInUnknownError)
            )
        }
    }


    override suspend fun sendForgotPasswordEmail(email: String): ResponseState<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            ResponseState.Success(
                Unit, context.getString(R.string.password_reset_email_sent_please_check_your_inbox)
            )
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            ResponseState.Error(
                e.localizedMessage ?: context.getString(R.string.send_forgot_password_error)
            )
        }
    }

    override suspend fun sendEmailVerification(): ResponseState<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return ResponseState.Error(context.getString(R.string.no_user_logged_in))
            user.sendEmailVerification().await()
            ResponseState.Success(
                Unit, context.getString(R.string.verification_email_sent_please_check_your_inbox)
            )
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            ResponseState.Error(
                e.localizedMessage ?: context.getString(R.string.send_email_verif_error)
            )
        }
    }

    override suspend fun logout(): ResponseState<Unit> {
        return try {
            firebaseAuth.signOut()
            credentialManager.clearCredentialState(
                ClearCredentialStateRequest()
            )
            ResponseState.Success(Unit, context.getString(R.string.logged_out_successfully))
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            ResponseState.Error(
                e.localizedMessage ?: context.getString(R.string.logout_error)
            )
        }
    }

    override suspend fun deleteAccount(): ResponseState<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return ResponseState.Error(context.getString(R.string.no_user_logged_in))
            user.delete().await()
            ResponseState.Success(Unit, context.getString(R.string.account_deleted_successfully))
        } catch (e: FirebaseAuthException) {
            return if (e.errorCode == "ERROR_REQUIRES_RECENT_LOGIN") {
                ResponseState.Error(
                    context.getString(R.string.re_authentication_required)
                )
            } else {
                crashlytics.recordNonFatal(e)
                ResponseState.Error(
                    e.localizedMessage ?: context.getString(R.string.account_deletion_error)
                )
            }
        }
    }


    override suspend fun reAuthenticate(password: String): ResponseState<Unit> {
        val user = firebaseAuth.currentUser ?: return ResponseState.Error(
            context.getString(
                R.string.no_user_logged_in
            )
        )
        val email =
            user.email ?: return ResponseState.Error(context.getString(R.string.no_user_logged_in))
        val credential = EmailAuthProvider.getCredential(email, password)
        return try {
            user.reauthenticate(credential).await()
            ResponseState.Success(Unit, context.getString(R.string.re_authentication_successful))
        } catch (e: Exception) {
            crashlytics.recordNonFatal(e)
            ResponseState.Error(
                e.localizedMessage ?: context.getString(R.string.re_authentication_failed)
            )
        }
    }
}