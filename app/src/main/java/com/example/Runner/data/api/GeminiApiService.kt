package com.example.Runner.data.api

import com.example.Runner.BuildConfig
import com.example.Runner.data.model.Route
import com.example.Runner.data.repository.ClaudeResponse
import com.example.Runner.data.repository.ClaudeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

interface GeminiApi {
    @POST("v1beta/models/gemini-1.5-flash-latest:generateContent")
    suspend fun generateContent(
        @Body request: GeminiRequest,
        @Query("key") apiKey: String
    ): Response<GeminiResponse>
}

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent,
    @SerialName("finishReason") val finishReason: String? = null
)

@Singleton
class GeminiApiService @Inject constructor(
    private val geminiApi: GeminiApi
) : ClaudeService {

    override suspend fun generateRouteDescription(route: Route): ClaudeResponse {
        return withContext(Dispatchers.IO) {
            try {
                // Create prompt for Gemini
                val prompt = createRoutePrompt(route)
                
                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(
                                GeminiPart(text = prompt)
                            )
                        )
                    )
                )

                val response = geminiApi.generateContent(
                    request = request,
                    apiKey = BuildConfig.GEMINI_API_KEY
                )

                if (response.isSuccessful && response.body() != null) {
                    val geminiResponse = response.body()!!
                    val generatedText = geminiResponse.candidates.firstOrNull()
                        ?.content?.parts?.firstOrNull()?.text
                    
                    if (generatedText != null) {
                        parseGeminiResponse(generatedText)
                    } else {
                        createFallbackResponse(route)
                    }
                } else {
                    createFallbackResponse(route)
                }
            } catch (e: Exception) {
                createFallbackResponse(route)
            }
        }
    }

    private fun createRoutePrompt(route: Route): String {
        return """
            You are a running coach helping describe jogging routes. Please provide a short, engaging description of this running route and a catchy label.

            Route Details:
            - Distance: ${String.format("%.1f", route.distance)} km
            - Duration: ${route.duration} minutes
            - Elevation gain: ${route.elevation} meters
            - Difficulty: ${route.difficulty.name.lowercase()}
            - Type: ${if (route.isLoop) "Loop route" else "Point-to-point route"}

            Please respond in this exact format:
            LABEL: [A short, catchy name for the route (2-4 words)]
            DESCRIPTION: [An engaging 1-2 sentence description highlighting what makes this route special]

            Examples:
            LABEL: Scenic Park Loop
            DESCRIPTION: A beautiful route through tree-lined paths with gentle hills and peaceful surroundings, perfect for a relaxing run.

            LABEL: Urban Explorer
            DESCRIPTION: Navigate through vibrant city streets with interesting architecture and urban energy to keep you motivated.

            Keep it positive, motivating, and focus on what runners would enjoy about this route.
        """.trimIndent()
    }

    private fun parseGeminiResponse(generatedText: String): ClaudeResponse {
        return try {
            val lines = generatedText.split("\n").map { it.trim() }
            
            var label = ""
            var description = ""
            
            for (line in lines) {
                when {
                    line.startsWith("LABEL:", ignoreCase = true) -> {
                        label = line.removePrefix("LABEL:").removePrefix("label:").trim()
                    }
                    line.startsWith("DESCRIPTION:", ignoreCase = true) -> {
                        description = line.removePrefix("DESCRIPTION:").removePrefix("description:").trim()
                    }
                }
            }
            
            // Fallback if parsing failed
            if (label.isBlank() || description.isBlank()) {
                return createSmartFallbackResponse(generatedText)
            }
            
            ClaudeResponse(
                description = description,
                label = label
            )
        } catch (e: Exception) {
            createSmartFallbackResponse(generatedText)
        }
    }

    private fun createSmartFallbackResponse(generatedText: String): ClaudeResponse {
        // Try to extract useful information even if format is wrong
        val words = generatedText.split(" ", "\n").filter { it.isNotBlank() }
        
        val label = when {
            words.size >= 2 -> "${words[0]} ${words[1]}"
            words.isNotEmpty() -> words[0]
            else -> "Great Route"
        }
        
        val description = if (generatedText.length > 50) {
            generatedText.take(150).split(".").firstOrNull()?.plus(".") ?: generatedText.take(100)
        } else {
            "A wonderful running route with varied terrain and interesting features."
        }
        
        return ClaudeResponse(
            description = description,
            label = label
        )
    }

    private fun createFallbackResponse(route: Route): ClaudeResponse {
        // Enhanced fallback responses based on route characteristics
        val labels = when {
            route.elevation < 30 -> listOf("Flat Runner", "Easy Cruise", "Smooth Path", "Level Ground")
            route.elevation > 100 -> listOf("Hill Challenge", "Summit Quest", "Peak Explorer", "Mountain Trail")
            route.distance < 2.0 -> listOf("Quick Sprint", "Short Burst", "Power Run", "Express Route")
            route.distance > 8.0 -> listOf("Long Haul", "Distance Challenge", "Endurance Test", "Marathon Prep")
            else -> listOf("Perfect Balance", "Classic Route", "Runner's Choice", "Ideal Path")
        }
        
        val descriptions = when {
            route.elevation < 30 && route.distance < 3.0 -> 
                listOf("A gentle, flat route perfect for beginners or recovery runs with minimal elevation changes.")
            route.elevation > 100 -> 
                listOf("Challenge yourself with this hilly route that offers great views and a solid workout for your legs.")
            route.distance > 7.0 -> 
                listOf("A longer route for serious runners looking to build endurance and explore more of the area.")
            route.difficulty.name == "EASY" -> 
                listOf("An easy-going route with pleasant scenery and comfortable pacing for all fitness levels.")
            else -> 
                listOf("A well-balanced route offering a mix of terrain and challenges to keep your run interesting.")
        }
        
        return ClaudeResponse(
            description = descriptions.random(),
            label = labels.random()
        )
    }
}