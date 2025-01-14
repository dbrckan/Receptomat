package database

import com.example.receptomat.entities.Category
import com.example.receptomat.entities.Preference
import com.example.receptomat.entities.Recipe
import com.example.receptomat.entities.RecipeDB
import com.example.receptomat.entities.Units
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
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
        val reviews: List<Review>,
        val recipes: List<Recipe>
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

    @GET("receptomat/get_user_profile.php")
    fun getUserProfile(@Query("user_id") userId: Int): Call<UserProfileResponse>

    @POST("receptomat/update_notifications.php")
    fun updateNotifications(@Body request: UpdateNotificationsRequest): Call<BasicResponse>


    /*upravljanje receptima*/
    @GET("receptomat/get_categories.php")
    fun getCategories(): Call<List<Category>>

    @GET("receptomat/get_units.php")
    fun getUnits(): Call<List<Units>>

    @FormUrlEncoded
    @POST("receptomat/add_item_from_recipe.php")
    fun addItem(
        @Field("name") name: String,
    ): Call<MessageResponse>


    @GET("receptomat/get_preference.php")
    fun getPreferences(): Call<List<Preference>>

    @GET("receptomat/get_recipe.php")
    fun getRecipes(): Call<List<RecipeDB>>

    @FormUrlEncoded
    @POST("receptomat/add_recipe.php")
    fun addRecipe(
        @Field("name") name: String,
        @Field("description") description: String,
        @Field("time") time: Int,
        @Field("user_id") userId: Int,
        @Field("category_id") categoryId: Int,
        @Field("preference_id") preferenceId: Int
    ): Call<MessageResponse>

    @POST("receptomat/delete_recipe.php")
    @FormUrlEncoded
    fun deleteRecipe(@Field("recipe_id") recipeId: Int): Call<MessageResponse>

    @FormUrlEncoded
    @POST("receptomat/add_recipe_item.php")
    fun linkItemToRecipe(
        @Field("recipe_id") recipeId: Int,
        @Field("item_name") itemName: String,
        @Field("quantity") quantity: Float,
        @Field("unit_id") unitId: Int
    ): Call<MessageResponse>
}