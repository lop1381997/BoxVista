package com.hirlu.boxvista.Models
import kotlinx.serialization.Serializable

data class ObjectItem(val id: Long, val name: String, val state: Boolean, val boxId: Long )

@Serializable
data class ObjectItemDTO(val id: Long, val nombre: String, val state: Boolean, val boxId: Long ){
    fun toObject(): ObjectItem {
        return ObjectItem(id = id,
            name = nombre,
            state = state,
            boxId = boxId)
    }

}