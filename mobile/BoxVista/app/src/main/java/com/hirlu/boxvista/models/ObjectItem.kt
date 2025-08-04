package com.hirlu.boxvista.Models
import kotlinx.serialization.Serializable

data class ObjectItem(val id: Long, val nombre: String, val state: Boolean, val boxId: Long )

@Serializable
data class ObjectItemDTO(val id: Long, val nombre: String, val state: Boolean, val boxId: Long ){
    fun toObject(): ObjectItem {
        return ObjectItem(id = id,
            nombre = nombre,
            state = state,
            boxId = boxId)
    }

}