package uber.kidinov.flickrloader.loader.model

import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import uber.kidinov.flickrloader.common.config.Configuration

class PhotosMapperImplTest {
    private lateinit var mapper: PhotosMapper

    companion object {
        private val testPhotos = Photos(
            1, 10, listOf(
                Photo(1, "https://farm66.static.flickr.com/65535/1_b121d0fde4.jpg"),
                Photo(2, "https://farm66.static.flickr.com/65535/2_8bbb3a72bb.jpg")
            )
        )

        private val testJson = "{\n" +
                "  \"photos\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pages\": 10,\n" +
                "    \"photo\": [\n" +
                "      {\n" +
                "        \"id\": \"1\",\n" +
                "        \"secret\": \"b121d0fde4\",\n" +
                "        \"server\": \"65535\",\n" +
                "        \"farm\": 66\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"2\",\n" +
                "        \"secret\": \"8bbb3a72bb\",\n" +
                "        \"server\": \"65535\",\n" +
                "        \"farm\": 66\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}"
    }

    @Before
    fun init() {
        mapper = PhotosMapperImpl(Configuration)
    }

    @Test
    fun `on jsonToPhotos Should convert json to photos`() {
        //WHEN
        val photos = mapper.jsonToPhotos(testJson)

        //THEN
        photos shouldEqual testPhotos
    }
}