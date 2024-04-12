package space.astro.api.central.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import space.astro.api.central.configs.Mappings

@RestController

class StatusController {
    @GetMapping(Mappings.Status.STATUS)
    suspend fun status(): ResponseEntity<*> {
        // TODO: Figure out how to fetch shards from all pods
        return ResponseEntity.noContent().build<Any>()
    }
}