package co.edu.udea.compumovil.gr04_20262.lab2.data.remote

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/** Servicio REST público https://dummyjson.com consumido con Retrofit. */
interface DummyJsonApi {

    /** Devuelve una frase aleatoria. Lo consume el worker periódico. */
    @GET("quotes/random")
    suspend fun randomQuote(): QuoteDto

    /** Devuelve los datos de un usuario. Lo consume la pantalla de perfil. */
    @GET("users/{id}")
    suspend fun user(@Path("id") id: Int): UserDto
}

@Serializable
data class QuoteDto(val id: Int = 0, val quote: String, val author: String)

@Serializable
data class CompanyDto(val name: String = "", val title: String = "")

@Serializable
data class UserDto(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
    val company: CompanyDto = CompanyDto(),
)

object ApiClient {
    private val json = Json { ignoreUnknownKeys = true }

    val api: DummyJsonApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(DummyJsonApi::class.java)
    }
}
