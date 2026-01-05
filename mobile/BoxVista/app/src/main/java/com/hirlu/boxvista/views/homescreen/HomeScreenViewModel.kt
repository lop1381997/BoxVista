package com.hirlu.boxvista.views.homescreen

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
        // Avoid duplicate loads if already loading
        if (_state.value.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            runCatching { boxService.getBoxes() }
                .onSuccess { boxes ->
                    _state.update {
                        it.copy(
                            boxes = boxes,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Unknown error"
                        )
                    }
                }
        }
    }


    /** Intent: retry tras error */
    fun retry() = loadBoxes()
}
