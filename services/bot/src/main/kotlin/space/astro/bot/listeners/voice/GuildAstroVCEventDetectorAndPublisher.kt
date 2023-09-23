package space.astro.bot.listeners.voice

import mu.KotlinLogging
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import space.astro.bot.managers.vc.VCEventData
import space.astro.bot.managers.vc.VCEventDetector

private val log = KotlinLogging.logger {  }

@Component
class GuildAstroVCEventDetectorAndPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @EventListener
    fun receiveGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        // Ignore bots and when a user doesn't switch voice channel
        if (event.member.user.isBot || event.channelJoined?.id == event.channelLeft?.id) {
            return
        }

        val vcEventData = VCEventData(
            event = event,
            generators = listOf(),
            temporaryVCs = listOf(),
            connections = listOf(),
        )

        try {
           VCEventDetector.detectAstroVoiceEvents(vcEventData)
               .forEach {
                   applicationEventPublisher.publishEvent(it)
               }
        } catch (e: IllegalStateException) {
            log.error { e.message }
        }
    }
}