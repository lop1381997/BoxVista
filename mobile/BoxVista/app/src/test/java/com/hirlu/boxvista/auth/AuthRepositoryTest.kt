package com.hirlu.boxvista.auth

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthRepositoryTest {

    @Test
    fun `login stores returned token`() = runTest {
        val storage = FakeTokenStorage()
        val repository = AuthRepository(
            tokenStorage = storage,
            loginCall = { _, _ -> "jwt-token-123" },
            registerCall = { _, _ -> "ignored" },
        )

        val token = repository.login("test@example.com", "secret")

        assertEquals("jwt-token-123", token)
        assertEquals("jwt-token-123", repository.getStoredToken())
    }

    @Test
    fun `register stores returned token`() = runTest {
        val storage = FakeTokenStorage()
        val repository = AuthRepository(
            tokenStorage = storage,
            loginCall = { _, _ -> "ignored" },
            registerCall = { _, _ -> "jwt-token-456" },
        )

        val token = repository.register("new@example.com", "password123")

        assertEquals("jwt-token-456", token)
        assertEquals("jwt-token-456", repository.getStoredToken())
    }

    @Test
    fun `logout clears token`() {
        val storage = FakeTokenStorage(initialToken = "jwt-token-123")
        val repository = AuthRepository(
            tokenStorage = storage,
            loginCall = { _, _ -> "ignored" },
            registerCall = { _, _ -> "ignored" },
        )

        repository.logout()

        assertNull(repository.getStoredToken())
    }

    private class FakeTokenStorage(initialToken: String? = null) : TokenStorage {
        private var token: String? = initialToken

        override fun saveToken(token: String) {
            this.token = token
        }

        override fun getToken(): String? = token

        override fun clearToken() {
            token = null
        }
    }
}
