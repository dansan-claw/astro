package space.astro.bot.managers.vc

sealed class AstroVCEvent {
    class JoinedGenerator(): AstroVCEvent()
    class JoinedTemporaryVC(): AstroVCEvent()
    class JoinedConnectedVC(): AstroVCEvent()
    class OwnerLeftTemporaryVC(): AstroVCEvent()
    class LeftTemporaryVC(): AstroVCEvent()
    class LeftConnectedVC(): AstroVCEvent()
}