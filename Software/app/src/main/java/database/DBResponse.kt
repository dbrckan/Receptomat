package database

import com.example.receptomat.entities.Ingredient
import java.io.Serializable


data class LoginResponse(
    val success: Boolean?,
    val user_id: Int?,
    val error: String?
)

data class BasicResponse(
    val success: Boolean?,
    val error: String?
)

data class FavoriteRecipesResponse(
    val recipes: List<RecipeResponse>
)
data class RecipeResponse(
    val recipe_id: Int,
    val name: String,
    val description: String,
    val time: Int,
    val user_id: Int,
    val category_id: Int,
    val preference_id: Int,
    val image_path: String?
)
data class ShoppingListsWithItemsResponse(
    val success: Boolean,
    val shopping_lists: List<ShoppingListWithItems>
)

data class ShoppingListWithItems(
    val list_id: Int,
    val list_name: String,
    val items: List<String>
)

data class AddShoppingListRequest(
    val list_name: String,
    val user_id: Int,
    val items: List<String>
)

data class DeleteItemRequest(
    val item_id: Int,
    val list_id: Int
)

data class UpdateShoppingListRequest(
    val list_id: Int,
    val list_name: String,
    val items: List<String>
)

data class DeleteListRequest(
    val list_id: Int
)

data class UpdateNotificationsRequest(
    val user_id: Int,
    val notifications: Int
)

data class UserProfileResponse(
    val success: Boolean,
    val username: String,
    val email: String,
    val notifications: Int
)

data class RecipeRequest(
    val recipe_id: Int,
    val name: String,
    val description: String,
    val time: Int,
    val user_id: Int,
    val category_id: Int,
    val category_name: String?,
    val preference_id: Int,
    val preference_name: String?,
    val ingredients: List<Ingredient>?
)

data class RecipeResponseAdd(
    val success: Boolean,
    val message: String?,
    val data: RecipeRequest?
)

data class MessageResponse(
    var success: Boolean,
    val message: String,
    val recipe_id: Int?,
    val item_id: Int?
)

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
    val date: String
)

data class ReviewsResponse(
    val success: Boolean,
    val reviews: List<Review>,
    val recipes: List<RecipeResponse>
)
data class Review(
    val review_id: String,
    val comment: String,
    val username : String,
    val rating: String,
    val date: String,
    val recipe_id: String
)

data class AddReviewResponse(
    val success: Boolean,
    val message: String,
    val error: String?
)


data class GetRecipeItemsResponse(
    val success: Boolean,
    val items: List<RecipeItem>
)

data class RecipeItem(
    val item_id: Int,
    val item_name: String,
    val quantity: String,
    val unit: String
)



data class UpdateUserPreferenceRequest(
    val user_id: Int,
    val preference_id: Int
)

data class PreferenceResponse(
    val success: Boolean,
    val data: List<Preference>
)

data class Preference(
    val preference_id: String,
    val name: String,
    val description: String
)

data class UserPreferenceResponse(
    val success: Boolean,
    val preference_id: Int?
)


