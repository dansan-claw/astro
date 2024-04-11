package space.astro.bot.controllers.discord

import net.dv8tion.jda.api.sharding.ShardManager
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ShardsController(
    private val shardManager: ShardManager
) {
    @GetMapping("/shards")
    suspend fun shards(): ResponseEntity<*> {
        // TODO: Figure out the data to provide
        return ResponseEntity.noContent().build<Any>()
    }
}