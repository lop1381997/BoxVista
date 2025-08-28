package com.hirlu.boxvista.Models

data class Box(val id:Long? =null, val name: String, val description:String, val objects: MutableList<ObjectItem>)

data class BoxDTO(val id:Long? = null, val name: String, val description:String, val objects: MutableList<ObjectItemDTO>){
    fun toBox(): Box {
        return Box(id = id,
            name = name,
            description = description,
            objects = objects.map { it.toObject() }.toMutableList())
    }
}
