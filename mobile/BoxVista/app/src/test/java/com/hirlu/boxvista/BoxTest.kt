package com.hirlu.boxvista

import com.hirlu.boxvista.models.Box
import com.hirlu.boxvista.models.BoxDTO
import com.hirlu.boxvista.models.ObjectItem
import com.hirlu.boxvista.models.ObjectItemDTO
import org.junit.Assert.assertEquals
import org.junit.Test

class BoxTest {

    @Test
    fun testToBox() {
        val objectItemDTO = ObjectItemDTO(id = 1, nombre = "Test Object", state = true, boxId = 1)
        val boxDTO = BoxDTO(id = 1, name = "Test Box", description = "Test Description", objetos = mutableListOf(objectItemDTO))

        val box = boxDTO.toBox()

        assertEquals(boxDTO.id, box.id)
        assertEquals(boxDTO.name, box.name)
        assertEquals(boxDTO.description, box.description)
        assertEquals(1, box.objects.size)
        assertEquals(objectItemDTO.id, box.objects[0].id)
        assertEquals(objectItemDTO.nombre, box.objects[0].name)
        assertEquals(objectItemDTO.state, box.objects[0].state)
        assertEquals(objectItemDTO.boxId, box.objects[0].boxId)
    }
}