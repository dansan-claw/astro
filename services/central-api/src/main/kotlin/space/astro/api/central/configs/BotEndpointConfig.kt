package space.astro.api.central.configs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("bot.endpoint")
class BotEndpointConfig {
    val localhost: Boolean = true
    val port: Long = 9000
    val podName: String = "astro"
    val serviceName: String = "bot"
    val namespace: String = "astro"
}
