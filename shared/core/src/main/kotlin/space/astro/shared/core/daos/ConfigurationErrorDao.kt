package space.astro.shared.core.daos

import com.influxdb.client.QueryApi
import com.influxdb.client.WriteApi
import com.influxdb.client.write.Point
import com.influxdb.query.FluxTable
import mu.KotlinLogging
import org.springframework.stereotype.Repository
import space.astro.shared.core.components.influx.InfluxConfig
import space.astro.shared.core.models.influx.ConfigurationErrorData
import space.astro.shared.core.models.influx.ConfigurationErrorWithTimeData

private val log = KotlinLogging.logger {  }

@Repository
class ConfigurationErrorDao(
    private val influxConfig: InfluxConfig,
    private val influxQueryApi: QueryApi,
    private val influxWriteApi: WriteApi
) {
    fun get(guildID: String, lookback: String = "-7d"): List<ConfigurationErrorWithTimeData> {
        val query = "from(bucket:\"${influxConfig.bucket}\") " +
                "|> range(start: $lookback) " +
                "|> filter(fn: (r) => r[\"_measurement\"] == \"configuration_error\") " +
                "|> filter(fn: (r) => r[\"guild_id\"] == \"$guildID\")"
        return influxQueryApi.query(query, ConfigurationErrorWithTimeData::class.java).sortedByDescending { it.instant?.toEpochMilli() }
    }

    fun save(guildId: String, configurationErrorData: ConfigurationErrorData) {
        val point = Point("configuration_error")
            .addTag("guild_id", guildId)
            .addField("description", configurationErrorData.description)

        influxWriteApi.writePoint(point)
    }

    fun clear(guildId: String) {
        val query = "from(bucket:\"${influxConfig.bucket}\") " +
                "|> filter(fn: (r) => r[\"_measurement\"] == \"configuration_error\") " +
                "|> filter(fn: (r) => r[\"guild_id\"] == \"$guildId\") " +
                "|> delete()"

        influxQueryApi.query(query)
    }
}