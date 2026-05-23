package com.hirlu.boxvista.views.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hirlu.boxvista.auth.AuthRepository
import com.hirlu.boxvista.auth.TokenStorage
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    authRepository: AuthRepository,
    isAuthenticated: Boolean,
    onSessionChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val canSubmit = email.trim().isNotEmpty() && password.length >= 8 && !isLoading

    fun runAuth(action: suspend () -> Unit) {
        scope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null
            runCatching { action() }
                .onSuccess {
                    password = ""
                    successMessage = "Sesión iniciada."
                    onSessionChanged(true)
                }
                .onFailure { error ->
                    errorMessage = error.message ?: "No se pudo iniciar sesión."
                }
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Cuenta",
            style = MaterialTheme.typography.headlineSmall,
        )

        if (isAuthenticated) {
            Text("Sesión activa. Las cajas se guardarán en tu cuenta.")
            Button(
                onClick = {
                    authRepository.logout()
                    successMessage = null
                    errorMessage = null
                    onSessionChanged(false)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                ),
            ) {
                Text("Cerrar sesión")
            }
        } else {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    onClick = {
                        runAuth {
                            authRepository.login(email.trim(), password)
                        }
                    },
                    enabled = canSubmit,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Iniciar sesión")
                }
                Button(
                    onClick = {
                        runAuth {
                            authRepository.register(email.trim(), password)
                        }
                    },
                    enabled = canSubmit,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Registrarse")
                }
            }
            Text(
                text = "La contraseña debe tener al menos 8 caracteres.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (isLoading) {
            CircularProgressIndicator()
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
            )
        }

        successMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = {
                errorMessage = null
                successMessage = null
            },
            enabled = errorMessage != null || successMessage != null,
        ) {
            Text("Limpiar mensajes")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthScreenPreview() {
    AuthScreen(
        authRepository = AuthRepository(FakeTokenStorage()),
        isAuthenticated = false,
        onSessionChanged = {},
    )
}

private class FakeTokenStorage : TokenStorage {
    private var token: String? = null

    override fun saveToken(token: String) {
        this.token = token
    }

    override fun getToken(): String? = token

    override fun clearToken() {
        token = null
    }
}
