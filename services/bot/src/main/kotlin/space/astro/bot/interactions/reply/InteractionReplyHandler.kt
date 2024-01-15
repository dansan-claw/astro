package space.astro.bot.interactions.reply

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.events.await
import kotlinx.coroutines.withTimeoutOrNull
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback
import net.dv8tion.jda.api.interactions.callbacks.IPremiumReplyCallback
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import net.dv8tion.jda.api.interactions.modals.Modal
import net.dv8tion.jda.api.sharding.ShardManager
import space.astro.bot.core.ui.Buttons
import space.astro.bot.core.ui.Embeds

class InteractionReplyHandler(
    private var replyCallback: IReplyCallback,
    private var modalCallback: IModalCallback?,
    private var premiumReplyCallback: IPremiumReplyCallback?,
    private val originatedFromInterface: Boolean,
    private val originatedFromExistingMessage: Boolean,
    private val shardManager: ShardManager
) : IInteractionReplyHandler {

    /////////////
    /// LOGIC ///
    /////////////
    private var ephemeralLocked = false
    private var deferringLocked = false
    private var sentFirstMessage = false

    /**
     * Tracks that the bot is replying to the interaction
     *
     * @return whether the bot should edit a previous reply or create a new one
     */
    private fun trackReply(): Boolean {
        val shouldEdit = if (originatedFromInterface) {
            sentFirstMessage
        } else {
            sentFirstMessage
        }

        sentFirstMessage = true
        ephemeralLocked = true
        deferringLocked = true

        return shouldEdit
    }

    private var interactionHook: InteractionHook = replyCallback.hook
    private var ephemeral: Boolean = true


    /////////////////
    /// CALLBACKS ///
    /////////////////
    override fun setCallbacks(
        replyCallback: IReplyCallback,
        modalCallback: IModalCallback?,
        premiumReplyCallback: IPremiumReplyCallback?
    ) {
        this.replyCallback = replyCallback
        this.modalCallback = modalCallback
        this.premiumReplyCallback = premiumReplyCallback
        sentFirstMessage = false
        interactionHook = replyCallback.hook
        ephemeralLocked = false
        deferringLocked = false
    }


    ///////////////////////////////
    /// EPHEMERAL AND DEFERRING ///
    ///////////////////////////////

    override fun setEphemeral(ephemeral: Boolean) {
        if (ephemeralLocked) {
            throw IllegalStateException("Trying to change ephemeral state on an interaction that was already replied to")
        }

        this.ephemeral = ephemeral
    }


    override suspend fun deferReply() {
        if (deferringLocked) {
            throw IllegalStateException("Trying to defer a reply on an interaction that was already replied to")
        }

        trackReply()
        replyCallback.deferReply(ephemeral).await()
    }


    ////////////////////
    /// FULL REPLIES ///
    ////////////////////

    override suspend fun reply(
        embed: MessageEmbed,
        components: List<LayoutComponent>
    ) {
        val shouldEdit = trackReply()

        if (!shouldEdit) {
            interactionHook = replyCallback.replyEmbeds(embed)
                .setComponents(components)
                .setEphemeral(ephemeral)
                .await()
        } else {
            interactionHook.editOriginalEmbeds(embed)
                .setComponents(components)
                .await()
        }
    }


    //////////////
    /// EMBEDS ///
    //////////////

    override suspend fun replyEmbed(embed: MessageEmbed) {
        reply(embed, emptyList())
    }

    override suspend fun replyEmbedAndComponent(embed: MessageEmbed, component: ItemComponent) {
        reply(embed, listOf(ActionRow.of(component)))
    }

    override suspend fun replyEmbedAndComponents(embed: MessageEmbed, components: List<ItemComponent>) {
        reply(embed, components.chunked(5).map { ActionRow.of(it) })
    }


    /////////////
    /// MODAL ///
    /////////////
    override suspend fun replyModal(modal: Modal) {
        if (modalCallback == null) {
            throw IllegalStateException("This interaction doesn't support modal replies")
        }

        modalCallback?.replyModal(modal)?.await()
    }


    ///////////////
    /// PREMIUM ///
    ///////////////

    override suspend fun replyPremiumRequired() {
        if (premiumReplyCallback == null) {
            throw IllegalStateException("This interaction doesn't support premium required replies")
        }

        premiumReplyCallback?.replyWithPremiumRequired()?.await()
    }


    //////////////////
    /// COMPONENTS ///
    //////////////////
    override suspend fun editComponents(components: List<LayoutComponent>) {
        val shouldEdit = trackReply()

        if (!shouldEdit) {
            throw IllegalStateException("Tried to edit components on an interaction that has not been replied to yet")
        }

        interactionHook.editOriginalComponents(components).await()
    }


    ////////////////////
    /// PREDASHBOARD ///
    ////////////////////
    private var timeoutAmount: Long = 60000

    override fun setTimeoutAmount(timeoutAmount: Long) {
        this.timeoutAmount = timeoutAmount
    }

    override suspend fun replyWithConfirmation(
        description: String,
        isDangerous: Boolean,
        onConfirmation: suspend () -> Unit
    ) {
        val buttons = Buttons.Bundles.confirmation(isDangerous)
        replyEmbedAndComponents(
            embed = Embeds.confirmation(description),
            components = buttons
        )

        withTimeoutOrNull(timeoutAmount) {
            return@withTimeoutOrNull shardManager.await<ButtonInteractionEvent> {
                it.componentId == buttons[0].id!! || it.componentId == buttons[1].id!!
            }
        }?.let { event ->
            setCallbacks(event, event, event)

            if (event.componentId == buttons[1].id)
                onConfirmation()
            else
                replyEmbed(Embeds.canceled)
        } ?: replyEmbed(Embeds.timeExpired)
    }

    override suspend fun replyWithButtons(
        embed: MessageEmbed,
        buttons: List<Button>,
        onClick: (suspend (buttonId: String) -> Unit)?
    ) {
        replyEmbedAndComponents(embed = embed, components = buttons)

        if (onClick != null) {
            val buttonsIds = buttons.mapNotNull { it.id }

            withTimeoutOrNull(timeoutAmount) {
                return@withTimeoutOrNull shardManager.await<ButtonInteractionEvent> {
                    it.componentId in buttonsIds
                }
            }?.let { event ->
                setCallbacks(event, event, event)
                onClick(event.componentId)
            } ?: replyEmbed(Embeds.timeExpired)
        }
    }

    override suspend fun replyWithOptionalButtons(
        embed: MessageEmbed,
        buttons: List<Button>,
        onClick: suspend (buttonId: String?) -> Unit
    ) {
        replyEmbedAndComponents(embed = embed, components = buttons)

        val buttonsIds = buttons.mapNotNull { it.id }
        withTimeoutOrNull(timeoutAmount) {
            return@withTimeoutOrNull shardManager.await<ButtonInteractionEvent> {
                it.componentId in buttonsIds
            }
        }.let { event ->
            if (event != null) {
                setCallbacks(event, event, event)
            }

            onClick(event?.componentId)
        }
    }

    override suspend fun replyWithSelectMenu(
        embed: MessageEmbed,
        selectMenu: StringSelectMenu,
        withCancelButton: Boolean,
        onSelect: suspend (values: List<String>) -> Unit
    ) {
        if (withCancelButton) {
            val cancelButton = Buttons.cancel()

            reply(
                embed = embed,
                components = listOf(ActionRow.of(selectMenu), ActionRow.of(cancelButton))
            )

            withTimeoutOrNull(timeoutAmount) {
                return@withTimeoutOrNull shardManager.await<GenericComponentInteractionCreateEvent> {
                    (it is StringSelectInteractionEvent && it.componentId == selectMenu.id)
                            || (it is ButtonInteractionEvent && it.componentId == cancelButton.id!!)
                }
            }?.let { event ->
                setCallbacks(event, event, event)

                if (event is StringSelectInteractionEvent)
                    onSelect(event.values)
                else
                    replyEmbed(Embeds.canceled)
            } ?: replyEmbed(Embeds.timeExpired)
        } else {
            replyEmbedAndComponent(embed = embed, selectMenu)

            withTimeoutOrNull(timeoutAmount) {
                return@withTimeoutOrNull shardManager.await<StringSelectInteractionEvent> {
                    it.componentId == selectMenu.id
                }
            }
                ?.let { event ->
                    setCallbacks(event, event, event)

                    onSelect(event.values)
                }
                ?: replyEmbed(Embeds.timeExpired)
        }
    }

    override suspend fun replyWithModel(
        modal: Modal,
        onFill: suspend (event: ModalInteractionEvent) -> Unit
    ) {
        replyModal(modal)

        withTimeoutOrNull(600000) {
            return@withTimeoutOrNull shardManager.await<ModalInteractionEvent> {
                it.modalId == modal.id
            }
        }?.let { event ->
            setCallbacks(event, null, null)
            onFill(event)
        }
    }
}