package com.berkeyilmaz.cardapp.data.remote

import com.berkeyilmaz.cardapp.data.remote.service.ScanService
import com.berkeyilmaz.cardapp.domain.scan.ScanRepository
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import okhttp3.MultipartBody
import javax.inject.Inject

class ScanRepositoryImpl @Inject constructor(
    private val scanService: ScanService,
    private val firebaseAuth: FirebaseAuth
) : ScanRepository {

    private suspend fun getAuthToken(): String {
        return try {
            val token = firebaseAuth.currentUser?.getIdToken(false)?.await()?.token
            "Bearer ${token ?: ""}"
        } catch (e: Exception) {
            "Bearer "
        }
    }

    override suspend fun scanImage(image: MultipartBody.Part): Result<ScanResponse> {
        return try {
            val authToken = getAuthToken()
            val response = scanService.scanImage(image, authToken)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Scan failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createContact(contactRequest: ContactRequest): Result<Unit> {
        return try {
            val authToken = getAuthToken()
            val response = scanService.createContact(contactRequest, authToken)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Create contact failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}