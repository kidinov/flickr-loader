package uber.kidinov.flickrloader.loader.model

import org.json.JSONObject
import uber.kidinov.flickrloader.common.config.PER_PAGE

interface PhotosMapper {
    fun jsonToPhotos(json: String): Photos
}

class FlickrMapperImpl : PhotosMapper {
    private val pictureUrlTemplate = "https://farm%d.static.flickr.com/%s/%s_%s.jpg"

    override fun jsonToPhotos(json: String): Photos {
        val obj = JSONObject(json).getJSONObject("photos")
        val photoList = obj.getJSONArray("photo").run {
            ArrayList<Photo>(length()).apply {
                repeat(length()) {
                    val photoObject = getJSONObject(it)
                    add(Photo(photoObject.getLong("id"), photoObject.buildPictureUrl()))
                }
            }
        }
        return Photos(
            obj.getInt("page"),
            photoList
        )
    }

    private fun JSONObject.buildPictureUrl() =
        pictureUrlTemplate.format(
            getInt("farm"),
            getString("server"),
            getString("id"),
            getString("secret")
        )
}

class GiphyMapperImpl : PhotosMapper {
    override fun jsonToPhotos(json: String): Photos {
        val obj = JSONObject(json)
        val photoList = obj.getJSONArray("data").run {
            ArrayList<Photo>(length()).apply {
                repeat(length()) {
                    val photoObject = getJSONObject(it)
                    add(
                        Photo(
                            photoObject.getLong("id"),
                            photoObject.getString("url")
                        )
                    )
                }
            }
        }

        val pagination = obj.getJSONObject("pagination")
        return Photos(
            pagination.getInt("total_count") / Integer.parseInt(PER_PAGE),
            photoList
        )
    }

}