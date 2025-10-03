package com.hirlu.boxvista.services

import android.util.Log
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

    override suspend fun getBoxes(): List<Box> {
        Log.e("BoxService", "getBoxes() iniciado")
        return try {
            Log.e("BoxService", "getBoxes() - Llamando a NetworkManager.fetchBoxes()")
            val result = NetworkManager.fetchBoxes()

            Log.e("BoxService", "getBoxes() - SUCCESS: Recibidas ${result?.size ?: "null"} boxes")
            Log.e("BoxService", "getBoxes() - Resultado: $result")
            result
        } catch (e: Exception) {
            Log.e("BoxService", "getBoxes() - ERROR: ${e::class.simpleName}: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getBox(id: Long): Box {
        return NetworkManager.fetchBox(id)
    }

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

    override suspend fun updateBox(box: Box): Box {
        return NetworkManager.updateBox(box.id!!, box)
    }
}
