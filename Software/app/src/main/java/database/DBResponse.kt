package database

import com.example.receptomat.entities.Ingredient

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
    val meal: String,
    val time: Int,
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


