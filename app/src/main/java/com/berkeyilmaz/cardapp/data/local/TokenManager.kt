package com.berkeyilmaz.cardapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.util.Base64
import com.berkeyilmaz.cardapp.core.manager.KeyStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.SecureRandom

private val Context.dataStore by preferencesDataStore("secure_prefs")

class TokenManager(context: Context) {

    private val dataStore = context.dataStore
    private val secretKey: SecretKey = KeyStoreManager.getOrCreateKey()
    private val secureRandom = SecureRandom()

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    private fun encrypt(data: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12)
        secureRandom.nextBytes(iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        val encrypted = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        val combined = iv + encrypted
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    private fun decrypt(data: String): String {
        val combined = Base64.decode(data, Base64.DEFAULT)
        val iv = combined.copyOfRange(0, 12)
        val encrypted = combined.copyOfRange(12, combined.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        return String(cipher.doFinal(encrypted), Charsets.UTF_8)
    }

    suspend fun saveTokens(access: String, refresh: String) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = encrypt(access)
            prefs[REFRESH_TOKEN] = encrypt(refresh)
        }
    }

    suspend fun getAccessToken(): String? {
        return dataStore.data.map { prefs ->
            prefs[ACCESS_TOKEN]?.let { decrypt(it) }
        }.first()
    }

    suspend fun getRefreshToken(): String? {
        return dataStore.data.map { prefs ->
            prefs[REFRESH_TOKEN]?.let { decrypt(it) }
        }.first()
    }

    suspend fun clearTokens() {
        dataStore.edit { it.clear() }
    }
}

