package space.astro.bot.managers.util

import org.springframework.stereotype.Component
import space.astro.bot.config.ApplicationFeaturesConfig
import space.astro.bot.config.DiscordApplicationConfig
import space.astro.shared.core.models.database.GuildDto

@Component
class PremiumRequirementDetector(
    val discordApplicationConfig: DiscordApplicationConfig,
    val applicationFeaturesConfig: ApplicationFeaturesConfig
) {
    fun isGuildPremium(guildDto: GuildDto): Boolean {
        val now = System.currentTimeMillis()

        return guildDto.upgradedByUserID != null
                || guildDto.entitlements.any { (it.endsAt == null || it.endsAt!! >= now) && it.skuId == discordApplicationConfig.premiumServerSkuId }
    }

    fun exceededMaximumGeneratorAmount(guildDto: GuildDto): Boolean {
        return applicationFeaturesConfig.premiumRestrictions
                && guildDto.generators.size > 2
                && !isGuildPremium(guildDto)
    }
}