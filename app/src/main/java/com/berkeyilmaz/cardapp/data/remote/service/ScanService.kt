package com.berkeyilmaz.cardapp.data.remote.service

import com.berkeyilmaz.cardapp.domain.contact.model.Contact
import com.berkeyilmaz.cardapp.domain.scan_result.model.ContactRequest
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ScanService {
    @Multipart
    @POST("scan/process-image")
    suspend fun scanImage(
        @Part file: MultipartBody.Part,
        @Header("Authorization") authorization: String
    ): Response<ScanResponse>


    @POST("contacts")
    suspend fun createContact(
        @Body contactRequest: ContactRequest,
        @Header("Authorization") authorization: String
    ): Response<Unit>

    @GET("contacts")
    suspend fun getRemoteContacts(
        @Header("Authorization") authorization: String
    ): Response<List<Contact>>
}