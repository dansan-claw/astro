package space.astro.shared.core.components.mongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import org.litote.kmongo.KMongo
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

/**
 * Factory for [MongoClient] and [MongoDatabase]
 */
@Component
class MongoFactory {

    @Bean
    fun getMongoClient(mongoConfig: MongoConfig): MongoClient {
        return KMongo.createClient(mongoConfig.connectionString)
    }

    @Bean
    fun getMongoDatabase(client: MongoClient, config: MongoConfig): MongoDatabase {
        return client.getDatabase(config.database)
    }
}