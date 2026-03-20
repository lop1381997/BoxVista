package com.hirlu.boxvista.views.createbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hirlu.boxvista.services.BoxService
import com.hirlu.boxvista.services.BoxServiceProtocol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class CreateBoxState(
    val selectedType: String = "",
    val isLoading: Boolean = false,
    val createdBoxId: Long? = null,
    val error: String? = null
)

class CreateBoxViewModel(
    private val boxService: BoxServiceProtocol = BoxService()
) : ViewModel() {

    private val _state = MutableStateFlow(CreateBoxState())
    val state: StateFlow<CreateBoxState> = _state.asStateFlow()

    fun onTypeSelected(type: String) {
        _state.update { it.copy(selectedType = type, error = null) }
    }

    fun createBox() {
        val type = _state.value.selectedType
        if (type.isBlank() || _state.value.isLoading) {
            if (type.isBlank()) {
                _state.update { it.copy(error = "Selecciona un tipo de caja") }
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, createdBoxId = null) }
            runCatching {
                boxService.createBox(
                    name = type,
                    description = "Caja tipo $type",
                    objects = emptyList()
                )
            }.onSuccess { created ->
                _state.update { it.copy(isLoading = false, createdBoxId = created.id, error = null) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = toUserMessage(e)) }
            }
        }
    }

    private fun toUserMessage(error: Throwable): String {
        return when (error) {
            is IOException -> "Sin conexión. Revisa tu red e inténtalo de nuevo."
            is HttpException -> when (error.code()) {
                400 -> "Datos inválidos. Revisa el tipo de caja."
                401, 403 -> "Tu sesión no tiene permisos para crear cajas."
                409 -> "La caja ya existe."
                else -> "Error del servidor (${error.code()}). Inténtalo de nuevo."
            }
            else -> error.message ?: "No se pudo crear la caja"
        }
    }

    fun dismissCreatedDialog() {
        _state.update { it.copy(createdBoxId = null) }
    }
}
