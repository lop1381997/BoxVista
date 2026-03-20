package com.hirlu.boxvista.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

interface TokenStorage {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
}

class SecureTokenStorage(context: Context) : TokenStorage {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferences = EncryptedSharedPreferences.create(
        context,
        FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    override fun saveToken(token: String) {
        preferences.edit().putString(KEY_TOKEN, token).apply()
    }

    override fun getToken(): String? = preferences.getString(KEY_TOKEN, null)

    override fun clearToken() {
        preferences.edit().remove(KEY_TOKEN).apply()
    }

    private companion object {
        const val FILE_NAME = "boxvista_secure_session"
        const val KEY_TOKEN = "auth_token"
    }
}
