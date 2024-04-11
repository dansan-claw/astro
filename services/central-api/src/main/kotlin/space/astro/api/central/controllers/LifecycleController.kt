package space.astro.api.central.controllers

import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import space.astro.api.central.configs.Mappings

private val log = KotlinLogging.logger { }

@RestController
class LifecycleController {

    @GetMapping(Mappings.Kube.ready)
    suspend fun ready(@RequestHeader("Authorization") auth: String): ResponseEntity<*> {
        return ResponseEntity.noContent().build<Any>()
    }

    @GetMapping(Mappings.Kube.shutdown)
    suspend fun shutdown(@RequestHeader("Authorization") auth: String): ResponseEntity<*> {
        log.info("Got shutdown request - persisting players...")
        delay(3000)
        return ResponseEntity.noContent().build<Any>()
    }
}
