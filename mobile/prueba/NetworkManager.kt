package com.hirlu.boxvista

import com.hirlu.boxvista.Models.Box
import com.hirlu.boxvista.Models.BoxDTO
import com.hirlu.boxvista.Models.ObjectItem
import com.hirlu.boxvista.Models.ObjectItemDTO
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

sealed class APIError : Exception() {
    object InvalidURL : APIError()
    object InvalidResponse : APIError()
    object DecodingError : APIError()
    class ServerError(val code: Int) : APIError()
}

class NetworkManager private constructor() {
    private val baseURL = "http://localhost:3000/api"
    private val client = OkHttpClient()
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }

    companion object {
        val shared = NetworkManager()
    }

    fun fetchBoxes(callback: (Result<List<Box>>) -> Unit) {
        val request = Request.Builder()
            .url("$baseURL/boxes")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(APIError.InvalidResponse))
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback(Result.failure(APIError.ServerError(response.code)))
                    return
                }

                val body = response.body?.string() ?: return callback(Result.failure(APIError.InvalidResponse))
                try {
                    val dtos = json.decodeFromString<List<BoxDTO>>(body)
                    val boxes = dtos.map { it.toBox() }
                    callback(Result.success(boxes))
                } catch (e: Exception) {
                    callback(Result.failure(APIError.DecodingError))
                }
            }
        })
    }

    fun fetchBox(id: Long, callback: (Result<Box>) -> Unit) {
        val request = Request.Builder()
            .url("$baseURL/boxes/$id")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(APIError.InvalidResponse))
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback(Result.failure(APIError.ServerError(response.code)))
                    return
                }

                val body = response.body?.string() ?: return callback(Result.failure(APIError.InvalidResponse))
                try {
                    val dto = json.decodeFromString<BoxDTO>(body)
                    callback(Result.success(dto.toBox()))
                } catch (e: Exception) {
                    callback(Result.failure(APIError.DecodingError))
                }
            }
        })
    }

    fun createBox(name: String, description: String, objects: List<ObjectItem>, callback: (Result<Box>) -> Unit) {
        val payload = mapOf(
            "name" to name,
            "description" to description,
            "objetos" to objects.map { mapOf("nombre" to it.nombre, "state" to it.state) }
        )
        val requestBody = json.encodeToString(payload).toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$baseURL/boxes")
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(jsonBoxCallback(callback))
    }

    fun updateBox(box: Box, callback: (Result<Box>) -> Unit) {
        val payload = mapOf(
            "name" to box.name,
            "description" to box.description,
            "objetos" to box.objects.map { mapOf("nombre" to it.nombre, "state" to it.state) }
        )
        val requestBody = json.encodeToString(payload).toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$baseURL/boxes/${box.id}")
            .put(requestBody)
            .build()
        client.newCall(request).enqueue(jsonBoxCallback(callback))
    }

    fun deleteBox(id: Long, callback: (Result<Unit>) -> Unit) {
        val request = Request.Builder()
            .url("$baseURL/boxes/$id")
            .delete()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(APIError.InvalidResponse))
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback(Result.failure(APIError.ServerError(response.code)))
                    return
                }
                callback(Result.success(Unit))
            }
        })
    }

    fun fetchObjects(boxId: Long, callback: (Result<List<ObjectItem>>) -> Unit) {
        val request = Request.Builder()
            .url("$baseURL/boxes/$boxId/objects")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(APIError.InvalidResponse))
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback(Result.failure(APIError.ServerError(response.code)))
                    return
                }
                val body = response.body?.string() ?: return callback(Result.failure(APIError.InvalidResponse))
                try {
                    val dtos = json.decodeFromString<List<ObjectItemDTO>>(body)
                    val objs = dtos.map { it.toObject() }
                    callback(Result.success(objs))
                } catch (e: Exception) {
                    callback(Result.failure(APIError.DecodingError))
                }
            }
        })
    }

    fun createObject(obj: ObjectItem, boxId: Long, callback: (Result<ObjectItem>) -> Unit) {
        val payload = mapOf("nombre" to obj.nombre, "state" to obj.state)
        val requestBody = json.encodeToString(payload).toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$baseURL/boxes/$boxId/objects")
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(jsonObjectCallback(callback))
    }

    fun updateObject(obj: ObjectItem, boxId: Long, callback: (Result<ObjectItem>) -> Unit) {
        val payload = mapOf("nombre" to obj.nombre, "state" to obj.state)
        val requestBody = json.encodeToString(payload).toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$baseURL/boxes/$boxId/objects/${obj.id}")
            .put(requestBody)
            .build()
        client.newCall(request).enqueue(jsonObjectCallback(callback))
    }

    fun deleteObject(id: Long, boxId: Long, callback: (Result<Unit>) -> Unit) {
        val request = Request.Builder()
            .url("$baseURL/boxes/$boxId/objects/$id")
            .delete()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(APIError.InvalidResponse))
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback(Result.failure(APIError.ServerError(response.code)))
                    return
                }
                callback(Result.success(Unit))
            }
        })
    }

    private fun jsonBoxCallback(callback: (Result<Box>) -> Unit): Callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback(Result.failure(APIError.InvalidResponse))
        }
        override fun onResponse(call: Call, response: Response) {
            if (!response.isSuccessful) {
                callback(Result.failure(APIError.ServerError(response.code)))
                return
            }
            val body = response.body?.string() ?: return callback(Result.failure(APIError.InvalidResponse))
            try {
                val dto = json.decodeFromString<BoxDTO>(body)
                callback(Result.success(dto.toBox()))
            } catch (e: Exception) {
                callback(Result.failure(APIError.DecodingError))
            }
        }
    }

    private fun jsonObjectCallback(callback: (Result<ObjectItem>) -> Unit): Callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback(Result.failure(APIError.InvalidResponse))
        }
        override fun onResponse(call: Call, response: Response) {
            if (!response.isSuccessful) {
                callback(Result.failure(APIError.ServerError(response.code)))
                return
            }
            val body = response.body?.string() ?: return callback(Result.failure(APIError.InvalidResponse))
            try {
                val dto = json.decodeFromString<ObjectItemDTO>(body)
                callback(Result.success(dto.toObject()))
            } catch (e: Exception) {
                callback(Result.failure(APIError.DecodingError))
            }
        }
    }
}