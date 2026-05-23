//
//  SettingsView.swift
//  BoxVistaIOS
//
//  Created by pol linger on 6/6/25.
//

import SwiftUI

struct SettingsView: View {
    @State private var email = ""
    @State private var password = ""
    @State private var isLoading = false
    @State private var isAuthenticated = AuthTokenStore.shared.isAuthenticated
    @State private var statusMessage: StatusMessage?

    var body: some View {
        NavigationStack {
            Form {
                Section("Cuenta") {
                    TextField("Email", text: $email)
                        .keyboardType(.emailAddress)
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled()

                    SecureField("Contraseña", text: $password)
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled()

                    VStack(spacing: 10) {
                        Button {
                            Task { await submitAuth(.login) }
                        } label: {
                            authButtonLabel("Iniciar sesión", systemImage: "person.crop.circle")
                        }
                        .buttonStyle(.borderedProminent)

                        Button {
                            Task { await submitAuth(.register) }
                        } label: {
                            authButtonLabel("Registrarse", systemImage: "person.badge.plus")
                        }
                        .buttonStyle(.bordered)
                    }
                    .disabled(isLoading || !canSubmit)

                    if isLoading {
                        ProgressView("Conectando...")
                    }
                }

                Section("Sesión") {
                    HStack {
                        Label(
                            isAuthenticated ? "Sesión activa" : "Sin sesión",
                            systemImage: isAuthenticated ? "checkmark.circle.fill" : "person.crop.circle.badge.exclamationmark"
                        )
                        Spacer()
                    }

                    if isAuthenticated {
                        Button("Cerrar sesión", role: .destructive) {
                            logout()
                        }
                    }
                }

                if let statusMessage {
                    Section {
                        Label(statusMessage.text, systemImage: statusMessage.systemImage)
                            .foregroundStyle(statusMessage.isError ? .red : .green)
                    }
                }
            }
            .navigationTitle("Settings")
            .onAppear {
                isAuthenticated = AuthTokenStore.shared.isAuthenticated
            }
        }
    }

    private var trimmedEmail: String {
        email.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    private var trimmedPassword: String {
        password.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    private var canSubmit: Bool {
        !trimmedEmail.isEmpty && trimmedPassword.count >= 8
    }

    @MainActor
    @ViewBuilder
    private func authButtonLabel(_ title: String, systemImage: String) -> some View {
        Label(title, systemImage: systemImage)
            .frame(maxWidth: .infinity)
    }

    @MainActor
    private func submitAuth(_ mode: AuthMode) async {
        guard canSubmit else {
            statusMessage = StatusMessage(
                text: "Introduce un email y una contraseña de al menos 8 caracteres.",
                isError: true
            )
            return
        }

        isLoading = true
        statusMessage = nil

        do {
            switch mode {
            case .login:
                _ = try await NetworkManager.shared.login(email: trimmedEmail, password: trimmedPassword)
            case .register:
                _ = try await NetworkManager.shared.register(email: trimmedEmail, password: trimmedPassword)
            }

            password = ""
            isAuthenticated = true
            statusMessage = StatusMessage(text: mode.successMessage, isError: false)
        } catch {
            statusMessage = StatusMessage(text: friendlyMessage(for: error), isError: true)
        }

        isLoading = false
    }

    private func logout() {
        AuthTokenStore.shared.clear()
        password = ""
        isAuthenticated = false
        statusMessage = StatusMessage(text: "Sesión cerrada.", isError: false)
    }

    private func friendlyMessage(for error: Error) -> String {
        guard let apiError = error as? APIError else {
            return error.localizedDescription
        }

        switch apiError {
        case .invalidURL:
            return "Error de configuración de la aplicación."
        case .invalidResponse:
            return "No se pudo contactar con el servidor."
        case .decodingError:
            return "No se pudo procesar la respuesta del servidor."
        case .unauthorized:
            return "Email o contraseña incorrectos."
        case .serverError(400, _):
            return "Revisa el email y que la contraseña tenga al menos 8 caracteres."
        case .serverError(409, _):
            return "Ese email ya está registrado."
        case .serverError(let code, let message):
            return message ?? "Error del servidor (código \(code))."
        }
    }
}

#Preview {
    SettingsView()
}

private enum AuthMode {
    case login
    case register

    var successMessage: String {
        switch self {
        case .login:
            return "Sesión iniciada."
        case .register:
            return "Cuenta creada y sesión iniciada."
        }
    }
}

private struct StatusMessage {
    let text: String
    let isError: Bool

    var systemImage: String {
        isError ? "exclamationmark.triangle.fill" : "checkmark.circle.fill"
    }
}
