package dev.warvdine.qotddiscordbot;

import com.mongodb.ConnectionString
import dev.warvdine.qotddiscordbot.paralleldots.SentimentAnalysisController
import com.paralleldots.paralleldots.App as ParallelDotsClient
import discord4j.core.DiscordClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InjectionPoint
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope

@Configuration
@PropertySource("classpath:application.properties")
open class AppConfig(
    @Value("\${discord_bot_api_key}") private val discordBotApiKey: String,
    @Value("\${parallel_docs_api_key}") private val parallelDocsApiKey: String,
    @Value("\${mongodb.username}") private val mongoDBUsername: String,
    @Value("\${mongodb.password}") private val mongoDBPassword: String,
    @Value("\${mongodb.database_name}") private val mongoDBDatabaseName: String,
) {

    @Bean
    open fun parallelDotsClient() = ParallelDotsClient(parallelDocsApiKey)

    @Bean
    open fun discordClient(): DiscordClient = DiscordClient.create(discordBotApiKey)

    @Bean
    open fun kMongoDatabaseClient(): CoroutineDatabase {
        val connectionString = "mongodb+srv://$mongoDBUsername:$mongoDBPassword@qotd-cluster.edga1.mongodb.net/$mongoDBDatabaseName?retryWrites=true&w=majority"
        return KMongo
            .createClient(ConnectionString(connectionString))
            .coroutine
            .getDatabase(mongoDBDatabaseName)
    }

    @Bean
    open fun qotdService(): QotdService {
        return QotdService()
    }

    @Bean
    open fun sentimentAnalysisController(
        parallelDotsClient: ParallelDotsClient,
    ): SentimentAnalysisController {
        return SentimentAnalysisController(
            parallelDotsClient = parallelDotsClient
        )
    }

}
