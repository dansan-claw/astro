package space.astro.api.central.models

data class TokenPayloadDto(

    val accessToken: String,
    val expiresIn: Int,
    val refreshToken: String,
    val scope: String?,
    val tokenType: String?
)