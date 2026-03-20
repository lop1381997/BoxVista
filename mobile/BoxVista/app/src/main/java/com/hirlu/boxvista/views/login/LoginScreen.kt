package com.hirlu.boxvista.views.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState

@Composable
fun LoginScreen(
    viewModel: LoginViewModel
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadExistingSession()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Iniciar sesión")

        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            singleLine = true,
            enabled = !state.isLoading && !state.isLoggedIn
        )

        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            enabled = !state.isLoading && !state.isLoggedIn
        )

        if (state.error != null) {
            Text("Error: ${state.error}")
        }

        if (state.isLoggedIn) {
            Text("Sesión iniciada")
            state.tokenPreview?.let { Text("Token guardado: $it") }
            Button(onClick = viewModel::logout) {
                Text("Cerrar sesión")
            }
        } else {
            Button(
                onClick = viewModel::login,
                enabled = !state.isLoading
            ) {
                Text(if (state.isLoading) "Entrando..." else "Entrar")
            }
        }
    }
}
