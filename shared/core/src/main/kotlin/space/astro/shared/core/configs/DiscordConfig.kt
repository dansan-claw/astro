package space.astro.shared.core.configs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration()
@ConfigurationProperties("discord.api")
class DiscordConfig {

    val baseUrl: String = "https://discord.com/api"
}
