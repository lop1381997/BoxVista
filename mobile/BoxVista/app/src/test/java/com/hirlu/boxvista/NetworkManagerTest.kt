package com.hirlu.boxvista

import com.hirlu.boxvista.models.Box
import com.hirlu.boxvista.models.ObjectItem
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class NetworkManagerTest {

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        NetworkManager.init(mockWebServer.url("/").toString())
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testFetchBoxes() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("[{\"id\":1,\"name\":\"Test Box\",\"description\":\"Test Description\",\"objetos\":[]}]")
        mockWebServer.enqueue(mockResponse)

        val result = NetworkManager.fetchBoxes()

        assertEquals(1, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("Test Box", result[0].name)
        assertEquals("Test Description", result[0].description)
        assertEquals(0, result[0].objects.size)
    }

    @Test
    fun testFetchBox() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":1,\"name\":\"Test Box\",\"description\":\"Test Description\",\"objetos\":[]}")
        mockWebServer.enqueue(mockResponse)

        val result = NetworkManager.fetchBox(1)

        assertEquals(1L, result.id)
        assertEquals("Test Box", result.name)
        assertEquals("Test Description", result.description)
        assertEquals(0, result.objects.size)
    }

    @Test
    fun testCreateBox() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(201)
            .setBody("{\"id\":1,\"name\":\"Test Box\",\"description\":\"Test Description\",\"objetos\":[]}")
        mockWebServer.enqueue(mockResponse)

        val result = NetworkManager.createBox(Box(name = "Test Box", description = "Test Description", objects = mutableListOf()))

        assertEquals(1L, result.id)
        assertEquals("Test Box", result.name)
        assertEquals("Test Description", result.description)
        assertEquals(0, result.objects.size)
    }

    @Test
    fun testDeleteBox() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(204)
        mockWebServer.enqueue(mockResponse)

        NetworkManager.deleteBox(1)

        // No need to assert anything here, as the method doesn't return anything.
        // We are just testing that the method doesn't throw an exception.
    }

    @Test
    fun testUpdateBox() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":1,\"name\":\"Test Box\",\"description\":\"Test Description\",\"objetos\":[]}")
        mockWebServer.enqueue(mockResponse)

        val result = NetworkManager.updateBox(1, Box(id = 1, name = "Test Box", description = "Test Description", objects = mutableListOf()))

        assertEquals(1L, result.id)
        assertEquals("Test Box", result.name)
        assertEquals("Test Description", result.description)
        assertEquals(0, result.objects.size)
    }

    @Test
    fun testFetchObjects() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("[{\"id\":1,\"nombre\":\"Test Object\",\"state\":true,\"boxId\":1}]")
        mockWebServer.enqueue(mockResponse)

        val result = NetworkManager.fetchObjects(1)

        assertEquals(1, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("Test Object", result[0].name)
        assertEquals(true, result[0].state)
        assertEquals(1L, result[0].boxId)
    }

    @Test
    fun testFetchObject() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":1,\"nombre\":\"Test Object\",\"state\":true,\"boxId\":1}")
        mockWebServer.enqueue(mockResponse)

        val result = NetworkManager.fetchObject(1, 1)

        assertEquals(1L, result.id)
        assertEquals("Test Object", result.name)
        assertEquals(true, result.state)
        assertEquals(1L, result.boxId)
    }

    @Test
    fun testCreateObject() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(201)
            .setBody("{\"id\":1,\"nombre\":\"Test Object\",\"state\":true,\"boxId\":1}")
        mockWebServer.enqueue(mockResponse)

        val result = NetworkManager.createObject(1, ObjectItem(id = 1, name = "Test Object", state = true, boxId = 1))

        assertEquals(1L, result.id)
        assertEquals("Test Object", result.name)
        assertEquals(true, result.state)
        assertEquals(1L, result.boxId)
    }

    @Test
    fun testUpdateObject() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"id\":1,\"nombre\":\"Test Object\",\"state\":true,\"boxId\":1}")
        mockWebServer.enqueue(mockResponse)

        val result = NetworkManager.updateObject(1, ObjectItem(id = 1, name = "Test Object", state = true, boxId = 1))

        assertEquals(1L, result.id)
        assertEquals("Test Object", result.name)
        assertEquals(true, result.state)
        assertEquals(1L, result.boxId)
    }

    @Test
    fun testDeleteObject() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(204)
        mockWebServer.enqueue(mockResponse)

        NetworkManager.deleteObject(1, 1)

        // No need to assert anything here, as the method doesn't return anything.
        // We are just testing that the method doesn't throw an exception.
    }
}