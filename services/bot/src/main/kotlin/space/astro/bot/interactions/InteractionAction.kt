package space.astro.bot.interactions

import net.dv8tion.jda.api.Permission

/**
 * Generic Astro action
 *
 * @property premium
 * @property cooldown in milliseconds
 * @property botPermissions
 */
enum class InteractionAction(
    val premium: Boolean,
    val cooldown: Long,
    val botPermissions: List<Permission>,
    val memberPermissions: List<Permission>
) {
    GENERIC(false, 0, emptyList(), emptyList()),

    VC_CHAT(true, 2000, listOf(Permission.ADMINISTRATOR), emptyList()),
    VC_LOGS(false, 2000, emptyList(), emptyList()),
    VC_CLAIM(false, 2000, listOf(Permission.ADMINISTRATOR), emptyList()),
    VC_TRANSFER(false, 2000, listOf(Permission.ADMINISTRATOR), emptyList()),
    VC_BAN(false, 2000, listOf(Permission.ADMINISTRATOR), emptyList()),
    VC_HIDE(false, 2000, listOf(Permission.ADMINISTRATOR), emptyList()),
    VC_INVITE(true, 5000, listOf(Permission.ADMINISTRATOR), emptyList()),
    VC_LOCK(false, 2000, listOf(Permission.ADMINISTRATOR), emptyList()),
    VC_PERMIT(false, 2000, listOf(Permission.ADMINISTRATOR), emptyList()),
    VC_UNHIDE(false, 2000, listOf(Permission.ADMINISTRATOR), emptyList()),
    VC_UNLOCK(false, 2000, listOf(Permission.ADMINISTRATOR), emptyList()),
    VC_BITRATE(false, 2000, listOf(Permission.ADMINISTRATOR), emptyList()),
    VC_LIMIT(false, 2000, listOf(Permission.ADMINISTRATOR), emptyList()),
    VC_NAME(false, 2000, listOf(Permission.ADMINISTRATOR), emptyList()),
    VC_REGION(false, 2000, listOf(Permission.ADMINISTRATOR), emptyList()),
    VC_TEMPLATE(false, 2000, listOf(Permission.ADMINISTRATOR), emptyList()),
    VC_WAITING_ROOM(true, 2000, listOf(Permission.ADMINISTRATOR), emptyList()),

    // PREDASHBOARD
    SETTINGS(false, 2000, listOf(Permission.ADMINISTRATOR), listOf(Permission.ADMINISTRATOR)),
    HIGH_COOLDOWN_NO_ADMIN(false, 5000, listOf(), listOf(Permission.ADMINISTRATOR))
}