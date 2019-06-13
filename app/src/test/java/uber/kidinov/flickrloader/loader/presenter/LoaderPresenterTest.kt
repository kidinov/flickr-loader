package uber.kidinov.flickrloader.loader.presenter

import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.amshove.kluent.any
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldHaveSize
import org.junit.Before
import org.junit.Test
import uber.kidinov.flickrloader.loader.LoaderContract
import uber.kidinov.flickrloader.loader.model.Photo
import uber.kidinov.flickrloader.loader.model.Photos

class LoaderPresenterTest {
    private val view: LoaderContract.View = mock()
    private val repo: LoaderContract.Repo = mock()
    private val itemView: LoaderContract.ItemView = mock()
    private lateinit var presenter: LoaderPresenter

    companion object {
        private lateinit var testPhotos: MutableList<Photo>
        private val testPhotosResponse = Photos(
            2, 10, listOf(
                Photo(3, "https://flickr.com/3"),
                Photo(3, "https://flickr.com/4")
            )
        )

        const val TEST_QUERY = "kittens"
    }

    @Before
    fun init() {
        presenter = LoaderPresenter(view, repo)
        testPhotos = mutableListOf(
            Photo(1, "https://flickr.com/1"),
            Photo(2, "https://flickr.com/2")
        )
    }

    @Test
    fun `on onRecreated With pageNum 1 and pages 0 Should showSummary zero to zero and requestListDataChanged`() {
        // GIVEN
        presenter.state.pageNum = 1
        presenter.state.pages = 0

        // WHEN
        presenter.onRecreated()

        // THEN
        verify(view).showSummary(0, 0)
        verify(view).requestListDataChanged()
    }

    @Test
    fun `on onQueryChanged Should reset state and fetch with specified query`() {
        // GIVEN
        presenter.state.pageNum = 10
        presenter.state.pages = 5
        presenter.state.photos = testPhotos

        // WHEN
        presenter.onQueryChanged(TEST_QUERY)

        // THEN
        presenter.state.pageNum shouldEqualTo 0
        presenter.state.pages shouldEqualTo 0
        presenter.state.photos.shouldBeEmpty()
        verify(repo).fetchPhotoUrls(eq(0), eq(TEST_QUERY), any())
    }

    @Test
    fun `on onQueryChanged With success Should set new state and update list`() {
        // GIVEN
        presenter.state.pageNum = 1
        presenter.state.pages = 1
        presenter.state.photos = testPhotos

        whenever(repo.fetchPhotoUrls(eq(0), eq(TEST_QUERY), any())).thenAnswer {
            val completion = it.getArgument<((Result<Photos>) -> Unit)>(2)
            completion.invoke(Result.success(testPhotosResponse))
        }

        // WHEN
        presenter.onQueryChanged(TEST_QUERY)

        // THEN
        presenter.state.pageNum shouldEqualTo testPhotosResponse.page + 1
        presenter.state.pages shouldEqualTo testPhotosResponse.pages
        presenter.state.photos.shouldHaveSize(testPhotosResponse.photos.size)
        verify(view).showSummary(0, 0)
        verify(view).showSummary(testPhotosResponse.page, testPhotosResponse.pages)
        verify(view, times(2)).requestListDataChanged()
    }

    @Test
    fun `on onQueryChanged With error Should showError`() {
        // GIVEN
        presenter.state.pageNum = 1
        presenter.state.pages = 1
        presenter.state.photos = testPhotos

        whenever(repo.fetchPhotoUrls(eq(0), eq(TEST_QUERY), any())).thenAnswer {
            val completion = it.getArgument<((Result<Photos>) -> Unit)>(2)
            completion.invoke(Result.failure(Exception()))
        }

        // WHEN
        presenter.onQueryChanged(TEST_QUERY)

        // THEN
        verify(view).showError(null)
        verify(view, never()).showSummary(testPhotosResponse.page, testPhotosResponse.pages)
    }

    @Test
    fun `on onScrolledDown with page num less than pages Should fetch with current state data`() {
        // GIVEN
        presenter.state.pageNum = 10
        presenter.state.pages = 12

        // WHEN
        presenter.onScrolledDown(TEST_QUERY)

        // THEN
        verify(repo).fetchPhotoUrls(eq(10), eq(TEST_QUERY), any())
    }

    @Test
    fun `on onScrolledDown with page num more than pages Should not fetch data`() {
        // GIVEN
        presenter.state.pageNum = 12
        presenter.state.pages = 11

        // WHEN
        presenter.onScrolledDown(TEST_QUERY)

        // THEN
        verify(repo, never()).fetchPhotoUrls(any(), any(), any())
    }

    @Test
    fun `on getItemId Should return id of item in state photos`() {
        // GIVEN
        val position = 0
        presenter.state.photos = testPhotos

        // WHEN
        val itemIdAt0 = presenter.getItemId(position)

        // THEN
        itemIdAt0 shouldEqualTo testPhotos[position].id
    }

    @Test
    fun `on getCount Should return amount of photos in state`() {
        // GIVEN
        presenter.state.photos = testPhotos

        // WHEN
        val count = presenter.getCount()

        // THEN
        count shouldEqualTo testPhotos.size
    }

    @Test
    fun `on bindPicture Should bind url in position from state photos`() {
        // GIVEN
        val position = 0
        presenter.state.photos = testPhotos

        // WHEN
        presenter.bindPicture(position, itemView)

        // THEN
        verify(itemView).bindPicture(testPhotos[position].url)
    }

    @Test
    fun `on getItem Should return item in position from state photos`() {
        // GIVEN
        val position = 0
        presenter.state.photos = testPhotos

        // WHEN
        val item = presenter.getItem(position)

        // THEN
        item shouldBe testPhotos[position]
    }
}