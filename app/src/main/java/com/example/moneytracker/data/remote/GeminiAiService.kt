package com.example.moneytracker.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiAiService @Inject constructor() {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val apiKey = "AIzaSyCUl8XLGJqAAYapqspsOwBDXGwo8pxR7Mg"
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"
    
    suspend fun generateFinancialInsights(
        totalIncome: Double,
        totalExpense: Double,
        balance: Double,
        topExpenseCategory: String?,
        transactionCount: Int,
        monthYear: String
    ): List<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = buildPrompt(totalIncome, totalExpense, balance, topExpenseCategory, transactionCount, monthYear)
            
            val requestBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.8)
                    put("topK", 40)
                    put("topP", 0.95)
                    put("maxOutputTokens", 1024)
                })
            }
            
            val request = Request.Builder()
                .url("$baseUrl?key=$apiKey")
                .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                parseGeminiResponse(responseBody)
            } else {
                // Thử lại với prompt đơn giản hơn
                generateSimpleFallbackInsights(totalIncome, totalExpense, balance, topExpenseCategory)
            }
        } catch (e: Exception) {
            // Thử lại với prompt đơn giản hơn
            generateSimpleFallbackInsights(totalIncome, totalExpense, balance, topExpenseCategory)
        }
    }
    
    private fun buildPrompt(
        totalIncome: Double,
        totalExpense: Double,
        balance: Double,
        topExpenseCategory: String?,
        transactionCount: Int,
        monthYear: String
    ): String {
        return """
            Bạn là một chuyên gia tài chính hài hước và dí dỏm. Hãy phân tích dữ liệu tài chính sau và đưa ra 3-4 nhận xét vui nhộn, sâu sắc bằng tiếng Việt:
            
            Dữ liệu tháng $monthYear:
            - Thu nhập: ${totalIncome.toInt()} VND
            - Chi tiêu: ${totalExpense.toInt()} VND  
            - Số dư: ${balance.toInt()} VND
            - Danh mục chi tiêu nhiều nhất: ${topExpenseCategory ?: "Không có"}
            - Tổng số giao dịch: $transactionCount
            
            Yêu cầu:
            1. Mỗi insight dài 8 từ
            2. Sử dụng ngôn ngữ hài hước, dí dỏm nhưng vẫn có ý nghĩa
            3. Có thể sử dụng emoji phù hợp
            4. Đưa ra lời khuyên thực tế nhưng vui nhộn
            5. Phản ánh đúng tình hình tài chính (tích cực/tiêu cực)
            
            Trả về chỉ 3 câu insight, mỗi câu trên một dòng, không có số thứ tự hay ký hiệu đặc biệt.
        """.trimIndent()
    }
    
    private fun parseGeminiResponse(responseBody: String?): List<String> {
        return try {
            val jsonResponse = JSONObject(responseBody ?: "")
            val candidates = jsonResponse.getJSONArray("candidates")
            val content = candidates.getJSONObject(0)
                .getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val text = parts.getJSONObject(0).getString("text")
            
            // Parse the response and split into individual insights
            val insights = text.split("\n")
                .filter { it.trim().isNotEmpty() }
                .map { it.trim() }
                .take(4)
            
            insights
        } catch (e: Exception) {
            listOf("Hôm nay AI đang nghỉ phép, hẹn bạn lần sau! 🤖✨")
        }
    }
    
    private suspend fun generateSimpleFallbackInsights(
        totalIncome: Double,
        totalExpense: Double,
        balance: Double,
        topExpenseCategory: String?
    ): List<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Prompt siêu đơn giản cho fallback
            val simpleFallbackPrompt = """
                Viết 2-3 câu ngắn và vui về tài chính:
                Thu nhập: ${totalIncome.toInt()} VND
                Chi tiêu: ${totalExpense.toInt()} VND
                Số dư: ${balance.toInt()} VND
                
                Mỗi câu 8-12 từ, có emoji, vui vẻ.
            """.trimIndent()
            
            val requestBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", simpleFallbackPrompt)
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 200)
                })
            }
            
            val request = Request.Builder()
                .url("$baseUrl?key=$apiKey")
                .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                parseGeminiResponse(responseBody)
            } else {
                // Nếu thất bại hoàn toàn, ít nhất vẫn có 1 câu AI tối thiểu
                generateMinimalAiInsight(balance)
            }
        } catch (e: Exception) {
            // Cuối cùng, dùng AI với prompt tối thiểu
            generateMinimalAiInsight(balance)
        }
    }
    
    private suspend fun generateMinimalAiInsight(balance: Double): List<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val minimalPrompt = if (balance > 0) {
                "Viết 1 câu vui về việc có tiền dư: ${balance.toInt()} VND"
            } else {
                "Viết 1 câu động viên vui về việc hết tiền"
            }
            
            val requestBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", minimalPrompt)
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.8)
                    put("maxOutputTokens", 50)
                })
            }
            
            val request = Request.Builder()
                .url("$baseUrl?key=$apiKey")
                .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                parseGeminiResponse(responseBody)
            } else {
                // Thất bại hoàn toàn - chỉ 1 câu tĩnh duy nhất
                listOf("AI đang nghỉ, nhưng bạn vẫn tuyệt vời! ✨😊")
            }
        } catch (e: Exception) {
            // Câu cuối cùng khi mọi thứ đều thất bại
            listOf("Kết nối AI có vấn đề, nhưng tinh thần vẫn ổn! 💪🚀")
        }
    }
    
    suspend fun generateCustomInsights(customPrompt: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val requestBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", customPrompt)
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.9)
                    put("topK", 40)
                    put("topP", 0.95)
                    put("maxOutputTokens", 512)
                })
            }
            
            val request = Request.Builder()
                .url("$baseUrl?key=$apiKey")
                .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                parseGeminiResponse(responseBody)
            } else {
                listOf("AI đang bận, thử lại sau nhé! 🤖💭")
            }
        } catch (e: Exception) {
            listOf("Lỗi kết nối AI, hãy kiểm tra mạng! 📶❌")
        }
    }
}
