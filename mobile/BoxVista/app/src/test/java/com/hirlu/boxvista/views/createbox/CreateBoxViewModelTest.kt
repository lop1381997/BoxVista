package com.hirlu.boxvista.views.createbox

import com.hirlu.boxvista.models.Box
import com.hirlu.boxvista.models.ObjectItem
import com.hirlu.boxvista.services.BoxServiceProtocol
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CreateBoxViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        kotlinx.coroutines.Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    @Test
    fun `createBox with empty type sets validation error`() = runTest {
        val vm = CreateBoxViewModel(FakeBoxService())

        vm.createBox()

        assertEquals("Selecciona un tipo de caja", vm.state.value.error)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `createBox success exposes created id`() = runTest {
        val vm = CreateBoxViewModel(FakeBoxService(createdId = 42L))

        vm.onTypeSelected("Herramientas")
        vm.createBox()
        advanceUntilIdle()

        assertEquals(42L, vm.state.value.createdBoxId)
        assertFalse(vm.state.value.isLoading)
        assertEquals(null, vm.state.value.error)
    }

    @Test
    fun `createBox failure exposes backend error`() = runTest {
        val vm = CreateBoxViewModel(FakeBoxService(shouldFail = true))

        vm.onTypeSelected("Electrónica")
        vm.createBox()
        advanceUntilIdle()

        assertEquals("boom", vm.state.value.error)
        assertFalse(vm.state.value.isLoading)
        assertEquals(null, vm.state.value.createdBoxId)
    }

    @Test
    fun `createBox network failure maps to friendly message`() = runTest {
        val vm = CreateBoxViewModel(FakeBoxService(errorToThrow = IOException("timeout")))

        vm.onTypeSelected("Electrónica")
        vm.createBox()
        advanceUntilIdle()

        assertEquals("Sin conexión. Revisa tu red e inténtalo de nuevo.", vm.state.value.error)
        assertFalse(vm.state.value.isLoading)
        assertEquals(null, vm.state.value.createdBoxId)
    }

    @Test
    fun `dismissCreatedDialog clears created id`() = runTest {
        val vm = CreateBoxViewModel(FakeBoxService(createdId = 10L))

        vm.onTypeSelected("Documentos")
        vm.createBox()
        advanceUntilIdle()
        assertEquals(10L, vm.state.value.createdBoxId)

        vm.dismissCreatedDialog()

        assertEquals(null, vm.state.value.createdBoxId)
    }

    private class FakeBoxService(
        private val createdId: Long = 1L,
        private val shouldFail: Boolean = false,
        private val errorToThrow: Throwable? = null
    ) : BoxServiceProtocol {
        override suspend fun getBoxes(): List<Box> = emptyList()

        override suspend fun getBox(id: Long): Box = Box(id = id, name = "", description = "", objects = mutableListOf())

        override suspend fun createBox(name: String, description: String, objects: List<ObjectItem>): Box {
            errorToThrow?.let { throw it }
            if (shouldFail) throw IllegalStateException("boom")
            return Box(id = createdId, name = name, description = description, objects = objects.toMutableList())
        }

        override suspend fun deleteBox(box: Box) = Unit

        override suspend fun updateBox(box: Box): Box = box
    }
}
