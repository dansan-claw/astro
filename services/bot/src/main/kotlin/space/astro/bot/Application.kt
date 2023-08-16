package space.astro.bot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication(
    scanBasePackages = [
        "space.astro.bot",
        "space.astro.shared.core.configs",
        "space.astro.shared.core.services",
        "space.astro.shared.core.components",
    ]
)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}