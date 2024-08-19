package space.astro.support.bot.components.web

import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import space.astro.shared.core.configs.KubeConfig
import space.astro.shared.core.configs.SupportBotApiConfig

@Component
class CustomWebFilter(
    private val supportBotApiConfig: SupportBotApiConfig,
    private val kubeConfig: KubeConfig
) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        val response = exchange.response
        val requestPath = request.path.toString()

        response.headers.set("Access-Control-Allow-Origin", supportBotApiConfig.originUrl)
        response.headers.set("Access-Control-Allow-Methods", "*")
        response.headers.set("Access-Control-Allow-Headers", "*")

        // chain preflight requests
        if (request.method == HttpMethod.OPTIONS) {
            return chain.filter(exchange)
        }

        if (requestPath.startsWith("/ready") || requestPath.startsWith("/shutdown")) {
            return mono {
                val authHeader = request.headers["Authorization"]?.get(0)
                    ?: run {
                        response.statusCode = HttpStatus.UNAUTHORIZED
                        return@mono null
                    }

                if (authHeader != kubeConfig.lifecycleAuthorization) {
                    response.statusCode = HttpStatus.UNAUTHORIZED
                    return@mono null
                }

                chain.filter(exchange).awaitSingleOrNull()
            }
        }

        return mono {
            val authHeader = request.headers["Authorization"]?.get(0)
            if (authHeader == null || authHeader != supportBotApiConfig.auth) {
                response.statusCode = HttpStatus.UNAUTHORIZED
                return@mono null
            }

            chain.filter(exchange).awaitSingleOrNull()
        }
    }
}