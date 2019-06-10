package uber.kidinov.flickrloader.common.network

import java.net.HttpURLConnection
import java.net.URL

interface NetworkExecutor {
    fun executeGetRequest(url: String): Result<String>
}

class NetworkExecutorImpl : NetworkExecutor {
    override fun executeGetRequest(url: String) = runCatching { doGet(url) }

    private fun doGet(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        return connection.inputStream.reader().readText().also { connection.disconnect() }
    }
}