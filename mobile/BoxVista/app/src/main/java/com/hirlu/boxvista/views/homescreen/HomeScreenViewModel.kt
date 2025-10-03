package com.hirlu.boxvista.views.homescreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hirlu.boxvista.services.BoxService
import com.hirlu.boxvista.services.BoxServiceProtocol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val boxService: BoxServiceProtocol = BoxService()
) : ViewModel() {

    private val _state = MutableStateFlow(HomeViewState())
    val state: StateFlow<HomeViewState> = _state.asStateFlow()

    /** Carga inicial / refresh */
    fun loadBoxes() {
        Log.e("HomeScreenViewModel", "loadBoxes() iniciado")

        // Evita dobles cargas si ya está cargando
        if (_state.value.isLoading) {
            Log.e("HomeScreenViewModel", "loadBoxes() - Ya está cargando, cancelando")
            return
        }

        Log.e("HomeScreenViewModel", "loadBoxes() - Iniciando corrutina")
        viewModelScope.launch {
            try {
                Log.e("HomeScreenViewModel", "loadBoxes() - Actualizando estado a loading")
                _state.update { it.copy(isLoading = true, error = null) }

                Log.e("HomeScreenViewModel", "loadBoxes() - Llamando a boxService.getBoxes()")
                runCatching { boxService.getBoxes() }
                    .onSuccess { boxes ->
                        Log.e("HomeScreenViewModel", "loadBoxes() - SUCCESS: Boxes recibidas: ${boxes?.size ?: "null"}")
                        Log.e("HomeScreenViewModel", "loadBoxes() - Boxes content: $boxes")
                        _state.update {
                            it.copy(
                                boxes = boxes ?: emptyList(),
                                isLoading = false,
                                error = null
                            )
                        }
                        Log.e("HomeScreenViewModel", "loadBoxes() - Estado actualizado exitosamente")
                    }
                    .onFailure { e ->
                        Log.e("HomeScreenViewModel", "loadBoxes() - FAILURE: ${e::class.simpleName}: ${e.message}")
                        Log.e("HomeScreenViewModel", "loadBoxes() - Stack trace:", e)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = e.message ?: "Unknown error"
                            )
                        }
                    }
            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "loadBoxes() - Exception fuera de runCatching: ${e::class.simpleName}: ${e.message}", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Error inesperado: ${e.message}"
                    )
                }
            }
        }
        Log.e("HomeScreenViewModel", "loadBoxes() - Función terminada")
    }


    /** Intent: retry tras error */
    fun retry() = loadBoxes()
}
