package dev.warvdine.qotddiscordbot

import dev.warvdine.qotddiscordbot.paralleldots.SentimentAnalysisController
import dev.warvdine.qotddiscordbot.utils.LoggingTestAppender
import io.kotest.assertions.any
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.json.simple.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.paralleldots.paralleldots.App as ParallelDotsClient

class SentimentAnalysisTest : DescribeSpec ({

    lateinit var sentimentAnalysisController: SentimentAnalysisController
    lateinit var mockParallelDotsClient: ParallelDotsClient
    lateinit var mockParallelDotsSentimentResponse: String
    val testList = listOf("Amazing!", "Not bad...", "Pretty cool")

    beforeEach {
        mockParallelDotsClient = mockk(relaxed = true)
        mockParallelDotsSentimentResponse = JSONObject.toJSONString(mapOf(
            "sentiment" to testList.map {
                mapOf("negative" to 0.023f, "neutral" to 0.00f, "positive" to 0.54f)
            }
        ))
        every { mockParallelDotsClient.sentiment_batch(any()) } returns mockParallelDotsSentimentResponse
        sentimentAnalysisController = SentimentAnalysisController(mockParallelDotsClient)
    }

    describe("sentimentAnalysis()") {

        it("should return list with same size as input list") {
            val response = sentimentAnalysisController.sentimentAnalysis(testList)

            response.size shouldBe testList.size
        }

        it("should call parallel dots client") {
            sentimentAnalysisController.sentimentAnalysis(testList)

            verify(exactly = 1) { mockParallelDotsClient.sentiment_batch(any()) }
        }

        it("should include input text in response") {
            val response = sentimentAnalysisController.sentimentAnalysis(testList)

            assertSoftly {
                response.forEach { sentimentAnalysis ->
                    testList shouldContain sentimentAnalysis.text
                }
            }
        }

        it("should log the list of stings to analyze") {
            // prepare logging context
            val loggingTestAppender = LoggingTestAppender()
            val logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
            logger.addAppender(loggingTestAppender)
            loggingTestAppender.start()

            sentimentAnalysisController.sentimentAnalysis(testList)

            val lastLoggedEvent = loggingTestAppender.getLastLoggedEvent()
            assertSoftly {
                lastLoggedEvent shouldNotBe null
                lastLoggedEvent?.message shouldNotBe null
                lastLoggedEvent?.argumentArray shouldNotBe null
                lastLoggedEvent!!.argumentArray shouldContain testList
            }
        }
    }
})