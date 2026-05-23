package com.hirlu.boxvista.views.addbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hirlu.boxvista.services.BoxService
import com.hirlu.boxvista.models.ObjectItem
import com.hirlu.boxvista.services.BoxServiceProtocol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddBoxViewModel(
	private val boxService: BoxServiceProtocol = BoxService()
) : ViewModel() {

	private val _state = MutableStateFlow(AddBoxViewState())
	val state: StateFlow<AddBoxViewState> = _state.asStateFlow()

	fun onNameChange(value: String) {
		_state.update { it.copy(name = value) }
	}

	fun descriptionChange(value: String) {
		_state.update { it.copy(description = value) }
	}

	fun addObject(item: ObjectItem) {
		if (item.name.isBlank()) return
		_state.update { current ->
			current.copy(objects = current.objects + item.copy(name = item.name.trim()), error = null)
		}
	}

	fun updateObjectState(index: Int, isActive: Boolean) {
		_state.update { current ->
			current.copy(
				objects = current.objects.mapIndexed { objectIndex, objectItem ->
					if (objectIndex == index) objectItem.copy(state = isActive) else objectItem
				}
			)
		}
	}

	fun createNewBox(onSuccess: () -> Unit = {}) {
		val current = _state.value
		if (current.name.isBlank()) {
			_state.update { it.copy(error = "El nombre es obligatorio") }
			return
		}

		if (current.objects.none { it.state }) {
			_state.update { it.copy(error = "Debe haber al menos un objeto activo") }
			return
		}

		viewModelScope.launch {
			_state.update { it.copy(isSaving = true, error = null) }
			runCatching {
				boxService.createBox(
					name = current.name.trim(),
					description = current.description.trim(),
					objects = current.objects
				)
			}.onSuccess {
				_state.update { AddBoxViewState() }
				onSuccess()
			}.onFailure { error ->
				_state.update {
					it.copy(
						isSaving = false,
						error = error.message ?: "No se pudo crear la caja"
					)
				}
			}
		}
	}
}
