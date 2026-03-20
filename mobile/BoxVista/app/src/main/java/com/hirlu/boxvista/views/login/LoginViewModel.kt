package com.hirlu.boxvista.views.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hirlu.boxvista.services.AuthService
import com.hirlu.boxvista.services.AuthServiceProtocol
import com.hirlu.boxvista.services.TokenStoreProtocol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val tokenPreview: String? = null,
    val error: String? = null
)

class LoginViewModel(
    private val authService: AuthServiceProtocol = AuthService(),
    private val tokenStore: TokenStoreProtocol
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEmailChanged(value: String) {
        _state.update { it.copy(email = value, error = null) }
    }

    fun onPasswordChanged(value: String) {
        _state.update { it.copy(password = value, error = null) }
    }

    fun loadExistingSession() {
        val existing = tokenStore.getToken()
        if (!existing.isNullOrBlank()) {
            _state.update {
                it.copy(
                    isLoggedIn = true,
                    tokenPreview = maskToken(existing),
                    error = null
                )
            }
        }
    }

    fun login() {
        val email = state.value.email.trim()
        val password = state.value.password

        if (state.value.isLoading) return

        if (email.isBlank() || password.isBlank()) {
            _state.update { it.copy(error = "Email y contraseña son obligatorios") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, isLoggedIn = false) }

            runCatching { authService.login(email = email, password = password) }
                .onSuccess { token ->
                    tokenStore.saveToken(token)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            tokenPreview = maskToken(token),
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = false,
                            error = toUserMessage(e)
                        )
                    }
                }
        }
    }

    fun logout() {
        tokenStore.clearToken()
        _state.update { it.copy(isLoggedIn = false, tokenPreview = null, error = null, password = "") }
    }

    private fun toUserMessage(error: Throwable): String {
        return when (error) {
            is IOException -> "Sin conexión. Revisa tu red e inténtalo de nuevo."
            is HttpException -> when (error.code()) {
                401 -> "Credenciales inválidas. Revisa email y contraseña."
                403 -> "Tu usuario no tiene permisos para iniciar sesión."
                else -> "Error del servidor (${error.code()}). Inténtalo de nuevo."
            }
            else -> error.message ?: "No se pudo iniciar sesión"
        }
    }

    private fun maskToken(token: String): String {
        val clean = token.trim()
        if (clean.length <= 10) return "****"
        return "${clean.take(6)}...${clean.takeLast(4)}"
    }
}
