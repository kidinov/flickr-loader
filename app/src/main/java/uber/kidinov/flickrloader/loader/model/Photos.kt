package uber.kidinov.flickrloader.loader.model

data class Photos(
    val page: Int,
    val pages: Int,
    val photos: List<Photo>
)