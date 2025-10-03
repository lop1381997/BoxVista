package com.hirlu.boxvista.services

import android.util.Log
import com.hirlu.boxvista.NetworkManager
import com.hirlu.boxvista.models.ObjectItem

interface ObjectServiceProtocol {
    suspend fun getObjects(boxID: Long): List<ObjectItem>
    suspend fun getObject(boxid: Long, objectID: Long): ObjectItem?
    suspend fun updateObject(objectItem: ObjectItem, boxID: Int ): ObjectItem?

}
class ObjectService : ObjectServiceProtocol {
    override suspend fun getObjects(boxID: Long): List<ObjectItem> {
        Log.d("ObjectService", "getObjects() called" )
        return try {
            Log.d("ObjectService", "Fetching objects...")
            val result = NetworkManager.fetchObjects(
                boxId = boxID
            )
            Log.d("ObjectService", "Fetched objects: $result")
            result
        }
        catch (e: Exception){
            Log.e("ObjectService", "Error fetching objects: ${e.message}", e)
            throw  e
        }
    }

    override suspend fun getObject(boxid: Long, objectID: Long): ObjectItem? {
        Log.d("ObjectService", "getObject() called")
        return try {
            Log.d("ObjectService", "Fetching object...")
            val result = NetworkManager.fetchObject(
                boxId = boxid,
                id = objectID
            )
            Log.d("ObjectService", "Fetched object: $result")
            result
        }
        catch (e: Exception){
            Log.e("ObjectService", "Error fetching object: ${e.message}", e)
            throw  e
        }
    }



    override suspend fun updateObject(
        objectItem: ObjectItem, boxID: Int
    ): ObjectItem? {
        Log.d("ObjectService", "updateObject() called")
        return try{
            Log.d("ObjectService", "Updating object...")
            val result = NetworkManager.updateObject(
                boxId = boxID,
                obj = objectItem
            )
            Log.d("ObjectService", "Updated object: $result")
            result
        }
        catch (e : Exception){
            Log.e("ObjectService", "Error updating object: ${e.message}", e)
            throw e
        }

    }

}