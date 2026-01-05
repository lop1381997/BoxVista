package com.hirlu.boxvista

import com.hirlu.boxvista.models.Box
import com.hirlu.boxvista.models.BoxDTO
import com.hirlu.boxvista.models.ObjectItem
import com.hirlu.boxvista.models.ObjectItemDTO
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit-only NetworkManager (no explicit OkHttp usage).
 *
 * Las excepciones de red se propagan (HttpException/IOException). Manejalas desde tu capa de UI/VM.
 */


object NetworkManager {

    enum class BaseURL(val url: String) {
        LOCAL("http://10.0.2.2:3000/api/"),
        REMOTE("https://your-prod-host/api/")
    }

    // ───────────────────────────── API ─────────────────────────────
    private interface ApiService {
        @GET("boxes")
        suspend fun fetchBoxes(): List<BoxDTO>

        @GET("boxes/{id}")
        suspend fun fetchBox(@Path("id") id: Long): BoxDTO

        @POST("boxes")
        suspend fun createBox(@Body body: BoxDTO): BoxDTO

        @PUT("boxes/{id}")
        suspend fun updateBox(@Path("id") id: Long, @Body body: BoxDTO): BoxDTO

        @DELETE("boxes/{id}")
        suspend fun deleteBox(@Path("id") id: Long)

        @GET("boxes/{boxId}/objects")
        suspend fun fetchObjects(@Path("boxId") boxId: Long): List<ObjectItemDTO>
        @GET("boxes/{boxId}/objects/{id}")
        suspend fun fetchObject(@Path("boxId") boxId: Long, @Path("id") objectid: Long):ObjectItemDTO

        @POST("boxes/{boxId}/objects")
        suspend fun createObject(
            @Path("boxId") boxId: Long,
            @Body body: ObjectItemDTO,
        ): ObjectItemDTO

        @PUT("boxes/{boxId}/objects/{id}")
        suspend fun updateObject(
            @Path("boxId") boxId: Int,
            @Path("id") id: Long,
            @Body body: ObjectItemDTO,
        ): ObjectItemDTO

        @DELETE("boxes/{boxId}/objects/{id}")
        suspend fun deleteObject(
            @Path("boxId") boxId: Long,
            @Path("id") id: Long,
        )
    }

    // ───────────────────── Retrofit (sin OkHttp explícito) ─────────────────────

    @Volatile private var api: ApiService? = null

    fun init(baseUrl: BaseURL = BaseURL.LOCAL) {
        init(baseUrl.url)
    }

    fun init(baseUrl: String) {
        this.api = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private fun requireApi(): ApiService =
        api ?: throw IllegalStateException("NetworkManager no inicializado. Llama a NetworkManager.init(baseUrl) al inicio de la app.")

    // ──────────────────────── Métodos públicos suspend ─────────────────────────
    // Network exceptions (HttpException/IOException) propagate to the UI/VM layer for handling
    suspend fun fetchBoxes(): List<Box> =
        requireApi().fetchBoxes().map { it.toDomain() }

    suspend fun fetchBox(id: Long): Box = requireApi().fetchBox(id).toDomain()

    suspend fun createBox(box: Box): Box = requireApi().createBox(box.toDto()).toDomain()

    suspend fun updateBox(id: Long, box: Box): Box = requireApi().updateBox(id, box.toDto()).toDomain()

    suspend fun deleteBox(id: Long) { requireApi().deleteBox(id) }

    suspend fun fetchObjects(boxId: Long): List<ObjectItem> =
        requireApi().fetchObjects(boxId).map { it.toDomain() }
    suspend fun fetchObject(boxId: Long, id: Long): ObjectItem =
        requireApi().fetchObject(boxId, id).toDomain()

    suspend fun createObject(boxId: Long, obj: ObjectItem): ObjectItem =
        requireApi().createObject(boxId, obj.toDto()).toDomain()

    suspend fun updateObject(boxId: Int, obj: ObjectItem): ObjectItem =
        requireApi().updateObject(boxId, obj.id, obj.toDto()).toDomain()

    suspend fun deleteObject(boxId: Long, objectId: Long) { requireApi().deleteObject(boxId, objectId) }

    // ──────────────────────────── Mappers DTO ↔ dominio ───────────────────────────
    private fun BoxDTO.toDomain(): Box = this.toBox()

    private fun Box.toDto(): BoxDTO = BoxDTO(
        id = id,
        name = name,
        description = description,
        objetos = objects.map { it.toDto() }.toMutableList()
    )

    private fun ObjectItemDTO.toDomain(): ObjectItem = this.toObject()

    private fun ObjectItem.toDto(): ObjectItemDTO = ObjectItemDTO(
        id = id,
        nombre = name,
        state = state,
        boxId = boxId
    )
}
