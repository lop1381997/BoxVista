package com.hirlu.boxvista.views.home

import com.hirlu.boxvista.Models.Box

data class HomeViewState(
    val boxes: List<Box> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isEmpty: Boolean get() = boxes.isEmpty() && !isLoading && error == null
}
