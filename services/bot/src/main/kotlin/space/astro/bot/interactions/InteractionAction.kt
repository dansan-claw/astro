package space.astro.bot.interactions

enum class InteractionAction(
    val premium: Boolean,
    val cooldown: Long
) {
    GENERIC(false, 0),

    CHAT(true, 2000),
    CLAIM(false, 2000),
    TRANSFER(false, 2000),
    BAN(false, 2000),
    HIDE(false, 2000),
    INVITE(true, 5000),
    KICK(false, 2000),
    LOCK(false, 2000),
    PERMIT(false, 2000),
    UNHIDE(false, 2000),
    UNLOCK(false, 2000),
    BITRATE(false, 2000),
    LIMIT(false, 2000),
    NAME(false, 2000),
    REGION(false, 2000),
    TEMPLATE(false, 2000),
    WAITING_ROOM(true, 2000)
}