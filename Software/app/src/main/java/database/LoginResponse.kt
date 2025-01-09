package database

data class LoginResponse(
    val success: Boolean?,
    val error: String?
)

data class RegisterResponse(
    val success: Boolean?,
    val error: String?
)
