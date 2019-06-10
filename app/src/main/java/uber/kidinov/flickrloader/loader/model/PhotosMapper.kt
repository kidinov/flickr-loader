package uber.kidinov.flickrloader.loader.model

import org.json.JSONObject
import uber.kidinov.flickrloader.common.config.Configuration

interface PhotosMapper {
    fun jsonToPhotos(json: String): Photos
}

class PhotosMapperImpl(private val config: Configuration) : PhotosMapper {
    override fun jsonToPhotos(json: String): Photos {
        val obj = JSONObject(json).getJSONObject("photos")
        val photoList = obj.getJSONArray("photo").run {
            ArrayList<Photo>(length()).apply {
                repeat(length()) { add(Photo(getJSONObject(it).buildPictureUrl())) }
            }
        }
        return Photos(
            obj.getInt("page"),
            obj.getInt("pages"),
            photoList
        )
    }

    private fun JSONObject.buildPictureUrl() =
        config.pictureUrlTemplate.format(
            getInt("farm"),
            getString("server"),
            getString("id"),
            getString("secret")
        )
}