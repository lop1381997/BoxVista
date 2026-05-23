package com.hirlu.boxvista.views.addbox

import com.hirlu.boxvista.models.ObjectItem

data class AddBoxViewState(
	val name: String = "",
	val description: String = "",
	val objects: List<ObjectItem> = emptyList(),
	val isSaving: Boolean = false,
	val error: String? = null
)


