package uber.kidinov.flickrloader.common.network

import uber.kidinov.flickrloader.common.config.Configuration

interface Api {
    fun getPhotos(page: Int, query: String): Result<String>
}

class FlickrApi(
    private val executor: NetworkExecutor,
    private val config: Configuration
) : Api {
    override fun getPhotos(page: Int, query: String): Result<String> =
        executor.executeGetRequest(config.BASE_URL.buildPhotoUrl(page, query))

    private fun String.buildPhotoUrl(page: Int, query: String) =
        this.plus("&text=$query").plus("&page=$page")
}