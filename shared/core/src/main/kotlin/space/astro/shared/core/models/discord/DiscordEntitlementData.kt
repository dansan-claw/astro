package space.astro.shared.core.models.discord

import java.time.OffsetDateTime

data class DiscordEntitlementData(
    val id: String,
    val guildId: String?,
    val endsAt: OffsetDateTime?
)