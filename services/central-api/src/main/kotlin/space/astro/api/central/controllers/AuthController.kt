package space.astro.api.central.controllers

import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import space.astro.api.central.models.OAuth2AuthorizationResponseDto
import space.astro.api.central.services.DiscordUserTokenFetchService
import space.astro.api.central.services.WebSessionService

val log = KotlinLogging.logger { }

@RestController
@RequestMapping("/auth")
class AuthController(
    val discordUserTokenFetchService: DiscordUserTokenFetchService,
    val webSessionService: WebSessionService
) {

    @GetMapping("/id/{code}")
    suspend fun authorizeDiscord(@PathVariable code: String): ResponseEntity<*> {
        log.info { "Authorizing discord user with code $code" }

        // fetch credentials + user from discord
        val authorizationWrapper = discordUserTokenFetchService.fetchCredentialsFromCode(code)

        val user = authorizationWrapper.user

        val sessionToken = webSessionService.createSession(user.id)

        val response = OAuth2AuthorizationResponseDto(
            sessionToken,
            user
        )

        log.info { "Successfully authorized discord user with id ${user.id} - response $response" }
        return ResponseEntity.ok(response)
    }
}