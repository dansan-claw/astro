package space.astro.bot.managers.vc

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import space.astro.shared.core.models.database.ConnectionDto
import space.astro.shared.core.models.database.GeneratorDto

/**
 * @param event the voice update event that triggered this [VCEventData]
 * @param generators list of astro temporary VC generators
 * @param temporaryVCs list of existing temporary VCs
 * @param connections list of astro connections
 */
data class VCEventData(
    val event: GuildVoiceUpdateEvent,
    val generators: List<GeneratorDto>,
    val temporaryVCs: List<TemporaryVCData>,
    val connections: List<ConnectionDto>
) {
    val userId = event.member.id
    val joinedChannel = event.channelJoined
    val leftChannel = event.channelLeft
}