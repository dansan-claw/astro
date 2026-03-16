package space.astro.shared.core.util.ui

/**
 * Utility class for Astro most used links
 */
object Links {
    const val WEBSITE = "http://localhost:3001"
    const val INVITE = "http://localhost:3001"
    const val SUPPORT_SERVER = "https://discord.gg/yeXwVhg"
    const val ULTIMATE = "http://localhost:3001"
    const val APP_DIRECTORY_ULTIMATE = "https://discord.com/discovery/applications/715621848489918495/store/1096107722115661934"
    const val DASHBOARD = "http://localhost:3001"
    fun GUILD_DASHBOARD(guildId: String) = "http://localhost:3001/guilds/$guildId"
    const val GUIDES = "http://localhost:3001/guides"
    const val VOTE = "http://localhost:3001"
    const val GITHUB = "https://github.com/dansan-claw/astro"
    const val BLOG_ARTICLE = "https://github.com/dansan-claw/astro"

    const val LOGO = "https://cdn.discordapp.com/avatars/715621848489918495/dc0affdf8de07a3d88c4d192efad649f.png?size=2048"
    const val INTERFACE_BUTTONS_IMAGE = ""
    const val EXAMPLE_CUSTOM_INTERFACE = ""

    const val EXAMPLE_CONNECTION = ""

    object Guides {
        const val ALL = "http://localhost:3001/guides"
        const val BASIC = "http://localhost:3001/guides/basic"
        const val GENERATOR = "http://localhost:3001/guides/generator"
        const val TEMPLATE = "http://localhost:3001/guides/template"
        const val INTERFACE = "http://localhost:3001/guides/interface"
        const val VOICE_ROLE = "http://localhost:3001/guides/voice-role"
    }

    object ExternalGuides {
        const val GET_ID = "https://support.discord.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID-"
        const val ROLE_HIERARCHY = "https://support.discord.com/hc/en-us/articles/214836687-Role-Management-101"
        const val COMMAND_PERMISSIONS = "https://discord.com/blog/slash-commands-permissions-discord-apps-bots"
        const val PREMIUM_FAQ = "https://support.discord.com/hc/en-us/articles/9359445233303-Premium-App-Subscriptions-FAQ"
    }
}
