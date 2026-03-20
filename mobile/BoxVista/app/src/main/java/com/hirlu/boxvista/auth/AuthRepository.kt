package com.hirlu.boxvista.auth

import com.hirlu.boxvista.NetworkManager

class AuthRepository(
    private val tokenStorage: TokenStorage,
    private val loginCall: suspend (String, String) -> String = NetworkManager::login,
) {
    suspend fun login(email: String, password: String): String {
        val token = loginCall(email, password)
        tokenStorage.saveToken(token)
        return token
    }

    fun getStoredToken(): String? = tokenStorage.getToken()

    fun logout() {
        tokenStorage.clearToken()
    }
}
