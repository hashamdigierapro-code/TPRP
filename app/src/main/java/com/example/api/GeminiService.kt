package com.example.api

import com.example.BuildConfig
import com.example.data.QuizQuestion
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import okhttp3.Request
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// --- Gemini Request / Response models using Moshi ---

@JsonClass(generateAdapter = true)
data class Part(
    @field:Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @field:Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class ResponseFormatText(
    @field:Json(name = "mimeType") val mimeType: String,
    @field:Json(name = "schema") val schema: Map<String, Any>? = null
)

@JsonClass(generateAdapter = true)
data class ResponseFormat(
    @field:Json(name = "text") val text: ResponseFormatText? = null
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @field:Json(name = "responseFormat") val responseFormat: ResponseFormat? = null,
    @field:Json(name = "temperature") val temperature: Float? = null
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @field:Json(name = "contents") val contents: List<Content>,
    @field:Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @field:Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @field:Json(name = "content") val content: Content
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @field:Json(name = "candidates") val candidates: List<Candidate>? = null
)

// --- Domain schema representation for parsing the generated MCQs ---

@JsonClass(generateAdapter = true)
data class GeneratedMcq(
    @field:Json(name = "question") val question: String,
    @field:Json(name = "optionA") val optionA: String,
    @field:Json(name = "optionB") val optionB: String,
    @field:Json(name = "optionC") val optionC: String,
    @field:Json(name = "optionD") val optionD: String,
    @field:Json(name = "correctAnswer") val correctAnswer: String // MUST be "A", "B", "C", or "D"
)

@JsonClass(generateAdapter = true)
data class GeneratedMcqList(
    @field:Json(name = "questions") val questions: List<GeneratedMcq>
)

// --- Retrofit API Service ---

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val service: GeminiApiService = retrofit.create(GeminiApiService::class.java)

    // Helper to generate custom subject-based quiz using user key or default key
    suspend fun generateMcqs(
        topic: String,
        difficulty: String,
        count: Int = 5,
        customKey: String? = null,
        manualUrl: String? = null
    ): List<QuizQuestion> {
        val systemPrompt = "You are QuizMaster Pro's advanced AI MCQ Generator. " +
                "Your task is to generate MCQs in strict JSON syntax based on the user's requested topic and difficulty level. " +
                "You MUST return a JSON object with a single key 'questions' containing a list of objects. Each object " +
                "MUST contain: 'question' (String), 'optionA' (String), 'optionB' (String), 'optionC' (String), " +
                "'optionD' (String), and 'correctAnswer' (String, which MUST be either 'A', 'B', 'C', or 'D'). " +
                "Any difficulty requested as 'Asian' should feature tricky, culturally witty, or extreme reasoning style academic questions. " +
                "Return raw valid JSON matching this schema, with no markdown styling, no extra text, and no code blocks."

        val promptText = "Generate exactly $count high-quality, smart, and fully engaging MCQs on the topic of '$topic' " +
                "with an overall difficulty level of '$difficulty'. Ensure the options are clever, realistic, and have exactly " +
                "one clearly correct answer."

        if (!manualUrl.isNullOrBlank()) {
            val key = if (!customKey.isNullOrBlank()) customKey else BuildConfig.GEMINI_API_KEY
            val finalTargetUrl = if (manualUrl.contains("chat/completions") || manualUrl.contains("generate")) {
                manualUrl
            } else {
                if (manualUrl.endsWith("/")) "${manualUrl}v1/chat/completions" else "$manualUrl/v1/chat/completions"
            }

            val safeSystemPrompt = systemPrompt.replace("\"", "\\\"").replace("\n", " ")
            val safePromptText = promptText.replace("\"", "\\\"").replace("\n", " ")

            val jsonBody = "{\n  \"model\": \"gpt-4o-mini\",\n  \"messages\": [\n    {\"role\": \"system\", \"content\": \"$safeSystemPrompt\"},\n    {\"role\": \"user\", \"content\": \"$safePromptText\"}\n  ],\n  \"response_format\": {\"type\": \"json_object\"},\n  \"temperature\": 0.8\n}"

            val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())

            val requestBuilder = Request.Builder()
                .url(finalTargetUrl)
                .post(requestBody)

            if (!key.isBlank() && key != "MY_GEMINI_API_KEY") {
                requestBuilder.addHeader("Authorization", "Bearer $key")
            }

            val callRequest = requestBuilder.build()
            
            val response = suspendCancellableCoroutine<Response> { continuation ->
                val call = okHttpClient.newCall(callRequest)
                continuation.invokeOnCancellation { call.cancel() }
                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: java.io.IOException) {
                        continuation.resumeWith(Result.failure(e))
                    }
                    override fun onResponse(call: Call, response: Response) {
                        continuation.resume(response)
                    }
                })
            }

            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: ""
                throw IllegalStateException("API error ${response.code}: $errorBody")
            }

            val bodyString = response.body?.string() ?: throw IllegalStateException("Response body is empty")
            
            val rootJson = JSONObject(bodyString)
            val choices = rootJson.optJSONArray("choices")
            val rawJsonContent = if (choices != null && choices.length() > 0) {
                choices.getJSONObject(0).getJSONObject("message").getString("content")
            } else {
                bodyString
            }

            val adapter = moshi.adapter(GeneratedMcqList::class.java)
            val mcqList = adapter.fromJson(rawJsonContent) 
                ?: throw IllegalStateException("Failed to parse custom AI JSON: $rawJsonContent")

            return mcqList.questions.map { g ->
                QuizQuestion(
                    subject = topic,
                    questionText = g.question,
                    optionA = g.optionA,
                    optionB = g.optionB,
                    optionC = g.optionC,
                    optionD = g.optionD,
                    correctAnswer = g.correctAnswer.trim().uppercase(),
                    difficulty = difficulty,
                    isCustom = true
                )
            }
        }

        val apiKey = if (!customKey.isNullOrBlank()) customKey else BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            throw IllegalArgumentException("API Key is missing or invalid. Set it up in the Secrets Panel.")
        }

        // Configure the JSON format schema for safety with Type constraints
        val jsonSchema = mapOf(
            "type" to "OBJECT",
            "properties" to mapOf(
                "questions" to mapOf(
                    "type" to "ARRAY",
                    "items" to mapOf(
                        "type" to "OBJECT",
                        "properties" to mapOf(
                            "question" to mapOf("type" to "STRING"),
                            "optionA" to mapOf("type" to "STRING"),
                            "optionB" to mapOf("type" to "STRING"),
                            "optionC" to mapOf("type" to "STRING"),
                            "optionD" to mapOf("type" to "STRING"),
                            "correctAnswer" to mapOf("type" to "STRING", "description" to "Must be A, B, C, or D")
                        ),
                        "required" to listOf("question", "optionA", "optionB", "optionC", "optionD", "correctAnswer")
                    )
                )
            ),
            "required" to listOf("questions")
        )

        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(text = promptText)))),
            generationConfig = GenerationConfig(
                responseFormat = ResponseFormat(
                    text = ResponseFormatText(
                        mimeType = "application/json",
                        schema = jsonSchema
                    )
                ),
                temperature = 0.8f
            ),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
        )

        val response = service.generateContent(apiKey, request)
        val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw IllegalStateException("Gemini returned an empty response")

        // Parse list of generated questions from JSON
        val adapter = moshi.adapter(GeneratedMcqList::class.java)
        val mcqList = adapter.fromJson(responseText) ?: throw IllegalStateException("Failed to parse JSON response: $responseText")

        return mcqList.questions.map { g ->
            QuizQuestion(
                subject = topic,
                questionText = g.question,
                optionA = g.optionA,
                optionB = g.optionB,
                optionC = g.optionC,
                optionD = g.optionD,
                correctAnswer = g.correctAnswer.trim().uppercase(),
                difficulty = difficulty,
                isCustom = true
            )
        }
    }
}
