package space.astro.shared.core.models.database

import com.fasterxml.jackson.annotation.JsonProperty
import space.astro.shared.core.models.database.VCState

data class TemporaryVCDto(
    val id: String,
    @JsonProperty("ownerID")
    var ownerId: String,
    @JsonProperty("genID")
    val generatorId: String,

    var state: VCState = VCState.UNLOCKED,

    var nameChanges: Int = 0,
    var lastNameChange: Long? = null,
    var renamed: Boolean = false,

    var position: Int? = null,

    var chatID: String?,
    var chatLogs: Boolean = false,
    var chatNameChanges: Int = 0,
    var lastChatNameChange: Long? = null,
)