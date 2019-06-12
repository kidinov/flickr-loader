package uber.kidinov.flickrloader.loader.model

import java.io.Serializable

data class Photo(
    val id: Long,
    val url: String
) : Serializable