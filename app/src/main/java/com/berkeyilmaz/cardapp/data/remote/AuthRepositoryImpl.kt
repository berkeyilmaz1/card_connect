package com.berkeyilmaz.cardapp.data.remote


import android.content.Context
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.domain.auth.AuthRepository
import com.berkeyilmaz.cardapp.domain.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {
    override suspend fun getCurrentUser(): AuthResult<FirebaseUser?> {
        val currentUser = firebaseAuth.currentUser
        return if (currentUser != null) {
            AuthResult.Success(currentUser)
        } else {
            AuthResult.Error.Generic(context.getString(R.string.no_user_logged_in))
        }
    }

    override suspend fun reloadCurrentUser(): Boolean {
        val result = getCurrentUser()
        if (result is AuthResult.Error) return false

        val user = (result as AuthResult.Success).data
        return try {
            user?.reload()?.await()
            true
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun loginOrRegister(email: String, password: String): AuthResult<Unit> {
        return try {
            // Try to sign in
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success(Unit, context.getString(R.string.login_successful))
        } catch (e: FirebaseAuthException) {
            when (e.errorCode) {
                "ERROR_USER_NOT_FOUND" -> {
                    // If user not found, try to register
                    return registerUser(email, password)
                }

                "ERROR_WRONG_PASSWORD" -> {
                    return AuthResult.Error.Generic(context.getString(R.string.wrong_password))
                }

                else -> {
                    return AuthResult.Error.Generic(
                        e.localizedMessage ?: context.getString(R.string.auth_error)
                    )
                }
            }
        }
    }

    override suspend fun sendForgotPasswordEmail(email: String): AuthResult<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            AuthResult.Success(
                Unit,
                context.getString(R.string.password_reset_email_sent_please_check_your_inbox)
            )
        } catch (e: Exception) {
            AuthResult.Error.Generic(
                e.localizedMessage ?: context.getString(R.string.send_forgot_password_error)
            )
        }
    }

    override suspend fun sendEmailVerification(): AuthResult<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return AuthResult.Error.Generic(context.getString(R.string.no_user_logged_in))
            user.sendEmailVerification().await()
            AuthResult.Success(
                Unit,
                context.getString(R.string.verification_email_sent_please_check_your_inbox)
            )
        } catch (e: Exception) {
            AuthResult.Error.Generic(
                e.localizedMessage ?: context.getString(R.string.send_email_verif_error)
            )
        }
    }

    override suspend fun logout(): AuthResult<Unit> {
        return try {
            firebaseAuth.signOut()
            AuthResult.Success(Unit, context.getString(R.string.logged_out_successfully))
        } catch (e: Exception) {
            AuthResult.Error.Generic(e.localizedMessage ?: context.getString(R.string.logout_error))
        }
    }

    override suspend fun deleteAccount(): AuthResult<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return AuthResult.Error.Generic(context.getString(R.string.no_user_logged_in))
            user.delete().await()
            AuthResult.Success(Unit, context.getString(R.string.account_deleted_successfully))
        } catch (e: FirebaseAuthException) {
            return if (e.errorCode == "ERROR_REQUIRES_RECENT_LOGIN") {
                AuthResult.Error.ReAuthNeeded(
                    context.getString(R.string.re_authentication_required)
                )
            } else {
                AuthResult.Error.Generic(
                    e.localizedMessage ?: context.getString(R.string.account_deletion_error)
                )
            }
        }
    }

    suspend fun registerUser(email: String, password: String): AuthResult<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            sendEmailVerification()
            AuthResult.Success(Unit, context.getString(R.string.registration_successful_welcome))
        } catch (e: Exception) {
            AuthResult.Error.Generic(
                e.localizedMessage ?: context.getString(R.string.register_error)
            )
        }
    }

    suspend fun reAuthenticate(password: String): AuthResult<Unit> {
        val user = firebaseAuth.currentUser
            ?: return AuthResult.Error.Generic(context.getString(R.string.no_user_logged_in))
        val email =
            user.email
                ?: return AuthResult.Error.Generic(context.getString(R.string.no_user_logged_in))
        val credential = EmailAuthProvider.getCredential(email, password)
        return try {
            user.reauthenticate(credential).await()
            AuthResult.Success(Unit, context.getString(R.string.re_authentication_successful))
        } catch (e: Exception) {
            AuthResult.Error.Generic(
                e.localizedMessage ?: context.getString(R.string.re_authentication_failed)
            )
        }
    }
}