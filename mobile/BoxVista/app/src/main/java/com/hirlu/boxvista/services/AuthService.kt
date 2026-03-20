package com.hirlu.boxvista.services

import com.hirlu.boxvista.NetworkManager

interface AuthServiceProtocol {
    suspend fun login(email: String, password: String): String
}

class AuthService : AuthServiceProtocol {
    override suspend fun login(email: String, password: String): String {
        return NetworkManager.login(email = email, password = password)
    }
}
