package space.astro.api.central

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication(
    scanBasePackages = [
        "space.astro.api.central",
        "space.astro.shared.core"
    ]
)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
