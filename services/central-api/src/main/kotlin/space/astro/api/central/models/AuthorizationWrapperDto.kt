package space.astro.api.central.models

import space.astro.shared.core.models.discord.DiscordUserData

data class AuthorizationWrapperDto(
    val user: DiscordUserData,
    val token: TokenPayloadDto,
)
