package database

import com.example.receptomat.entities.Category
import com.example.receptomat.entities.Preference
import com.example.receptomat.entities.RecipeDB
import com.example.receptomat.entities.Units
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header

interface ApiService {
    @GET("receptomat/login.php")
    fun loginUser(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<LoginResponse>


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
    fun addReview(@Body request: ReviewRequest): Call<AddReviewResponse>

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
        @Field("quantity") quantity: Double,
        @Field("unit_id") unitId: Int
    ): Call<MessageResponse>

    @GET("receptomat/get_recipe_by_id.php")
    suspend fun getRecipeById(@Query("recipe_id") recipeId: Int): Response<RecipeResponseAdd>

    @POST("receptomat/update_recipe.php")
    suspend fun updateRecipe(@Body updatedRecipe: RecipeRequest): Response<RecipeResponse>

    @POST("receptomat/update_ingredients.php")
    suspend fun updateIngredients(@Body ingredientsRequest: IngredientsRequest): Response<IngredientsResponse>


    /*pretrazivanje recepta*/
    @GET("receptomat/search_recipes.php")
    fun searchRecipesByName(@Query("search") name: String): Call<List<RecipeDB>>

    @GET("/receptomat/get_recipe_item_for_cart.php")
    fun getRecipeItems(@Query("recipe_id") recipeId: Int): Call<GetRecipeItemsResponse>

    @GET("/receptomat/get_preferences.php")
    fun getPreferencesProfile(): Call<PreferenceResponse>


    @POST("/receptomat/update_user_preference.php")
    fun updateUserPreference(@Body request: UpdateUserPreferenceRequest): Call<BasicResponse>

    @GET("/receptomat/get_user_preference.php")
    fun getUserPreference(@Query("user_id") userId: Int): Call<UserPreferenceResponse>

    @FormUrlEncoded
    @POST("receptomat/add_recipe_to_meal_plan.php")
    fun addRecipeToPlan(
        @Field("recipe_id") recipeId: Int,
        @Field("plan_id") planId: Int,
        @Field("day_id") dayId: Int
    ): Call<MessageResponse>

    @GET("receptomat/get_plan_by_user.php")
    fun getPlanIdByUserId(@Query("user_id") userId: Int): Call<PlanResponse>
    @GET("receptomat/get_recipes_for_day.php")
    fun getRecipesByDay(
        @Query("day_id") dayId: Int,
        @Query("plan_id") planId: Int
    ): Call<RecipePlanResponse>

    @FormUrlEncoded
    @POST("receptomat/delete_recipe_plan.php")
    fun removeRecipeFromPlan(
        @Field("recipe_id") recipeId: Int,
        @Field("plan_id") planId: Int,
        @Field("day_id") dayId: Int
    ): Call<RecipePlanResponse>

    @GET("receptomat/sendCode.php")
    fun requestLoginCode(
        @Header("X-Authorization") authToken: String,
        @Query("username") username: String,
        @Query("fcm_token") fcmToken: String?
    ): Call<OtpResponse>

}