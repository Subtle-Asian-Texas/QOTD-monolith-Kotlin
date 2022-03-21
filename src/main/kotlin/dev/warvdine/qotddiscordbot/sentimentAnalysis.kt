package dev.warvdine.qotddiscordbot

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import com.paralleldots.paralleldots.App as ParallelDotsClient

val PARALLEL_DOTS_CLIENT = ParallelDotsClient(System.getenv("parallel_docs_api_key"))

data class SentimentAnalysis(
    val text: String,
    val negative: Double,
    val neutral: Double,
    val positive: Double,
    val sentiment: String = getSentiment(negative, neutral, positive)
)

fun getSentiment(negative: Double, neutral: Double, positive: Double): String {
    if (negative > neutral && negative > positive) return "negative sentiment"
    if (neutral > negative && neutral > positive) return "neutral sentiment"
    if (positive > neutral && positive > negative) return "positive sentiment"
    else return "unknown sentiment"
}

fun sentimentAnalysis(
    listToAnalyze: List<String>,
    parallelDotsClient: ParallelDotsClient = PARALLEL_DOTS_CLIENT
): List<SentimentAnalysis> {
    val parser = JSONParser()

    // I'm not sure why JSONArray(listToAnalyze) doesn't work, since I see it in several examples online.
    val stringOfListToAnalyze: String = JSONArray.toJSONString(listToAnalyze)
    val jsonArrayToAnalyze: JSONArray = parser.parse(stringOfListToAnalyze) as JSONArray

    val sentimentResponse: String = parallelDotsClient.sentiment_batch(jsonArrayToAnalyze)
    val sentimentResponseMap: Map<*, *> = parser.parse(sentimentResponse) as Map<*, *>
    val listOfSentimentObjects: List<*> = sentimentResponseMap["sentiment"] as List<*>

    return listOfSentimentObjects.mapIndexed { idx: Int, sentimentAnalysisWithoutText ->
        SentimentAnalysis(
            text = listToAnalyze[idx],
            positive = ((sentimentAnalysisWithoutText as JSONObject)["positive"] as Double) * 100,
            neutral = (sentimentAnalysisWithoutText["neutral"] as Double) * 100,
            negative = (sentimentAnalysisWithoutText["negative"] as Double) * 100
        )
    }
}

fun main() {
    val sentimentResponse: List<SentimentAnalysis> = sentimentAnalysis(listOf(
        "I think we should have meme mondays",
        "i like qotd ☺️\nive been doing my best to keep up with it everyday!!",
        "I've been enjoying these questions. I can't always keep up with convos in general so it's nice to just answer a question and get back to work lol I do think you should be answering these too @Devin.io/Josh's bestie FOR LIFE 👀",
    ))

    sentimentResponse.forEach { println(it) }
}