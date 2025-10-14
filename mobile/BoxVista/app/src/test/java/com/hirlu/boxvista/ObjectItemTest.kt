package com.hirlu.boxvista

import com.hirlu.boxvista.models.ObjectItemDTO
import org.junit.Assert.assertEquals
import org.junit.Test

class ObjectItemTest {

    @Test
    fun testToObject() {
        val objectItemDTO = ObjectItemDTO(id = 1, nombre = "Test Object", state = true, boxId = 1)

        val objectItem = objectItemDTO.toObject()

        assertEquals(objectItemDTO.id, objectItem.id)
        assertEquals(objectItemDTO.nombre, objectItem.name)
        assertEquals(objectItemDTO.state, objectItem.state)
        assertEquals(objectItemDTO.boxId, objectItem.boxId)
    }
}