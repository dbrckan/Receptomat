package database

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("receptomat/login.php")
    fun loginUser(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<LoginResponse>

    data class RegisterRequest(
        val name: String,
        val username: String,
        val email: String,
        val password: String
    )

    @POST("receptomat/register.php")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>
}