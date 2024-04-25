package space.astro.shared.core.models.database

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.Region
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.Widget
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import space.astro.shared.core.util.extention.*
import space.astro.shared.core.util.ui.Colors
import space.astro.shared.core.util.ui.Links

data class GuildData(
    val guildID: String,
    var upgradedByUserID: String? = null,
    val entitlements: MutableList<GuildEntitlement> = mutableListOf(),
    var bannedCommands: MutableList<String> = mutableListOf(),
    val templates: MutableList<TemplateData> = mutableListOf(),
    val connections: MutableList<ConnectionData> = mutableListOf(),
    val generators: MutableList<GeneratorData> = mutableListOf(),
    var interfaces: MutableList<InterfaceData> = mutableListOf(),
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

@JsonIgnoreProperties(ignoreUnknown = true)
data class TemplateData(
    val id: String = NanoIdUtils.randomNanoId(),
    var name: String,
    var enabledGeneratorIds: MutableList<String>? = null,
    var vcName: String? = null,
    var vcLimit: Int? = null,
    var vcBitrate: Int? = null,
    var vcRegion: String? = null
) {
    fun validate() : Boolean {
        return name.isNotEmpty()
                && enabledGeneratorIds?.all { it.isValidSnowflake() } ?: true
                && vcName?.length?.let { it in 2..100 } ?: true
                && vcLimit?.let { it in 0..99 } ?: true
                && vcBitrate?.let { it in 8000..384000 } ?: true
                && vcRegion?.let { it in Region.values().map { region -> region.key } } ?: true
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeneratorData(
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
    var autoWaiting: Boolean = false,

    var chatCategory: String? = category,
    var chatTopic: String? = "Temporary text chat made by Astro | ${Links.WEBSITE}",
    var chatNsfw: Boolean = false,
    var chatSlowmode: Int = 0,
    var chatPermissionsInherited: PermissionsInherited = PermissionsInherited.NONE,
    var defaultChatName: String = "{vc_name}",
    var defaultChatText: String? = null,
    var defaultChatTextEmbed: Boolean = true,
    var chatInterface: Int = -1,

    var waitingCategory: String? = category,
    var waitingPermissionsInherited: PermissionsInherited = PermissionsInherited.NONE,
    var defaultWaitingName: String = "Waiting for {vc_name}",
    var waitingBitrate: Int = 0,
    var waitingPosition: InitialPosition = InitialPosition.BEFORE,
    var waitingUserLimit: Int = 0
) {
    fun validate() : Boolean {
        return id.isValidSnowflake()
                && fallbackId?.isValidSnowflake() ?: true
                && defaultName.length in 2..500
                && defaultLockedName?.length?.let { it in 2..500 } ?: true
                && defaultHiddenName?.length?.let { it in 2..500 } ?: true
                && userLimit in 0..99
                && bitrate in 0..384000
                && category?.isValidSnowflake() ?: true
                && permissionsTargetRole?.isValidSnowflake() ?: true
                && permissionsImmuneRole?.isValidSnowflake() ?: true
                && ownerRole?.isValidSnowflake() ?: true
                && commandsSettings.validate()
                && chatCategory?.isValidSnowflake() ?: true
                && chatTopic?.length?.let { it in 0.. TextChannel.MAX_TOPIC_LENGTH } ?: true
                && chatSlowmode in 0..TextChannel.MAX_SLOWMODE
                && defaultChatName.length in 2..500
                && defaultChatText?.length?.let { it in 0..(if (defaultChatTextEmbed) MessageEmbed.DESCRIPTION_MAX_LENGTH else MessageEmbed.TEXT_MAX_LENGTH) } ?: true
                && waitingCategory?.isValidSnowflake() ?: true
                && defaultWaitingName.length in 2..500
                && waitingBitrate in 0..384000
                && waitingUserLimit in 0..99
    }
}


enum class PermissionsInherited {
    GENERATOR, CATEGORY, NONE;

    override fun toString(): String {
        return when (this) {
            GENERATOR -> "From the generator"
            CATEGORY -> "From the category (in which they get generated)"
            NONE -> "Do not inherit any permission"
        }
    }
}

enum class InitialPosition {
    BEFORE, AFTER, BOTTOM;

    override fun toString(): String {
        return when (this) {
            BEFORE -> "Before the generator"
            AFTER -> "After the generator"
            BOTTOM -> "Bottom of the category"
        }
    }
}

/**
 * @param permissionDenied the permission that should be denied when this state is applied
 * @param permissionReset the permissions that should be reset when this state is applied
 */
enum class VCState(
    val permissionDenied: Permission? = null,
    val permissionReset: Permission? = null
) {
    UNLOCKED(null, Permission.VOICE_CONNECT),
    LOCKED(Permission.VOICE_CONNECT),
    HIDDEN(Permission.VIEW_CHANNEL),
    UNHIDDEN(null, Permission.VIEW_CHANNEL);
}


@JsonIgnoreProperties(ignoreUnknown = true)
data class InterfaceData(
    var channelID: String,
    var messageID: String,
    var buttons: MutableList<InterfaceButton> = mutableListOf(),

    @Deprecated("Use buttons property instead")
    var actions: MutableList<InterfaceAction> = mutableListOf(),

    val embedStyle: EmbedStyle = EmbedStyle(),
    var generateEmbedFields: Boolean = false
) {
    fun asMarkdownLink(guildID: String) = "interface".asMessageMarkdownLink(guildID, channelID, messageID)

    fun validate() : Boolean {
        return channelID.isValidSnowflake()
                && messageID.isValidSnowflake()
                && buttons.all { it.validate() }
                && embedStyle.validate()
    }
}

data class EmbedStyle(
    var url: String? = Links.WEBSITE,
    var title: String? = null,
    var description: String? = "You can use this interface to manage your voice channel.\nYou can also use `/vc` slash commands!",
    var timestamp: Long? = null,
    var color: Int = Colors.purple.rgb,
    var thumbnail: String? = null,
    var image: String? = Links.INTERFACE_BUTTONS_IMAGE,
    var authorName: String? = "Astro Interface",
    var authorUrl: String? = Links.WEBSITE,
    var authorIconUrl: String? = Links.LOGO,
    var footer: String? = "Use the buttons below to manage your voice channel",
    var footerIconUrl: String? = null
) {
    fun validate() : Boolean {
        return url?.length?.let { it < MessageEmbed.URL_MAX_LENGTH } ?: true
                && title?.length?.let { it < MessageEmbed.TITLE_MAX_LENGTH } ?: true
                && description?.length?.let { it < MessageEmbed.DESCRIPTION_MAX_LENGTH } ?: true
                && thumbnail?.length?.let { it < MessageEmbed.URL_MAX_LENGTH } ?: true
                && image?.length?.let { it < MessageEmbed.URL_MAX_LENGTH } ?: true
                && authorName?.length?.let { it < MessageEmbed.AUTHOR_MAX_LENGTH } ?: true
                && authorUrl?.length?.let { it < MessageEmbed.URL_MAX_LENGTH } ?: true
                && authorIconUrl?.length?.let { it < MessageEmbed.URL_MAX_LENGTH } ?: true
                && footer?.length?.let { it < MessageEmbed.TEXT_MAX_LENGTH } ?: true
                && footerIconUrl?.length?.let { it < MessageEmbed.URL_MAX_LENGTH } ?: true
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
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

@JsonIgnoreProperties(ignoreUnknown = true)
data class InterfaceButton(
    var id: String,
    var name: String? = null,
    var emoji: String? = null,
    var buttonStyleKey: Int = ButtonStyle.SECONDARY.key,
    var buttonDisabled: Boolean = false,
    var position: Pair<Int, Int>,
    var fieldValue: String = id
) {
    fun validate() : Boolean {
        return id.isValidSnowflake()
                && position.first in 0..4
                && position.second in 0..4
    }

    companion object {
        fun fromButton(button: Button, position: Pair<Int, Int>) = InterfaceButton(
            id = button.id!!.plus("?interface=true"),
            name = button.label,
            emoji = button.emoji?.formatted,
            buttonStyleKey = button.style.key,
            false,
            position = position,
        )
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ConnectionData(
    var id: String,
    var roleID: String,
    var action: ConnectionAction = ConnectionAction.ASSIGN
) {
    fun validate() : Boolean {
        return true // TODO
    }

    override fun toString(): String {
        val joinActionName = when (action) {
            ConnectionAction.ASSIGN -> "receive"
            ConnectionAction.REMOVE -> "loose"
            ConnectionAction.TOGGLE -> "get toggled"
        }
        val leaveActionName = when (action) {
            ConnectionAction.ASSIGN -> "removed"
            ConnectionAction.REMOVE -> "back"
            ConnectionAction.TOGGLE -> "toggled back"
        }

        return "Users will $joinActionName the ${roleID.asRoleMention()} role when joining ${id.asChannelMention()}" +
            " and they will ${if (action.permanent) "__not__ " else ""}get that role $leaveActionName" +
            " when they leave the channel." +
            "\n" +
            "\n**Summary**" +
            "\n> **Channel** > ${id.asChannelMention()}" +
            "\n> **Role** > ${roleID.asRoleMention()}" +
            "\n> **Action** > ${action.name.lowercaseAndCapitalize()}" +
            "\n> **Permanent** > ${action.permanent.asTrueOrFalse()}"
    }
}

enum class ConnectionAction(
    var permanent: Boolean = false
) {
    ASSIGN, REMOVE, TOGGLE;

    override fun toString(): String = when (this) {
        ASSIGN -> "Assign the role"
        REMOVE -> "Remove the role"
        TOGGLE -> "Toggle the role (assign if missing, remove if present)"
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class CommandsSettings(
    var maxUserLimit: Int = 99,
    var minUserLimit: Int = 0,

    var maxBitrate: Int? = null,
    var minBitrate: Int = 8000,

    var badwordsAllowed: Boolean = true,
) {
    fun validate() : Boolean {
        // TODO: Return invalid message for all validate functions so that the user understands what's not valid
        return maxUserLimit in 0..99
                && minUserLimit in 0..99
                && maxBitrate?.let { it in 8000..384000 } ?: true
                && minBitrate in 8000..384000
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class RenameConditions(
    var stateChange: Boolean = true,
    var ownerChange: Boolean = true,
    var renamed: Boolean = false,
    var activityChange: Boolean = true
)