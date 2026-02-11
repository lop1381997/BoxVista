package com.hirlu.boxvista.views.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hirlu.boxvista.models.Box
import com.hirlu.boxvista.models.ObjectItem
import com.hirlu.boxvista.services.BoxService
import com.hirlu.boxvista.services.BoxServiceProtocol
import com.hirlu.boxvista.services.ObjectService
import com.hirlu.boxvista.services.ObjectServiceProtocol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val boxService: BoxServiceProtocol = BoxService(),
    private val objectService: ObjectServiceProtocol = ObjectService()
) : ViewModel() {

    private val _state = MutableStateFlow(HomeViewState())
    val state: StateFlow<HomeViewState> = _state.asStateFlow()

    fun loadBoxes() {
        if (_state.value.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            runCatching { boxService.getBoxes() }
                .onSuccess { boxes ->
                    _state.update { current ->
                        current.copy(
                            boxes = boxes,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { current ->
                        current.copy(
                            isLoading = false,
                            error = e.message ?: "Unknown error"
                        )
                    }
                }
        }
    }

    fun createBox(name: String, description: String, objects: List<ObjectItem>, onSuccess: () -> Unit = {}) {
        if (_state.value.isSaving) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, actionError = null) }
            runCatching {
                boxService.createBox(name = name, description = description, objects = objects)
            }.onSuccess {
                _state.update { it.copy(isSaving = false, actionError = null) }
                loadBoxes()
                onSuccess()
            }.onFailure { e ->
                _state.update { it.copy(isSaving = false, actionError = e.message ?: "No se pudo crear la caja") }
            }
        }
    }

    fun updateBox(box: Box, onSuccess: () -> Unit = {}) {
        if (_state.value.isSaving) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, actionError = null) }
            runCatching { boxService.updateBox(box) }
                .onSuccess { updated ->
                    _state.update { current ->
                        current.copy(
                            isSaving = false,
                            actionError = null,
                            boxes = current.boxes.map { existing -> if (existing.id == updated.id) updated else existing }
                        )
                    }
                    onSuccess()
                }
                .onFailure { e ->
                    _state.update { it.copy(isSaving = false, actionError = e.message ?: "No se pudo actualizar la caja") }
                }
        }
    }

    fun deleteBox(box: Box, onSuccess: () -> Unit = {}) {
        if (_state.value.isSaving) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, actionError = null) }
            runCatching { boxService.deleteBox(box) }
                .onSuccess {
                    _state.update { current ->
                        current.copy(
                            isSaving = false,
                            actionError = null,
                            boxes = current.boxes.filterNot { it.id == box.id }
                        )
                    }
                    onSuccess()
                }
                .onFailure { e ->
                    _state.update { it.copy(isSaving = false, actionError = e.message ?: "No se pudo borrar la caja") }
                }
        }
    }

    fun updateObjectState(boxId: Long, objectItem: ObjectItem, newState: Boolean) {
        viewModelScope.launch {
            val previous = objectItem.state
            val updatedObject = objectItem.copy(state = newState)

            _state.update { current ->
                current.copy(
                    boxes = current.boxes.map { box ->
                        if (box.id == boxId) {
                            box.copy(
                                objects = box.objects.map {
                                    if (it.id == objectItem.id) it.copy(state = newState) else it
                                }.toMutableList()
                            )
                        } else {
                            box
                        }
                    }
                )
            }

            runCatching { objectService.updateObject(updatedObject, boxId.toInt()) }
                .onFailure { e ->
                    _state.update { current ->
                        current.copy(
                            actionError = e.message ?: "No se pudo actualizar el objeto",
                            boxes = current.boxes.map { box ->
                                if (box.id == boxId) {
                                    box.copy(
                                        objects = box.objects.map {
                                            if (it.id == objectItem.id) it.copy(state = previous) else it
                                        }.toMutableList()
                                    )
                                } else {
                                    box
                                }
                            }
                        )
                    }
                }
        }
    }


    fun setActionError(message: String) {
        _state.update { it.copy(actionError = message) }
    }

    fun clearActionError() {
        _state.update { it.copy(actionError = null) }
    }

    fun retry() = loadBoxes()
}
