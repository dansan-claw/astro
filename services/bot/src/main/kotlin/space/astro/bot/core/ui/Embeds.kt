package space.astro.bot.core.ui

import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import space.astro.shared.core.util.extention.linkFromLink
import space.astro.shared.core.util.ui.Colors
import space.astro.shared.core.util.ui.Links

object Embeds {
    const val footer = "Use /help for support"

    fun default(description: String): MessageEmbed {
        return Embed(
            color = Colors.purple.rgb,
            description = description,
            footerText = footer
        )
    }

    fun error(description: String): MessageEmbed {
        return Embed(
            color = Colors.red.rgb,
            description = description,
            footerText = footer
        )
    }

    ////////////
    /// HELP ///
    ////////////

    val helpGeneral = Embed {
        color = Colors.purple.rgb
        title = "Help - General"
        description = "Astro is the most complete and unique bot for temporary voice channels and voice roles!" +
                "\n\n**Server requirements**" +
                "\n• Your server must not have more than 50 bots, this is a limit set by Discord" +
                "\n• You need the `Manage channels` permission to configure Astro for your server" +
                "\n\n**Bot permissions**" +
                "\nAstro requires `Administrator` permissions by default to work and ensure a great user experience for the average Discord users." +
                "\n\nIf you own a professional server and cannot give that permission to Astro for security reasons, you can disable this requirement via the command `/settings admin-permission`." +
                "\nYou may ask for guidelines in the [Support Server](${Links.SUPPORT_SERVER}) regarding the permissions that Astro needs in order to work, but in depth support is not provided for that." +
                "\n\n**Command permissions**" +
                "\nTo manage which roles or users can use specific Astro commands you can use [Discord command permissions](${Links.ExternalGuides.COMMAND_PERMISSIONS})." +
                "\n\n**See reported errors**" +
                "\nYou can view all the errors that Astro encountered when working in your server with the command `/settings errors`." +
                "\nIf something doesn't seem to work properly always make sure to check those errors to make sure the issue is not on your end."
        footer {
            name = "For help regarding other features of Astro see the buttons below or /help commands"
        }
    }

    val helpPremium = Embed(
        description = "todo"
    )

    val helpVariables = Embed(
        description = "todo"
    )

    val helpGenerators = Embed(
        description = "todo"
    )

    val helpInterfaces = Embed(
        description = "todo"
    )

    val helpTemplates = Embed(
        description = "todo"
    )

    val helpConnections = Embed(
        description = "todo"
    )

    /*
    fun dashboardSettings() : MessageEmbed {
        return Embed(
            color = Colors.purple.rgb,
            description = "You can manage Astro's settings on its ${"dashboard".linkFromName(Links.DASHBOARD)}!"
        )
    }
     */


    ////////////////////
    /// PREDASHBOARD ///
    ////////////////////

    val canceled = EmbedBuilder()
        .setColor(Colors.red)
        .setDescription("The action has been canceled so nothing has been modified")
        .setFooter(footer)
        .build()

    val timeExpired = EmbedBuilder()
        .setColor(Colors.red)
        .setDescription("You took too long to complete this action")
        .setFooter(footer)
        .build()

    fun selector(description: String) = EmbedBuilder()
        .setColor(Colors.purple)
        .setDescription(description)
        .setFooter(footer)
        .build()

    fun confirmation(description: String) = EmbedBuilder()
        .setColor(Colors.yellow)
        .setDescription(description)
        .setFooter(footer)
        .build()

    fun success(description: String): MessageEmbed {
        val builder = EmbedBuilder()
            .setColor(Colors.green)
            .setDescription(description)
            .setFooter(footer)

        return builder.build()
    }

    fun requireRoleHierarchy(roleName: String) = EmbedBuilder()
        .setColor(Colors.red)
        .setTitle("Cannot manage the role $roleName")
        .setDescription(
             "Make sure that the role you selected is below the Astro role in the *Server settings > Roles*" +
                    "\nFind out more about roles hierarchy [here](${Links.ExternalGuides.ROLE_HIERARCHY})."
        )
        .setFooter(footer)
        .build()
}