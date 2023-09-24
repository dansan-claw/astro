package space.astro.bot.listeners.voice.handlers

import space.astro.bot.managers.cooldown.CooldownsManager
import space.astro.bot.managers.roles.SimpleMemberRolesManager
import space.astro.bot.managers.util.GuildErrorNotifier
import space.astro.bot.managers.util.PremiumRequirementDetector
import space.astro.bot.managers.vc.VCEvent
import space.astro.bot.managers.vc.VCNameManager
import space.astro.bot.managers.vc.VCPositionManager

class VCEventHandler(
    val premiumRequirementDetector: PremiumRequirementDetector,
    val cooldownsManager: CooldownsManager,
    val vcNameManager: VCNameManager,
    val vcPositionManager: VCPositionManager
) {
    fun handleEvents(
        events: List<VCEvent>,
        guildErrorNotifier: GuildErrorNotifier,
        memberRolesManager: SimpleMemberRolesManager
    ) {
        events.forEach {
            when (it) {
                is VCEvent.JoinedGenerator -> handleJoinedGeneratorEvent(it, guildErrorNotifier, memberRolesManager)
            }
        }
    }
}