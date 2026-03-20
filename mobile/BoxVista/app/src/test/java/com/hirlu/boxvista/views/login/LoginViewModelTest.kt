package com.hirlu.boxvista.views.login

import com.hirlu.boxvista.services.AuthServiceProtocol
import com.hirlu.boxvista.services.TokenStoreProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success stores token and sets logged in`() = runTest {
        val tokenStore = FakeTokenStore()
        val vm = LoginViewModel(
            authService = FakeAuthService(token = "jwt-token-123456789"),
            tokenStore = tokenStore
        )

        vm.onEmailChanged("test@boxvista.com")
        vm.onPasswordChanged("secret")
        vm.login()
        advanceUntilIdle()

        assertTrue(vm.state.value.isLoggedIn)
        assertEquals("jwt-token-123456789", tokenStore.savedToken)
        assertEquals(null, vm.state.value.error)
    }

    @Test
    fun `login unauthorized maps to credentials error`() = runTest {
        val vm = LoginViewModel(
            authService = FakeAuthService(
                error = HttpException(
                    Response.error<String>(
                        401,
                        "unauthorized".toResponseBody("text/plain".toMediaType())
                    )
                )
            ),
            tokenStore = FakeTokenStore()
        )

        vm.onEmailChanged("test@boxvista.com")
        vm.onPasswordChanged("bad")
        vm.login()
        advanceUntilIdle()

        assertFalse(vm.state.value.isLoggedIn)
        assertEquals("Credenciales inválidas. Revisa email y contraseña.", vm.state.value.error)
    }

    @Test
    fun `login network failure maps to offline message`() = runTest {
        val vm = LoginViewModel(
            authService = FakeAuthService(error = IOException("timeout")),
            tokenStore = FakeTokenStore()
        )

        vm.onEmailChanged("test@boxvista.com")
        vm.onPasswordChanged("secret")
        vm.login()
        advanceUntilIdle()

        assertFalse(vm.state.value.isLoggedIn)
        assertEquals("Sin conexión. Revisa tu red e inténtalo de nuevo.", vm.state.value.error)
    }

    private class FakeAuthService(
        private val token: String = "token",
        private val error: Throwable? = null
    ) : AuthServiceProtocol {
        override suspend fun login(email: String, password: String): String {
            error?.let { throw it }
            return token
        }
    }

    private class FakeTokenStore : TokenStoreProtocol {
        var savedToken: String? = null

        override fun saveToken(token: String) {
            savedToken = token
        }

        override fun getToken(): String? = savedToken

        override fun clearToken() {
            savedToken = null
        }
    }
}
