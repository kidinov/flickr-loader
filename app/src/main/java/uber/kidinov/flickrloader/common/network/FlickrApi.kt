package uber.kidinov.flickrloader.common.network

import android.util.Log
import uber.kidinov.flickrloader.common.config.Configuration

interface Api {
    fun getPhotos(page: Int, query: String): Result<String>
}

class FlickrApi(
    private val executor: NetworkExecutor,
    private val config: Configuration
) : Api {
    override fun getPhotos(page: Int, query: String): Result<String> {
        val url = config.BASE_URL.buildPhotoUrl(page, query)
        Log.d("FlickrApi", "Fetching with $url")
        return executor.executeGetRequest(url)
    }

    private fun String.buildPhotoUrl(page: Int, query: String) =
        this.plus("&text=$query").plus("&page=$page")
}