package dev.warvdine.qotddiscordbot.paralleldots

import dev.warvdine.qotddiscordbot.logging.Logging
import dev.warvdine.qotddiscordbot.logging.getLogger
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.paralleldots.paralleldots.App as ParallelDotsClient

data class SentimentAnalysis(
    val text: String,
    val negative: Double,
    val neutral: Double,
    val positive: Double,
    val sentiment: String,
)

class SentimentAnalysisController(
    private val parallelDotsClient: ParallelDotsClient,
) : Logging {

    private val logger = getLogger()

    private fun getSentiment(negative: Double, neutral: Double, positive: Double): String {
        if (negative > neutral && negative > positive) return "negative sentiment"
        if (neutral > negative && neutral > positive) return "neutral sentiment"
        if (positive > neutral && positive > negative) return "positive sentiment"
        else return "unknown sentiment"
    }

    fun sentimentAnalysis(
        listToAnalyze: List<String>,
    ): List<SentimentAnalysis> {
        logger.info("Analyzing sentiment: {}", listToAnalyze)
        val parser = JSONParser()

        // I'm not sure why JSONArray(listToAnalyze) doesn't work, since I see it in several examples online.
        val stringOfListToAnalyze: String = JSONArray.toJSONString(listToAnalyze)
        val jsonArrayToAnalyze: JSONArray = parser.parse(stringOfListToAnalyze) as JSONArray

        val sentimentResponse: String = parallelDotsClient.sentiment_batch(jsonArrayToAnalyze)
        val sentimentResponseMap: Map<*, *> = parser.parse(sentimentResponse) as Map<*, *>
        val listOfSentimentObjects: List<*> = sentimentResponseMap["sentiment"] as List<*>

        return listOfSentimentObjects.mapIndexed { idx: Int, sentimentAnalysisWithoutText ->
            val positiveSentimentPercentage = ((sentimentAnalysisWithoutText as JSONObject)["positive"] as Double) * 100
            val neutralSentimentPercentage = (sentimentAnalysisWithoutText["neutral"] as Double) * 100
            val negativeSentimentPercentage = (sentimentAnalysisWithoutText["negative"] as Double) * 100
            val overallSentiment = getSentiment(
                positive = positiveSentimentPercentage,
                neutral = neutralSentimentPercentage,
                negative = negativeSentimentPercentage
            )

            SentimentAnalysis(
                text = listToAnalyze[idx],
                positive = positiveSentimentPercentage,
                neutral = neutralSentimentPercentage,
                negative = negativeSentimentPercentage,
                sentiment = overallSentiment
            )
        }
    }
}

fun main() {
    val parallelDotsClient = ParallelDotsClient(System.getenv("parallel_docs_api_key"))
    val sentimentAnalysisController = SentimentAnalysisController(
        parallelDotsClient = parallelDotsClient
    )

    val listToCheckSentiments = listOf(
        "I think we should have meme mondays",
        "i like qotd ☺️\nive been doing my best to keep up with it everyday!!",
        "I've been enjoying these questions. I can't always keep up with convos in general so it's nice to just answer a question and get back to work lol I do think you should be answering these too @Devin.io/Josh's bestie FOR LIFE 👀",
    )

    val sentimentResponse: List<SentimentAnalysis> = sentimentAnalysisController.sentimentAnalysis(listToCheckSentiments)

    sentimentResponse.forEach { println(it) }
}