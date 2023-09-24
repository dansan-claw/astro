package space.astro.bot.listeners.voice.handlers

import space.astro.bot.managers.roles.SimpleMemberRolesManager
import space.astro.bot.managers.vc.VCEvent

fun VCEventHandler.handleJoinedGeneratorEvent(event: VCEvent.JoinedGenerator, memberRolesManager: SimpleMemberRolesManager) {
    val data = event.vcEventData
    val generator = event.generatorDto


}