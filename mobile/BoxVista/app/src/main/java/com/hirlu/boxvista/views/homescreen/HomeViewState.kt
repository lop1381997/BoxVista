package com.hirlu.boxvista.views.homescreen

import com.hirlu.boxvista.models.Box

data class HomeViewState(
    val boxes: List<Box> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isEmpty: Boolean get() = boxes.isEmpty() && !isLoading && error == null
}
