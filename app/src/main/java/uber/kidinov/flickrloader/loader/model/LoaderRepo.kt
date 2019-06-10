package uber.kidinov.flickrloader.loader.model

import uber.kidinov.flickrloader.common.async.Async
import uber.kidinov.flickrloader.common.network.Api
import uber.kidinov.flickrloader.loader.LoaderContract

class LoaderRepo(
    private val api: Api,
    private val mapper: PhotosMapper,
    private val async: Async
) : LoaderContract.Repo {
    override fun fetchPhotoUrls(page: Int, query: String, result: (Result<Photos>) -> Unit) {
        async.doAsync {
            val photos = api.getPhotos(page, query).mapCatching { mapper.jsonToPhotos(it) }
            async.onUiThread { result(photos) }
        }
    }
}