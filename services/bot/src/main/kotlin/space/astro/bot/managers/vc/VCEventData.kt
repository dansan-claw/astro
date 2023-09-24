package space.astro.bot.managers.vc

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import space.astro.shared.core.models.database.ConnectionDto
import space.astro.shared.core.models.database.GeneratorDto
import space.astro.shared.core.models.database.GuildDto
import space.astro.shared.core.models.database.TemporaryVCDto

/**
 * @param event the voice update event that triggered this [VCEventData]
 * @param generators list of astro temporary VC generators
 * @param temporaryVCs list of existing temporary VCs
 * @param connections list of astro connections
 */
data class VCEventData(
    val event: GuildVoiceUpdateEvent,
    val guildDto: GuildDto,
    val temporaryVCs: List<TemporaryVCDto>,
) {
    val guild = event.guild
    val member = event.member
    val userId = event.member.id
    val joinedChannel = event.channelJoined
    val leftChannel = event.channelLeft

    val generators = guildDto.generators
    val connections = guildDto.connections
}