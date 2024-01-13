package space.astro.bot.interactions

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import space.astro.bot.models.discord.vc.VCOperationCTX
import space.astro.shared.core.models.database.ConnectionData
import space.astro.shared.core.models.database.GeneratorData
import space.astro.shared.core.models.database.GuildData
import space.astro.shared.core.models.database.InterfaceData

open class InteractionContext(
    val guild: Guild,
    val member: Member,
    val user: User
) {
    val guildId = guild.id
    val memberId = member.id
}

class VcInteractionContext(
    val vcOperationCTX: VCOperationCTX,
    guild: Guild,
    member: Member,
    user: User,
    usedInterfaceComponent: Boolean
) : InteractionContext(guild, member, user)

class SettingsInteractionContext(
    val guildData: GuildData,
    guild: Guild,
    member: Member,
    user: User,
) : InteractionContext(guild, member, user)

class GeneratorSettingsInteractionContext(
    val generatorData: GeneratorData,
    val guildData: GuildData,
    guild: Guild,
    member: Member,
    user: User,
) : InteractionContext(guild, member, user)

class InterfaceSettingsInteractionContext(
    val interfaceData: InterfaceData,
    val guildData: GuildData,
    guild: Guild,
    member: Member,
    user: User,
) : InteractionContext(guild, member, user)

class ConnectionSettingsInteractionContext(
    val connectionData: ConnectionData,
    val guildData: GuildData,
    guild: Guild,
    member: Member,
    user: User,
) : InteractionContext(guild, member, user)