package database

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.DELETE
import java.util.Date

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

    data class ReviewRequest(
        val user_id: Int,
        val recipe_id: Int,
        val comment: String,
        val rating: Int,
        val date: Date
    )

    data class ReviewsResponse(
        val success: Boolean,
        val reviews: List<Review>
    )

    data class Review(
        val review_id: String,
        val comment: String,
        val rating: String,
        val date: String,
        val recipe_id: String
    )


    @POST("receptomat/register.php")
    fun registerUser(@Body request: RegisterRequest): Call<BasicResponse>

    @GET("receptomat/get_favorite_recipes.php")
    fun getFavoriteRecipesForPerson(@Query("user_id") userId: Int?): Call<FavoriteRecipesResponse>

    @POST("receptomat/get_favorite_recipes.php")
    fun addFavoriteRecipe(@Body request: AddFavoriteRecipeRequest): Call<BasicResponse>

    @DELETE("receptomat/get_favorite_recipes.php")
    fun removeFavoriteRecipe(@Query("user_id") userId: Int, @Query("recipe_id") recipeId: Int): Call<BasicResponse>

    @GET("receptomat/get_shopping_list_and_items.php")
    fun getShoppingListsWithItems(@Query("user_id") userId: Int): Call<ShoppingListsWithItemsResponse>


    @POST("receptomat/create_shopping_list_and_items.php")
    fun createShoppingListWithItems(@Body request: AddShoppingListRequest): Call<BasicResponse>

    @POST("receptomat/delete_items_from_shopping_list.php")
    fun deleteItemFromList(@Body request: DeleteItemRequest): Call<BasicResponse>

    @POST("receptomat/update_shopping_list.php")
    fun updateShoppingList(@Body request: UpdateShoppingListRequest): Call<BasicResponse>

    @POST("receptomat/delete_shopping_list.php")
    fun deleteShoppingList(@Body request: DeleteListRequest): Call<BasicResponse>



    @POST("receptomat/recipe_review.php")
    fun addReview(@Body request: ReviewRequest): Call<BasicResponse>

    @GET("receptomat/recipe_review.php")
    fun getReviewsForRecipeId(@Query("recipe_id") recipeId: Int): Call<ReviewsResponse>

    @GET("receptomat/recipe_review.php")
    fun getReviewsForRecipeUserId(@Query("user_id") userId: Int): Call<ReviewsResponse>

    @DELETE("receptomat/recipe_review.php")
    fun removeReview(@Query("user_id") userId: Int, @Query("recipe_id") recipeId: Int): Call<BasicResponse>

}