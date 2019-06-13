package uber.kidinov.flickrloader.loader.model

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Before
import org.junit.Test
import uber.kidinov.flickrloader.common.network.Api
import uber.kidinov.flickrloader.common.util.AsyncImpl
import uber.kidinov.flickrloader.loader.LoaderContract
import uber.kidinov.flickrloader.loader.util.currentHandlerWrapper
import uber.kidinov.flickrloader.loader.util.currentThreadExecutorService


class LoaderRepoTest {
    private val api: Api = mock()
    private val mapper: PhotosMapper = mock()
    private val async = AsyncImpl(currentHandlerWrapper(), currentThreadExecutorService())
    private lateinit var repo: LoaderContract.Repo

    companion object {
        const val TEST_QUERY = "kittens"
        const val TEST_RESPONSE = "response"

        private val testPhotosResponse = Photos(
            2, 10, listOf(
                Photo(3, "https://flickr.com/3"),
                Photo(3, "https://flickr.com/4")
            )
        )
    }

    @Before
    fun init() {
        repo = LoaderRepo(api, mapper, async)
    }

    @Test
    fun `on fetchPhotoUrls with success Should return success`() {
        //GIVEN
        val page = 0
        whenever(api.getPhotos(page, TEST_QUERY)).thenReturn(Result.success(TEST_RESPONSE))
        whenever(mapper.jsonToPhotos(TEST_RESPONSE)).thenReturn(testPhotosResponse)

        // WHEN && THEN
        repo.fetchPhotoUrls(0, TEST_QUERY) {
            it.isSuccess shouldEqualTo true
            it.getOrNull() shouldEqual testPhotosResponse
        }
    }

    @Test
    fun `on fetchPhotoUrls with error Should return error`() {
        //GIVEN
        val page = 0
        whenever(api.getPhotos(page, TEST_QUERY)).thenReturn(Result.failure(Exception()))
        whenever(mapper.jsonToPhotos(TEST_RESPONSE)).thenReturn(testPhotosResponse)

        // WHEN && THEN
        repo.fetchPhotoUrls(0, TEST_QUERY) {
            it.isSuccess shouldEqualTo false
        }
    }
}