package database

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