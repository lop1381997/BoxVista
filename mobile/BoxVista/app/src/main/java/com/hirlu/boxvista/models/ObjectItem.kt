package com.hirlu.boxvista.models
import com.google.gson.annotations.SerializedName

data class ObjectItem(val id: Long, val name: String, val state: Boolean, val boxId: Long )

data class ObjectItemDTO(
    val id: Long,
    @SerializedName("nombre")
    val nombre: String,
    val state: Boolean,
    val boxId: Long
){
    fun toObject(): ObjectItem {
        return ObjectItem(
            id = id,
            name = nombre,
            state = state,
            boxId = boxId)
    }

}