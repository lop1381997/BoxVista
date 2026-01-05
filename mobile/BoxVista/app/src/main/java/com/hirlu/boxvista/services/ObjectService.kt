package com.hirlu.boxvista.services

import com.hirlu.boxvista.NetworkManager
import com.hirlu.boxvista.models.ObjectItem

interface ObjectServiceProtocol {
    suspend fun getObjects(boxID: Long): List<ObjectItem>
    suspend fun getObject(boxid: Long, objectID: Long): ObjectItem?
    suspend fun updateObject(objectItem: ObjectItem, boxID: Int): ObjectItem?
}

class ObjectService : ObjectServiceProtocol {
    override suspend fun getObjects(boxID: Long): List<ObjectItem> =
        NetworkManager.fetchObjects(boxId = boxID)

    override suspend fun getObject(boxid: Long, objectID: Long): ObjectItem? =
        NetworkManager.fetchObject(boxId = boxid, id = objectID)

    override suspend fun updateObject(objectItem: ObjectItem, boxID: Int): ObjectItem? =
        NetworkManager.updateObject(boxId = boxID, obj = objectItem)
}