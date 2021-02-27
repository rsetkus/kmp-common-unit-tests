package lt.setkus.multiplatform.repository

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class Repository {

    val httpClient = HttpClient {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(JsonFeature) {
            serializer = KotlinxSerializer(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    inline fun <reified T : Any> request(
        url: String,
        methodType: HttpMethod = HttpMethod.Get,
        crossinline callback: (Consequence<T>) -> Unit = {}
    ): Job {
        return GlobalScope.launch {
            val result = try {
                val data: T = httpClient.request(url) {
                    this.method = methodType
                }
                Consequence(data)
            } catch (e: Exception) {
                Consequence.failure(e)
            }

            callback(result)
        }
    }
}