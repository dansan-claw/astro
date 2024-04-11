package space.astro.api.central.controllers.dashboard

import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import space.astro.api.central.controllers.getUserID

private val log = KotlinLogging.logger {  }

@RestController
@RequestMapping("/dashboard")
class DashboardGuildsController(

) {
    @GetMapping("/guilds")
    suspend fun getUserGuilds(
        exchange: ServerWebExchange
    ): ResponseEntity<*> {
        val userId = exchange.getUserID()

        return ResponseEntity.ok(null)
    }
}