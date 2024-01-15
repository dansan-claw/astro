package space.astro.shared.core.models.influx

import com.influxdb.annotations.Column
import com.influxdb.annotations.Measurement
import java.time.Instant

@Measurement(name = "configuration_error")
class ConfigurationErrorWithTimeData {
    @Column(name = "_value", tag = true)
    val description: String = ""

    @Column(name = "time")
    val instant: Instant? = null
}