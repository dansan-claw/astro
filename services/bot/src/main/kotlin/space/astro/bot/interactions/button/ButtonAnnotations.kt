package space.astro.bot.interactions.button

import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import org.springframework.stereotype.Component
import space.astro.bot.interactions.InteractionAction

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Component
annotation class Button(
    val id: String = "",
    val style: ButtonStyle = ButtonStyle.PRIMARY,
    val action: InteractionAction = InteractionAction.GENERIC
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ButtonRunnable