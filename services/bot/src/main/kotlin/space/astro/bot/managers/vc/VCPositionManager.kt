package space.astro.bot.managers.vc

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import space.astro.shared.core.models.database.GeneratorDto
import space.astro.shared.core.models.database.InitialPosition
import space.astro.shared.core.models.database.TemporaryVCDto

object VCPositionManager {
    /**
     * Minimum distance between two temporary VCs
     * Set to two because it needs to leave a space for waiting rooms
     */
    private val rawPositionsMinimumDistance = 2

    private fun getRawPositionWithoutPositionalData(
        generator: GeneratorDto, 
        generatorVC: VoiceChannel
    ): Int? {
        // If the temporary VC gets generated in a different category than the one of the generator
        // then no position gets calculated
        if (generator.category != generatorVC.parentCategoryId) {
            return null
        }

        val generatorPosition = generatorVC.positionRaw

        return when (generator.initialPosition) {
            InitialPosition.BOTTOM -> null
            InitialPosition.BEFORE -> {
                generatorPosition - rawPositionsMinimumDistance
            }
            InitialPosition.AFTER -> {
                generatorPosition + rawPositionsMinimumDistance
            }
        }?.coerceAtLeast(0)
    }

    private fun getRawPositionWithIncrementalPosition(
        incrementalPosition: Int,
        generator: GeneratorDto,
        generatorVC: VoiceChannel
    ): Int {
        val defaultRawPosition = getRawPositionWithoutPositionalData(generator, generatorVC)
            ?: 0

        return defaultRawPosition + (rawPositionsMinimumDistance * incrementalPosition)
    }

    /**
     * Gets the first available incremental position for a specific generator
     *
     * @param generatorId id of the generator
     * @param excludedVCId id of a channel to exclude from the search, usually the existing channel of which we need to recalculate the position
     * @param temporaryVCs list of existing temporary VCs
     */
    fun getIncrementalPosition(generatorId: String, excludedVCId: String?, temporaryVCs: List<TemporaryVCDto>): Int {
        var incrementalPosition = 1

        val positions = temporaryVCs.filter {
            it.generatorId == generatorId
                    && it.id != excludedVCId
                    && it.position != null
        }
            .mapNotNull { it.position }
            .toSortedSet()

        for (position in positions) {
            if (incrementalPosition != position)
                break
            incrementalPosition++
        }

        return incrementalPosition
    }

    /**
     * Gets the raw discord position of a temporary voice channel
     * based on the [incrementalPosition]
     *
     * When [incrementalPosition] is null it will calculate the best position based on the [generator] data
     * otherwise it will search for the correct position based on the other temporary VCs
     * which are supposed to use incremental ordering already
     */
    fun getRawPosition(incrementalPosition: Int?, generator: GeneratorDto, generatorVC: VoiceChannel): Int? {
        return if (incrementalPosition == null) {
            getRawPositionWithoutPositionalData(generator, generatorVC)
        } else {
            getRawPositionWithIncrementalPosition(incrementalPosition, generator, generatorVC)
        }
    }
}