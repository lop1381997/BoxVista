package com.hirlu.boxvista

import com.hirlu.boxvista.models.Box
import com.hirlu.boxvista.models.BoxDTO
import com.hirlu.boxvista.models.LoginRequest
import com.hirlu.boxvista.models.LoginResponse
import com.hirlu.boxvista.models.ObjectItem
import com.hirlu.boxvista.models.ObjectItemDTO
import com.hirlu.boxvista.models.RegisterRequest
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit-only NetworkManager (no explicit OkHttp usage).
 *
 * Las excepciones de red se propagan (HttpException/IOException). Manejalas desde tu capa de UI/VM.
 */


object NetworkManager {
    class UnauthorizedException(
        override val message: String = "Inicia sesión o regístrate para continuar."
    ) : RuntimeException(message)

    enum class BaseURL(val url: String) {
        LOCAL("http://10.0.2.2:3000/api/"),
        REMOTE("https://your-prod-host/api/")
    }

    // ───────────────────────────── API ─────────────────────────────
    private interface ApiService {
        @POST("auth/login")
        suspend fun login(@Body body: LoginRequest): LoginResponse

        @POST("auth/register")
        suspend fun register(@Body body: RegisterRequest): LoginResponse

        @GET("boxes")
        suspend fun fetchBoxes(@Header("Authorization") authorization: String): List<BoxDTO>

        @GET("boxes/{id}")
        suspend fun fetchBox(
            @Header("Authorization") authorization: String,
            @Path("id") id: Long,
        ): BoxDTO

        @POST("boxes")
        suspend fun createBox(
            @Header("Authorization") authorization: String,
            @Body body: BoxDTO,
        ): BoxDTO

        @PUT("boxes/{id}")
        suspend fun updateBox(
            @Header("Authorization") authorization: String,
            @Path("id") id: Long,
            @Body body: BoxDTO,
        ): BoxDTO

        @DELETE("boxes/{id}")
        suspend fun deleteBox(
            @Header("Authorization") authorization: String,
            @Path("id") id: Long,
        )

        @GET("boxes/{boxId}/objects")
        suspend fun fetchObjects(
            @Header("Authorization") authorization: String,
            @Path("boxId") boxId: Long,
        ): List<ObjectItemDTO>

        @GET("boxes/{boxId}/objects/{id}")
        suspend fun fetchObject(
            @Header("Authorization") authorization: String,
            @Path("boxId") boxId: Long,
            @Path("id") objectid: Long,
        ): ObjectItemDTO

        @POST("boxes/{boxId}/objects")
        suspend fun createObject(
            @Header("Authorization") authorization: String,
            @Path("boxId") boxId: Long,
            @Body body: ObjectItemDTO,
        ): ObjectItemDTO

        @PUT("boxes/{boxId}/objects/{id}")
        suspend fun updateObject(
            @Header("Authorization") authorization: String,
            @Path("boxId") boxId: Int,
            @Path("id") id: Long,
            @Body body: ObjectItemDTO,
        ): ObjectItemDTO

        @DELETE("boxes/{boxId}/objects/{id}")
        suspend fun deleteObject(
            @Header("Authorization") authorization: String,
            @Path("boxId") boxId: Long,
            @Path("id") id: Long,
        )
    }

    // ───────────────────── Retrofit (sin OkHttp explícito) ─────────────────────

    @Volatile private var api: ApiService? = null
    @Volatile private var tokenProvider: (() -> String?)? = null

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

    fun setAuthTokenProvider(provider: () -> String?) {
        tokenProvider = provider
    }

    fun clearAuthTokenProvider() {
        tokenProvider = null
    }

    private fun requireAuthHeader(): String {
        val token = tokenProvider?.invoke()?.trim().orEmpty()
        if (token.isEmpty()) throw UnauthorizedException()
        return "Bearer $token"
    }

    private suspend fun <T> authenticatedCall(call: suspend (String) -> T): T {
        val authHeader = requireAuthHeader()
        return try {
            call(authHeader)
        } catch (error: HttpException) {
            if (error.code() == 401) {
                throw UnauthorizedException()
            }
            throw error
        }
    }

    // ──────────────────────── Métodos públicos suspend ─────────────────────────
    // Network exceptions (HttpException/IOException) propagate to the UI/VM layer for handling
    suspend fun login(email: String, password: String): String =
        requireApi().login(LoginRequest(email = email, password = password)).token

    suspend fun register(email: String, password: String): String =
        requireApi().register(RegisterRequest(email = email, password = password)).token

    suspend fun fetchBoxes(): List<Box> =
        authenticatedCall { authorization ->
            requireApi().fetchBoxes(authorization).map { it.toDomain() }
        }

    suspend fun fetchBox(id: Long): Box =
        authenticatedCall { authorization ->
            requireApi().fetchBox(authorization, id).toDomain()
        }

    suspend fun createBox(box: Box): Box =
        authenticatedCall { authorization ->
            requireApi().createBox(authorization, box.toDto()).toDomain()
        }

    suspend fun updateBox(id: Long, box: Box): Box =
        authenticatedCall { authorization ->
            requireApi().updateBox(authorization, id, box.toDto()).toDomain()
        }

    suspend fun deleteBox(id: Long) {
        authenticatedCall { authorization -> requireApi().deleteBox(authorization, id) }
    }

    suspend fun fetchObjects(boxId: Long): List<ObjectItem> =
        authenticatedCall { authorization ->
            requireApi().fetchObjects(authorization, boxId).map { it.toDomain() }
        }

    suspend fun fetchObject(boxId: Long, id: Long): ObjectItem =
        authenticatedCall { authorization ->
            requireApi().fetchObject(authorization, boxId, id).toDomain()
        }

    suspend fun createObject(boxId: Long, obj: ObjectItem): ObjectItem =
        authenticatedCall { authorization ->
            requireApi().createObject(authorization, boxId, obj.toDto()).toDomain()
        }

    suspend fun updateObject(boxId: Int, obj: ObjectItem): ObjectItem =
        authenticatedCall { authorization ->
            requireApi().updateObject(authorization, boxId, obj.id, obj.toDto()).toDomain()
        }

    suspend fun deleteObject(boxId: Long, objectId: Long) {
        authenticatedCall { authorization -> requireApi().deleteObject(authorization, boxId, objectId) }
    }

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
