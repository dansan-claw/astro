package space.astro.bot.managers.vc

import net.dv8tion.jda.api.entities.channel.ChannelType
import space.astro.shared.core.models.database.ConnectionDto

object VCEventDetector {

    /**
     * Detects any event related to Astro features that got triggered by a user moving between voice channels
     *
     * @param vcEventData all the necessary data about the voice event
     * @throws IllegalStateException when [vcEventData] doesn't have neither a [VCEventData.joinedChannel] or [VCEventData.leftChannel]
     * @return a [List] of [AstroVCEvent]s
     */
    fun detectAstroVoiceEvents(vcEventData: VCEventData): List<AstroVCEvent> {
        val events = mutableListOf<AstroVCEvent>()

        if (vcEventData.joinedChannel == null && vcEventData.leftChannel == null) {
            throw IllegalStateException("Received invalid astro voice event data: missing both joined and left audio channel")
        }

        // These two variables are needed for optimizing connection events calculation
        var joinedTemporaryVC: TemporaryVCData? = null
        var leftTemporaryVC: TemporaryVCData? = null
        var joinedConnection: ConnectionDto? = null
        var leftConnection: ConnectionDto? = null

        ///////////////////////////////
        /// TEMPORARY VC JOIN EVENT ///
        ///////////////////////////////
        vcEventData.joinedChannel
            ?.takeIf { it.type == ChannelType.VOICE }
            ?.also { joinedVC ->
                val indexOfJoinedGenerator = vcEventData.generators.firstOrNull { it.id == joinedVC.id }

                if (indexOfJoinedGenerator != null) {
                    events.add(AstroVCEvent.JoinedGenerator())
                } else {
                    vcEventData.temporaryVCs
                        .firstOrNull { it.id == joinedVC.id }
                        ?.also {
                            joinedTemporaryVC = it
                            events.add(AstroVCEvent.JoinedTemporaryVC())
                        }
                }
            }

        ///////////////////////////////
        /// TEMPORARY VC LEFT EVENT ///
        ///////////////////////////////
        vcEventData.leftChannel
            ?.takeIf { it.type == ChannelType.VOICE }
            ?.also { leftVC ->
                vcEventData.temporaryVCs
                    .firstOrNull { it.id == leftVC.id }
                    ?.also { temporaryVCData ->
                        leftTemporaryVC = temporaryVCData

                        events.add(
                            if (temporaryVCData.ownerId == vcEventData.userId)
                                AstroVCEvent.OwnerLeftTemporaryVC()
                            else
                                AstroVCEvent.LeftTemporaryVC()
                        )
                    }
            }

        ///////////////////////////////
        /// JOINED CONNECTION EVENT ///
        ///////////////////////////////
        /*
            Conditions to accept a connection as joined:
            - Joined channel is connected (normal or generator doesn't matter)
            - Joined channel is under connected category
            - Joined channel is a temporary VC generated from a connected generator
        */
        vcEventData.joinedChannel?.also { joinedVC ->
            vcEventData.connections
                .firstOrNull { connection ->
                    val isConnected = connection.id == joinedVC.id
                    val isNotGenerator =  vcEventData.generators.none { gen -> gen.id == joinedVC.id }
                    val parentIsConnected = connection.id == joinedVC.parentCategoryId
                    val generatorOfJoinedTemporaryVCIsConnected = connection.id == joinedTemporaryVC?.generatorId

                    isConnected || (isNotGenerator && (parentIsConnected || generatorOfJoinedTemporaryVCIsConnected))
                }?.also {
                    joinedConnection = it
                }
        }

        /////////////////////////////
        /// LEFT CONNECTION EVENT ///
        /////////////////////////////
        /*
            Conditions to accept a connection as left:
            - Left channel is connected and isn't a generator
            - Left channel is under a connected category and left channel isn't a generator
            - Left channel is a temporary VC generated from a connected generator
         */
        vcEventData.leftChannel
            ?.takeIf { vcEventData.generators.none { gen -> gen.id == it.id } }
            ?.also { leftVC ->
                vcEventData.connections
                    .firstOrNull {  connection ->
                        val isConnected = connection.id == leftVC.id
                        val parentIsConnected = connection.id == leftVC.parentCategoryId
                        val generatorOfLeftTemporaryVCIsConnected = connection.id == leftTemporaryVC?.generatorId

                        val hasNotJoinedTemporaryVCOfConnectedGenerator = connection.id == joinedTemporaryVC?.generatorId

                        (isConnected || parentIsConnected || generatorOfLeftTemporaryVCIsConnected)
                                && hasNotJoinedTemporaryVCOfConnectedGenerator
                    }?.also {
                        leftConnection = it
                    }
            }

        if (joinedConnection != null
            && leftConnection != null
            && (joinedConnection!!.id == leftConnection!!.id || joinedConnection!!.roleID == leftConnection!!.roleID)
        ) {
            joinedConnection = null
            leftConnection = null
        }

        if (joinedConnection != null) {
            events.add(AstroVCEvent.JoinedConnectedVC())
        }

        if (leftConnection != null) {
            events.add(AstroVCEvent.LeftConnectedVC())
        }

        return events
    }
}