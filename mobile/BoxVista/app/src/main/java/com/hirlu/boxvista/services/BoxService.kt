package com.hirlu.boxvista.services

import com.hirlu.boxvista.Models.Box
import com.hirlu.boxvista.Models.ObjectItem
import com.hirlu.boxvista.NetworkManager

//
interface BoxServiceProtocol {
     var networkingManager : NetworkManager
    fun init(baseUrl: String)

    suspend fun getBoxes(): List<Box>
    suspend fun getBox(id: Long): Box
    suspend fun createBox(name: String, description: String, objects: List<ObjectItem>): Box
    suspend fun deleteBox(box: Box)
    suspend fun updateBox(box: Box): Box
}

class BoxService(override var networkingManager: NetworkManager = NetworkManager) : BoxServiceProtocol {


    override fun init(baseUrl: String) {
        NetworkManager.init(baseUrl)
        networkingManager = NetworkManager
    }

    private fun requireNM(): NetworkManager = requireNotNull(networkingManager) {
        "NetworkManager no inicializado."
    }


    override suspend fun getBoxes(): List<Box> {
        return requireNM().fetchBoxes()

    }

    override suspend fun getBox(id: Long): Box {
        return requireNM().fetchBox(id)
    }

    override suspend fun createBox(
        name: String,
        description: String,
        objects: List<ObjectItem>
    ): Box {
        val box: Box = Box(
            name = name, description = description, objects = objects.map{ it }.toMutableList()
        )
       return requireNM().createBox(box)
    }



    override suspend fun deleteBox(box: Box) {
        requireNM().deleteBox(box.id!!)
    }

    override suspend fun updateBox(box: Box): Box {
        return requireNM().updateBox(
            box.id!!,
            Box(
                id = box.id,
                name = box.name,
                description = box.description,
                objects = box.objects
            )
        )
    }

}

