package com.hirlu.boxvista.auth

import com.hirlu.boxvista.NetworkManager

class AuthRepository(
    private val tokenStorage: TokenStorage,
    private val loginCall: suspend (String, String) -> String = NetworkManager::login,
    private val registerCall: suspend (String, String) -> String = NetworkManager::register,
) {
    suspend fun login(email: String, password: String): String {
        val token = loginCall(email, password)
        tokenStorage.saveToken(token)
        return token
    }

    suspend fun register(email: String, password: String): String {
        val token = registerCall(email, password)
        tokenStorage.saveToken(token)
        return token
    }

    fun getStoredToken(): String? = tokenStorage.getToken()

    fun isAuthenticated(): Boolean = !getStoredToken().isNullOrBlank()

    fun logout() {
        tokenStorage.clearToken()
    }
}
