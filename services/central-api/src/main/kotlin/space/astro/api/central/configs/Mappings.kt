package space.astro.api.central.configs

object Mappings {
    object Kube {
        const val READY = "/ready"
        const val SHUTDOWN = "/shutdown"
    }

    object Status {
        const val STATUS = "/status"
    }

    object Chargebee {
        object Prefixes {
            const val EVENT = "/chargebee/event"
        }

        const val PORTAL_SESSION = "/chargebee/portal-session"
        const val EVENT_SUB_CREATE = "${Prefixes.EVENT}/sub/create"
        const val EVENT_SUB_CANCEL = "${Prefixes.EVENT}/sub/cancel"
    }

    object Dashboard {
        object Prefixes {
            const val DASHBOARD = "/dashboard"
            const val LOGIN = "${DASHBOARD}/auth/login"
        }
        const val LOGIN = "${Prefixes.LOGIN}/{code}"
        const val LOGOUT = "${Prefixes.DASHBOARD}/auth/logout"

        const val USERS_ME = "${Prefixes.DASHBOARD}/users/@me"

        const val GUILDS = "${Prefixes.DASHBOARD}/guilds"
        const val GUILD = "${GUILDS}/{guildID}"
        const val GUILD_CHANNELS = "${GUILD}/channels"
        const val GUILD_ROLES = "${GUILD}/roles"
    }
}