package space.astro.bot.managers.util

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import space.astro.bot.listeners.events.ConfigurationErrorEvent

@Component
class GuildErrorNotifier(
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun send() {
        TODO()
        val event = ConfigurationErrorEvent()
        applicationEventPublisher.publishEvent(event)
    }
}