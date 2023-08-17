package space.astro.shared.core.models.database

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import space.astro.shared.core.util.Colors
import space.astro.shared.core.util.Links
import space.astro.shared.core.util.extention.asChannelMention
import space.astro.shared.core.util.extention.asMessageMarkdownLink
import space.astro.shared.core.util.extention.asRoleMention
import space.astro.shared.core.util.extention.asTrueOrFalse

data class GuildDto(
    val guildID: String,
    val upgradedByUserID: String? = null,
    val entitlements: MutableList<GuildEntitlement> = mutableListOf(),
    var bannedCommands: MutableList<String> = mutableListOf(),
    val templates: MutableList<TemplateDto> = mutableListOf(),
    val connections: MutableList<ConnectionDto> = mutableListOf(),
    val generators: MutableList<GeneratorDto> = mutableListOf(),
    var interfaces: MutableList<InterfaceDto> = mutableListOf(),
    var errorLogsChannelId: String? = null,
    var allowMissingAdminPerm: Boolean = false,
)

/**
 * Entitlement owned by a guild
 *
 * @param id Id of the entitlement
 * @param skuId Id of the SKU of the entitlement
 * @param endsAt Expiration of the entitlement in epoch millis, NULL if it never expires
 */
data class GuildEntitlement(
    val id: String,
    val skuId: String,
    val endsAt: Long?
)

data class TemplateDto(
    val id: String = NanoIdUtils.randomNanoId(),
    val name: String,
    val enabledGeneratorIds: MutableList<String>? = null,
    val vcName: String? = null,
    val vcLimit: Int? = null,
    val vcBitrate: Int? = null,
    val vcRegion: String? = null
)

data class GeneratorDto(
    val id: String,
    var fallbackId: String? = null,
    var queueMode: Boolean = false,
    var defaultName: String = "{nickname}'s VC",
    var defaultLockedName: String? = "Locked | {nickname}'s VC",
    var defaultHiddenName: String? = "Hidden | {nickname}'s VC",
    var userLimit: Int = 0,
    var bitrate: Int = 0,
    var category: String? = null,
    var permissionsInherited: PermissionsInherited = PermissionsInherited.GENERATOR,
    var permissionsTargetRole: String? = null,
    var permissionsImmuneRole: String? = null,
    var ownerPermissions: Long = 0,
    var ownerRole: String? = null,
    var initialState: VCState = VCState.UNLOCKED,
    var initialPosition: InitialPosition = InitialPosition.BOTTOM,
    var renameConditions: RenameConditions = RenameConditions(),
    var commandsSettings: CommandsSettings = CommandsSettings(),

    var autoChat: Boolean = false,
    var chatCategory: String? = category,
    var chatTopic: String? = "Temporary text chat made by Astro | ${Links.base}",
    var chatNsfw: Boolean = false,
    var chatSlowmode: Int = 0,
    var chatPermissionsInherited: PermissionsInherited = PermissionsInherited.NONE,
    var defaultChatName: String = "{vc_name}",
    var defaultChatText: String? = null,
    var defaultChatTextEmbed: Boolean = true,
    var chatInterface: Int = -1
)


enum class PermissionsInherited {
    GENERATOR, CATEGORY, NONE
}

enum class InitialPosition {
    BEFORE, AFTER, BOTTOM
}

enum class VCState(
    val permissionDenied: Permission? = null
) {
    UNLOCKED, LOCKED(Permission.VOICE_CONNECT), HIDDEN(Permission.VIEW_CHANNEL)
}


data class InterfaceDto(
    var channelID: String,
    var messageID: String,
    var buttons: MutableList<InterfaceButton> = mutableListOf(),

    @Deprecated("Use buttons property instead")
    var actions: MutableList<InterfaceAction> = mutableListOf(),

    val embedStyle: EmbedStyle = EmbedStyle(),
    var generateEmbedFields: Boolean = false
)

data class EmbedStyle(
    var url: String? = Links.base,
    var title: String? = null,
    var description: String? = "You can use this interface to manage your voice channel.\nYou can also use `/vc` slash commands!",
    var timestamp: Long? = null,
    var color: Int = Colors.purple.rgb,
    var thumbnail: String? = null,
    var image: String? = Links.interfaceInstructions,
    var authorName: String? = "Astro Interface",
    var authorUrl: String? = Links.base,
    var authorIconUrl: String? = Links.logo,
    var footer: String? = "Use the buttons below to manage your voice channel",
    var footerIconUrl: String? = null
)

data class InterfaceAction(
    /**
     * Action path
     */
    var cmdPath: String,
    var name: String? = null,
    var emoji: String? = null,
    var buttonStyleKey: Int = ButtonStyle.SECONDARY.key,
    var buttonDisabled: Boolean = false,
    var position: Pair<Int, Int>,
)

data class InterfaceButton(
    var id: String,
    var name: String? = null,
    var emoji: String? = null,
    var buttonStyleKey: Int = ButtonStyle.SECONDARY.key,
    var buttonDisabled: Boolean = false,
    var position: Pair<Int, Int>,
    var fieldValue: String = id
)

data class ConnectionDto(
    var id: String,
    var roleID: String,
    var action: ConnectionAction = ConnectionAction.ASSIGN
)

enum class ConnectionAction(
    var permanent: Boolean = false
) {
    ASSIGN, REMOVE, TOGGLE
}

data class CommandsSettings(
    var maxUserLimit: Int = 99,
    var minUserLimit: Int = 0,

    var maxBitrate: Int? = null,
    var minBitrate: Int = 8000,

    var badwordsAllowed: Boolean = true,
)

data class RenameConditions(
    var stateChange: Boolean = true,
    var ownerChange: Boolean = true,
    var renamed: Boolean = false,
    var activityChange: Boolean = true
)