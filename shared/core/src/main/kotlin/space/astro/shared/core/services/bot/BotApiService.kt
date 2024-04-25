package space.astro.shared.core.services.bot

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import space.astro.shared.core.configs.BotApiConfig
import space.astro.shared.core.configs.WebClientConfig
import space.astro.shared.core.models.database.GeneratorData
import space.astro.shared.core.models.database.InterfaceData
import space.astro.shared.core.util.exceptions.BotApiPermissionException
import java.time.Duration

@Service
class BotApiService(
    webClientConfig: WebClientConfig,
    botApiConfig: BotApiConfig,
    private val objectMapper: ObjectMapper
) {
    private final val provider: ConnectionProvider =
        ConnectionProvider.builder("support-bot-service-provider")
            //.maxConnections(webClientConfig.httpMaxConnections)
            .maxIdleTime(Duration.ofSeconds(webClientConfig.httpMaxIdleTime))
            .maxLifeTime(Duration.ofSeconds(webClientConfig.httpMaxLifeTime))
            //.pendingAcquireTimeout(Duration.ofSeconds(webClientConfig.httpPendingAcquireTimeout))
            .evictInBackground(Duration.ofSeconds(webClientConfig.httpEvictInBackground))
            .build()

    private val webClient: WebClient = WebClient.builder()
        .clientConnector(ReactorClientHttpConnector(HttpClient.create(provider)))
        .codecs { configurer ->
            val codecs = configurer.defaultCodecs()
            codecs.jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper))
            codecs.jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper))
        }
        .defaultHeader("Authorization", botApiConfig.auth)
        .build()

    /**
     * Requests the bot to create a generator
     *
     * @param endpoint
     * @param guildID
     *
     * @throws [BotApiPermissionException] if bot doesn't have permissions to create the generator
     * @throws [Throwable] for unknown issues
     * @return the [GeneratorData] of the created generator if successful
     */
    suspend fun createGenerator(
        endpoint: String,
        guildID: String
    ): GeneratorData {
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("$endpoint/api/${guildID}/generator/create")
                    .build()
            }
            .retrieve()
            .onStatus(
                { it == HttpStatus.METHOD_NOT_ALLOWED },
                { throw BotApiPermissionException("bot doesn't have permissions to create the generator")}
            )
            .onStatus(
                { it != HttpStatus.OK },
                { throw Throwable("${it.statusCode()} - Unexpected Response") })
            .awaitBody()
    }

    /**
     * Requests the bot to create an interface
     *
     * @param endpoint
     * @param guildID
     * @param channelID
     *
     * @throws [BotApiPermissionException] if bot doesn't have permissions to create the inteface
     * @throws [Throwable] for unknown issues
     * @return the [InterfaceData] of the created interface if successful
     */
    suspend fun createInterface(
        endpoint: String,
        guildID: String,
        channelID: String
    ): InterfaceData {
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("$endpoint/api/${guildID}/interface/create/${channelID}")
                    .build()
            }
            .retrieve()
            .onStatus(
                { it == HttpStatus.METHOD_NOT_ALLOWED },
                { throw BotApiPermissionException("bot doesn't have permissions to create the generator")}
            )
            .onStatus(
                { it != HttpStatus.OK },
                { throw Throwable("${it.statusCode()} - Unexpected Response") })
            .awaitBody()
    }

    /**
     * Requests the bot to update an interface
     *
     * @param endpoint
     * @param guildID
     * @param interfaceData
     *
     * @throws [BotApiPermissionException] if bot doesn't have permissions to update the interface
     * @throws [Throwable] for unknown issues
     */
    suspend fun updateInterface(
        endpoint: String,
        guildID: String,
        interfaceData: InterfaceData
    ) {
        return webClient.post()
            .uri { uriBuilder ->
                uriBuilder.path("$endpoint/api/${guildID}/interface/update")
                    .build()
            }
            .bodyValue(interfaceData)
            .retrieve()
            .onStatus(
                { it == HttpStatus.METHOD_NOT_ALLOWED },
                { throw BotApiPermissionException("bot doesn't have permissions to create the generator")}
            )
            .onStatus(
                { it != HttpStatus.OK },
                { throw Throwable("${it.statusCode()} - Unexpected Response") })
            .awaitBody()
    }
}