package space.astro.please.bot.listeners

import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands
import mu.KotlinLogging
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import space.astro.shared.core.services.redis.RedisClientService
import javax.security.auth.login.LoginException

private val log = KotlinLogging.logger {  }

@Component
class ContextRefreshedListeners(
    private val redisClientService: RedisClientService
) {
    @EventListener
    @Throws(LoginException::class)
    fun applicationStarted(event: ContextRefreshedEvent) {
        log.info { "Running Redis query" }
        val test = redisClientService.syncCommands().get("test")
        log.info { "got redis value test=$test (should not exist)" }
    }
}