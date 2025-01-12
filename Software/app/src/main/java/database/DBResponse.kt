package database
import com.example.receptomat.entities.Recipe

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
    val recipes: List<Recipe>
)
