package space.astro.api.central.models.dashboard.body

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import space.astro.shared.core.util.extention.isValidSnowflake

@JsonIgnoreProperties(ignoreUnknown = true)
data class GuildDataInterfaceCreateBody(
    val channelID: String
) {
    fun validate() : Boolean {
        return channelID.isValidSnowflake()
    }
}