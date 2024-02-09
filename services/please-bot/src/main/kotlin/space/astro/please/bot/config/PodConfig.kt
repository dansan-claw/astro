package space.astro.please.bot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("pod")
class PodConfig {

    var hostname: String = "please-01.astro-bot.space"
    var ordinal: String = "please-0"

    fun getParsedOrdinal() : Int {
        return ordinal.split("-").last().toInt()
    }

}
