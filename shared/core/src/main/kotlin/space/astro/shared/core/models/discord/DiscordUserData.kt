package space.astro.shared.core.models.discord

data class DiscordUserData(
    val id: Long,
    val username: String,
    val discriminator: String,
    val avatar: String?
)
