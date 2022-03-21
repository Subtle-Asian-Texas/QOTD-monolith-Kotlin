package dev.warvdine.qotddiscordbot

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.json.simple.JSONObject
import com.paralleldots.paralleldots.App as ParallelDotsClient

class SentimentAnalysisTest : DescribeSpec ({

    describe("getSentiment function") {

        forAll(
            table(
                headers("negativeValue", "neutralValue", "positiveValue", "expectedSentiment"),
                row(1.0, 2.0, 3.0, "positive"),
                row(1.0, 3.0, 2.0, "neutral"),
                row(3.0, 2.0, 1.0, "negative"),
                row(1.0, 1.0, 1.0, "unknown"),
            )
        ) { negativeValue, neutralValue, positiveValue, expectedSentiment ->
            it("should return $expectedSentiment when $expectedSentiment value is greater") {
                val sentimentString: String = getSentiment(
                    negative = negativeValue,
                    neutral = neutralValue,
                    positive = positiveValue
                )
                sentimentString shouldContain expectedSentiment
            }
        }

    }

    describe("sentimentAnalysis function") {

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

        }

        it("should return list with same size as input list") {
            every { mockParallelDotsClient.sentiment_batch(any()) } returns mockParallelDotsSentimentResponse

            val response = sentimentAnalysis(testList, mockParallelDotsClient)

            response.size shouldBe testList.size
        }

        it("should call parallel dots client") {
            every { mockParallelDotsClient.sentiment_batch(any()) } returns mockParallelDotsSentimentResponse

            sentimentAnalysis(testList, mockParallelDotsClient)

            verify(exactly = 1) { mockParallelDotsClient.sentiment_batch(any()) }
        }

        it("should include input text in response") {
            every { mockParallelDotsClient.sentiment_batch(any()) } returns mockParallelDotsSentimentResponse

            val response = sentimentAnalysis(testList, mockParallelDotsClient)

            assertSoftly {
                response.forEach { sentimentAnalysis ->
                    testList shouldContain sentimentAnalysis.text
                }
            }
        }
    }
})