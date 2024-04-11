package space.astro.api.central.models

data class TokenPayloadDto(
    val accessToken: String,
    val expiresIn: Int,
    val refreshToken: String,
    val scope: String?,
    val tokenType: String?,
    val guild: TokenGuildPayloadDto?
)

data class TokenGuildPayloadDto(
    val id: String,
    val name: String,
    val icon: String?
)