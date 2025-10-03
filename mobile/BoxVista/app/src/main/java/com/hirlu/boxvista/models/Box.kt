package com.hirlu.boxvista.models

import com.google.gson.annotations.SerializedName

data class Box(val id: Long? =null, val name: String, val description: String, val objects: MutableList<out ObjectItem>)

data class BoxDTO(
    val id:Long? = null,
    val name: String,
    val description:String,
    @SerializedName("objetos")
    val objetos: MutableList<ObjectItemDTO>?
){
    fun toBox(): Box {
        return Box(
            id = id,
            name = name,
            description = description,
            objects = objetos?.map { it.toObject() }?.toMutableList() ?: mutableListOf())
    }
}
