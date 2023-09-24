package space.astro.bot.listeners.voice.handlers

import space.astro.bot.managers.roles.SimpleMemberRolesManager
import space.astro.bot.managers.vc.VCEvent

object VCEventHandler {
    fun handleEvents(events: List<VCEvent>, memberRolesManager: SimpleMemberRolesManager) {
        events.forEach {
            when (it) {
                is VCEvent.JoinedGenerator -> handleJoinedGeneratorEvent(it, memberRolesManager)
            }
        }
    }
}