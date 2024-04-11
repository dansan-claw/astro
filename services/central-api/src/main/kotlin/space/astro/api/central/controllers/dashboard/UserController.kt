package space.astro.api.central.controllers.dashboard

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import space.astro.api.central.configs.Mappings
import space.astro.api.central.controllers.getAccessToken
import space.astro.api.central.controllers.getUserID
import space.astro.api.central.daos.AuthedUsersDao
import space.astro.api.central.models.OAuth2AuthorizationResponseDto
import space.astro.api.central.models.OAuth2GuildInfo
import space.astro.api.central.services.DiscordUserService
import space.astro.api.central.services.DiscordUserTokenFetchService
import space.astro.api.central.services.DiscordUserTokenPersistenceService
import space.astro.api.central.services.WebSessionService

private val log = KotlinLogging.logger { }

@RestController
class UserController(
    val discordUserService: DiscordUserService,
) {
    @GetMapping(Mappings.Dashboard.usersMe)
    suspend fun getSelfUser(exchange: ServerWebExchange): ResponseEntity<*> {
        val accessToken = exchange.getAccessToken()

        try {
            val discordUser = discordUserService.fetchSelfUser(accessToken)
            return ResponseEntity.ok(discordUser)
        } catch (e: Throwable) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }
}