package space.astro.shared.core.components.io

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class DataSerializer(val objectMapper: ObjectMapper) {

    fun serializeData(clazz: Any): String {
        return objectMapper.writeValueAsString(clazz)
    }
}
