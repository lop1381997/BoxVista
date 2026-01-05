package com.hirlu.boxvista.services

import com.hirlu.boxvista.NetworkManager
import com.hirlu.boxvista.models.Box
import com.hirlu.boxvista.models.ObjectItem

//
interface BoxServiceProtocol {
    suspend fun getBoxes(): List<Box>
    suspend fun getBox(id: Long): Box
    suspend fun createBox(name: String, description: String, objects: List<ObjectItem>): Box
    suspend fun deleteBox(box: Box)
    suspend fun updateBox(box: Box): Box
}

class BoxService : BoxServiceProtocol {

    override suspend fun getBoxes(): List<Box> = NetworkManager.fetchBoxes()

    override suspend fun getBox(id: Long): Box = NetworkManager.fetchBox(id)

    override suspend fun createBox(
        name: String,
        description: String,
        objects: List<ObjectItem>
    ): Box {
        val box = Box(
            name = name,
            description = description,
            objects = objects.toMutableList()
        )
        return NetworkManager.createBox(box)
    }

    override suspend fun deleteBox(box: Box) {
        NetworkManager.deleteBox(box.id!!)
    }

    override suspend fun updateBox(box: Box): Box = NetworkManager.updateBox(box.id!!, box)
}
