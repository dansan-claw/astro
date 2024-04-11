package space.astro.api.central.configs

object Mappings {
    object Kube {
        const val ready = "/ready"
        const val shutdown = "/shutdown"
    }

    object Status {
        const val status = "/status"
    }

    object Chargebee {
        object Prefixes {
            const val event = "/chargebee/event"
        }

        const val portalSession = "/chargebee/portal-session"
        const val eventSubCreate = "${Prefixes.event}/sub/create"
        const val eventSubCancel = "${Prefixes.event}/sub/cancel"
    }

    object Dashboard {
        object Prefixes {
            const val login = "/dashboard/auth/login"
            const val dashboard = "/dashboard"
        }
        const val login = "${Prefixes.login}/{code}"
        const val logout = "${Prefixes.dashboard}/auth/logout"

        const val usersMe = "${Prefixes.dashboard}/users/@me"

        const val guilds = "${Prefixes.dashboard}/guilds"
        const val guild = "${Prefixes.dashboard}${guilds}/{guildID}"
        const val guildChannels = "${Prefixes.dashboard}${guild}/channels"
        const val guildRoles = "${Prefixes.dashboard}${guild}/roles"
    }
}