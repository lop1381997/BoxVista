package com.hirlu.boxvista.services

//import com.hirlu.boxvista.Models.Box
//import com.hirlu.boxvista.Models.BoxDTO
//import com.hirlu.boxvista.Models.ObjectItem
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import kotlinx.serialization.json.Json
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody
//import okhttp3.RequestBody.Companion.toRequestBody
//import java.io.IOException
//
//interface BoxServiceProtocol {
//    val baseURL: String
//
//    suspend fun getBoxes(): List<Box>
//    suspend fun getBox(id: Long): Box
//    suspend fun createBox(name: String, description: String, objects: List<ObjectItem>): Box
//    suspend fun deleteBox(box: Box)
//    suspend fun updateBox(box: Box): Box
//}
//
//class BoxService : BoxServiceProtocol {
//    override val baseURL: String = "http://localhost:3000/api"
//
//    private val client = OkHttpClient()
//
//    override suspend fun getBoxes(): List<Box> {
//        return withContext(Dispatchers.IO) {
//            val request = Request.Builder()
//                .url("$baseURL/boxes")
//                .build()
//            val response = client.newCall(request).execute()
//
//            if (!response.isSuccessful) {
//                throw IOException("Unexpected code $response")
//            }
//
//            val body = response.body?.string() ?: throw IOException("Empty response body")
//            val boxes = Json.decodeFromString<List<BoxDTO>>(body)
//            boxes.map { it.toBox() }
//        }
//    }
//
//    override suspend fun getBox(id: Long): Box {
//        return withContext(Dispatchers.IO) {
//            val request = Request.Builder()
//                .url("$baseURL/boxes/$id")
//                .build()
//            val response = client.newCall(request).execute()
//
//            if (!response.isSuccessful) {
//                throw IOException("Unexpected code $response")
//            }
//
//            val body = response.body?.string() ?: throw IOException("Empty response body")
//            val dto = Json.decodeFromString<BoxDTO>(body)
//            dto.toBox()
//        }
//    }
//
//    override suspend fun createBox(name: String, description: String, objects: List<ObjectItem>): Box {
//        return withContext(Dispatchers.IO) {
//            val payload = mapOf(
//                "name" to name,
//                "description" to description,
//                "objetos" to objects.map { mapOf("nombre" to it.nombre, "state" to it.state) }
//            )
//            val requestBody = payload.toJsonRequestBody()
//            val request = Request.Builder()
//                .url("$baseURL/boxes")
//                .post(requestBody)
//                .build()
//            val response = client.newCall(request).execute()
//
//            if (!response.isSuccessful) {
//                throw IOException("Unexpected code $response")
//            }
//
//            val body = response.body?.string() ?: throw IOException("Empty response body")
//            val dto = Json.decodeFromString<BoxDTO>(body)
//            dto.toBox()
//        }
//    }
//
//    override suspend fun deleteBox(box: Box) {
//        return withContext(Dispatchers.IO) {
//            val request = Request.Builder()
//                .url("$baseURL/boxes/${box.id}")
//                .delete()
//                .build()
//            val response = client.newCall(request).execute()
//
//            if (!response.isSuccessful) {
//                throw IOException("Unexpected code $response")
//            }
//        }
//    }
//
//    override suspend fun updateBox(box: Box): Box {
//        return withContext(Dispatchers.IO) {
//            val payload = mapOf(
//                "name" to box.name,
//                "description" to box.description,
//                "objetos" to box.objects.map { mapOf("nombre" to it.nombre, "state" to it.state) }
//            )
//            val requestBody = payload.toJsonRequestBody()
//            val request = Request.Builder()
//                .url("$baseURL/boxes/${box.id}")
//                .put(requestBody)
//                .build()
//            val response = client.newCall(request).execute()
//
//            if (!response.isSuccessful) {
//                throw IOException("Unexpected code $response")
//            }
//
//            val body = response.body?.string() ?: throw IOException("Empty response body")
//            val dto = Json.decodeFromString<BoxDTO>(body)
//            dto.toBox()
//        }
//    }
//
//    private fun Map<String, Any>.toJsonRequestBody(): RequestBody {
//        val json = Json.encodeToString(this)
//        return json.toRequestBody("application/json".toMediaType())
//    }
//}

