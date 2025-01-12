package database

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.DELETE

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

    data class AddFavoriteRecipeRequest(
        val user_id: Int,
        val recipe_id: Int
    )

    @POST("receptomat/register.php")
    fun registerUser(@Body request: RegisterRequest): Call<BasicResponse>

    @GET("receptomat/get_favorite_recipes.php")
    fun getFavoriteRecipesForPerson(@Query("user_id") userId: Int?): Call<FavoriteRecipesResponse>

    @POST("receptomat/get_favorite_recipes.php")
    fun addFavoriteRecipe(@Body request: AddFavoriteRecipeRequest): Call<BasicResponse>

    @DELETE("receptomat/get_favorite_recipes.php")
    fun removeFavoriteRecipe(@Query("user_id") userId: Int, @Query("recipe_id") recipeId: Int): Call<BasicResponse>
}