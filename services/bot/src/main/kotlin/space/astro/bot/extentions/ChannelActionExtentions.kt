package space.astro.bot.extentions

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.IPermissionHolder
import net.dv8tion.jda.api.entities.PermissionOverride
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import net.dv8tion.jda.api.requests.restaction.ChannelAction

fun<T: GuildChannel> ChannelAction<T>.modifyPermissionOverride(
    permissionOverride: PermissionOverride?,
    target: IPermissionHolder,
    allow: List<Permission>,
    deny: List<Permission>
): ChannelAction<T> {
    var allowedPermissions = permissionOverride?.allowedRaw ?: 0L
    var deniedPermission = permissionOverride?.deniedRaw ?: 0L

    allow.forEach {
        allowedPermissions = allowedPermissions or it.rawValue
        deniedPermission = deniedPermission and it.rawValue.inv()
    }

    deny.forEach {
        deniedPermission = deniedPermission or it.rawValue
        allowedPermissions = allowedPermissions and it.rawValue.inv()
    }

    return addPermissionOverride(target, allowedPermissions, deniedPermission);
}

fun<T: GuildChannel> ChannelAction<T>.modifyPermissionOverride(
    permissionOverride: PermissionOverride?,
    target: IPermissionHolder,
    allow: Long,
    deny: Long
): ChannelAction<T> {
    var allowedPermissions = permissionOverride?.allowedRaw ?: 0L
    var deniedPermission = permissionOverride?.deniedRaw ?: 0L

    allowedPermissions = allowedPermissions or allow
    deniedPermission = deniedPermission and allow.inv()

    deniedPermission = deniedPermission or deny
    allowedPermissions = allowedPermissions and deny.inv()

    return addPermissionOverride(target, allowedPermissions, deniedPermission);
}